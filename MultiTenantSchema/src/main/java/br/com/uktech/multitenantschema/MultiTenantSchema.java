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
package br.com.uktech.multitenantschema;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.servlet.DispatcherServlet;

/**
 *
 * @author Carlos Alberto Cipriano Korovsky <carlos.korovsky at gmail.com>
 */
public class MultiTenantSchema implements WebApplicationInitializer {
    
    private static final String APPLICATION_NAME = "MultiTenantSchema";
    private static final String SERVLET_NAME = APPLICATION_NAME + "Servlet";
    private static final String CONFIG_LOCATION = "br.com.uktech.multitenantschema.config";
    private static final String MAPPING_URL = "/*";

    private AnnotationConfigWebApplicationContext getContext() {
        AnnotationConfigWebApplicationContext context;
        context = new AnnotationConfigWebApplicationContext();
        context.setDisplayName(APPLICATION_NAME);
        context.setConfigLocation(CONFIG_LOCATION);
        return context;
    }
    
    private CharacterEncodingFilter getCharacterEncodingFilter() {
        CharacterEncodingFilter characterEncodingFilter;
        characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        return characterEncodingFilter;
    }

    private RequestContextFilter getRequestContextFilter() {
        RequestContextFilter requestContextFilter;
        requestContextFilter = new RequestContextFilter();
        return requestContextFilter;
    }
    
    private OpenEntityManagerInViewFilter getOpenEntityManagerInViewFilter() {
        OpenEntityManagerInViewFilter openEntityManagerInViewFilter;
        openEntityManagerInViewFilter = new OpenEntityManagerInViewFilter();
        openEntityManagerInViewFilter.setEntityManagerFactoryBeanName("entityManagerFactory");
        return openEntityManagerInViewFilter;
    }
    
    private DelegatingFilterProxy getDelegatingFilterProxy() {
        DelegatingFilterProxy delegatingFilterProxy;
        delegatingFilterProxy = new DelegatingFilterProxy("springSecurityFilterChain");
        return delegatingFilterProxy;
    }   

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        WebApplicationContext context;
        context = this.getContext();

        ContextLoaderListener contextLoaderListener;
        contextLoaderListener = new ContextLoaderListener(context);

        servletContext.addListener(contextLoaderListener);

        servletContext.addFilter("CharacterEncodingFilter", this.getCharacterEncodingFilter()).addMappingForUrlPatterns(null, true, MAPPING_URL);
        servletContext.addFilter("RequestContextFilter", this.getRequestContextFilter()).addMappingForUrlPatterns(null, true, MAPPING_URL);
        servletContext.addFilter("OpenEntityManagerInViewFilter", getOpenEntityManagerInViewFilter()).addMappingForUrlPatterns(null, true, MAPPING_URL);
        servletContext.addFilter("SecurityFilter", getDelegatingFilterProxy()).addMappingForUrlPatterns(null, true, MAPPING_URL);

        DispatcherServlet dispatcherServlet;
        dispatcherServlet = new DispatcherServlet(context);
        dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);

        ServletRegistration.Dynamic dispatcher;
        dispatcher = servletContext.addServlet(SERVLET_NAME, dispatcherServlet);
        dispatcher.setLoadOnStartup(1);
        dispatcher.setAsyncSupported(true);
        dispatcher.addMapping(MAPPING_URL);
    }
    
}
