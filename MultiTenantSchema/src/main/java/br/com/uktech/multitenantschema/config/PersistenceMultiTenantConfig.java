/*
 * Copyright (C) 2016 Uhlig e Korovsky Tecnologia Ltda - ME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.com.uktech.multitenantschema.config;

import br.com.uktech.multitenantschema.multitenant.ConfigurableMultiTenantConnectionProvider;
import br.com.uktech.multitenantschema.multitenant.CurrentTenantIdentifierResolverImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.hibernate.MultiTenancyStrategy;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 *
 * @author Carlos Alberto Cipriano Korovsky <carlos.korovsky at gmail.com>
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "multiTenantEntityManagerFactory",
        transactionManagerRef = "multiTenantTransactionManager",
        basePackages = { 
    "br.com.uktech.multitenantschema.model.repository",
    "br.com.uktech.multitenantschema.model.shared.repository",
    "br.com.uktech.multitenantschema.model.restricted.repository"
})
public class PersistenceMultiTenantConfig {
    
    private String[] getMultiTenantPackagesToScan() {
        List<String> packages = new ArrayList<>();
        packages.add("br.com.uktech.multitenantschema.model.shared");
        packages.add("br.com.uktech.multitenantschema.model.restricted");
        return packages.toArray(new String[packages.size()]);
    }
    
    private JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }
    
    @Bean
    public MultiTenantConnectionProvider getMultiTenantConnectionProvider(DataSource dataSource) {
        MultiTenantConnectionProvider multiTenantConnectionProvider;
        multiTenantConnectionProvider = new ConfigurableMultiTenantConnectionProvider(dataSource);
        return multiTenantConnectionProvider;
    }

    @Bean
    public CurrentTenantIdentifierResolver getTenantIdentifierResolver() {
        CurrentTenantIdentifierResolver tenantIdentifierResolver;
        tenantIdentifierResolver = new CurrentTenantIdentifierResolverImpl();
        return tenantIdentifierResolver;
    }


    
    @Bean("multiTenantEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean multiTenantEntityManagerFactory(DataSource dataSource,
                                                                       MultiTenantConnectionProvider multiTenantConnectionProvider,
                                                                       CurrentTenantIdentifierResolver tenantIdentifierResolver) {
        
        LocalContainerEntityManagerFactoryBean emfBean = new LocalContainerEntityManagerFactoryBean();
        emfBean.setDataSource(dataSource);
        emfBean.setPackagesToScan(getMultiTenantPackagesToScan());
        emfBean.setJpaVendorAdapter(jpaVendorAdapter());

        Map<String, Object> jpaProperties = new HashMap<>();
        
        jpaProperties.put(org.hibernate.cfg.Environment.SHOW_SQL, "true");
        jpaProperties.put(org.hibernate.cfg.Environment.FORMAT_SQL, "true");
        jpaProperties.put(org.hibernate.cfg.Environment.HBM2DDL_AUTO, "update");
        
        jpaProperties.put(org.hibernate.cfg.Environment.MULTI_TENANT,
                          MultiTenancyStrategy.SCHEMA);
        jpaProperties.put(org.hibernate.cfg.Environment.MULTI_TENANT_CONNECTION_PROVIDER,
                          multiTenantConnectionProvider);
        jpaProperties.put(org.hibernate.cfg.Environment.MULTI_TENANT_IDENTIFIER_RESOLVER,
                          tenantIdentifierResolver);
        emfBean.setJpaPropertyMap(jpaProperties);
        
        return emfBean;
    }
    
    @Bean(name = "multiTenantTransactionManager")
    public PlatformTransactionManager transactionManager(EntityManagerFactory multiTenantEntityManagerFactory) {
        return new JpaTransactionManager(multiTenantEntityManagerFactory);
    }
    
}
