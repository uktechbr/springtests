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

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 *
 * @author Carlos Alberto Cipriano Korovsky <carlos.korovsky at gmail.com>
 */
@Configuration
@EnableAsync
@EnableScheduling
@ComponentScan(basePackages={"br.com.uktech.multitenantschema"}, 
        excludeFilters=@ComponentScan.Filter(
                type=FilterType.REGEX, 
                pattern={"br.com.uktech.multitenantschema.controller.*",
                         "br.com.uktech.multitenantschema.model.*"}))
public class ApplicationConfig implements ApplicationContextAware {
    
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    
    /*
    private MessageSource databaseMessageSource() {
        DatabaseDrivenMessageSource messageSource;
        SystemMessageService systemMessageService;
        systemMessageService = (SystemMessageService) this.applicationContext.getBean("SystemMessageService");
        messageSource = new DatabaseDrivenMessageSource();
        messageSource.setSystemMessageService(systemMessageService);
        return messageSource;
    }
    */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource;
        messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(-1);
        messageSource.setFallbackToSystemLocale(Boolean.FALSE);
        //messageSource.setParentMessageSource(this.databaseMessageSource());
        messageSource.setBasenames(
                "classpath:/org/springframework/security/messages",
                "classpath:/org/hibernate/validator/ValidationMessages",
                "classpath:/br/com/uktech/testemultitenant/i18n/messages");
        return messageSource;
    }
    
}

