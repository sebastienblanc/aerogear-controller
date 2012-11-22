/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.aerogear.controller.router.decorators;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.enterprise.inject.Instance;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.aerogear.controller.router.Route;
import org.jboss.aerogear.controller.router.RouteContext;
import org.jboss.aerogear.controller.router.RouteProcessor;
import org.jboss.aerogear.controller.router.Routes;
import org.jboss.aerogear.controller.spi.SecurityProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SecurityHandlerTest {
    
    @Mock
    private Route route;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RouteProcessor routeProcessor;
    @Mock
    private Routes routes;
    @Mock
    private Instance<SecurityProvider> securityInstance;
    @Mock
    private SecurityProvider securityProvider;
    @Mock
    private ServletContext servletContext;
    private SecurityHandler securityHandler;
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(securityInstance.isUnsatisfied()).thenReturn(false);
        when(securityInstance.get()).thenReturn(securityProvider);
        securityHandler = new SecurityHandler(routeProcessor, securityInstance);
        when(servletContext.getContextPath()).thenReturn("/myapp");
        when(request.getServletContext()).thenReturn(servletContext);
        when(request.getRequestURI()).thenReturn("/myapp/cars/1");
    }
    
    @Test
    public void testRouteAllowed() throws Exception {
        when(route.isSecured()).thenReturn(true);
        final RouteContext routeContext = routeContext();
        securityHandler.process(route, routeContext);
        verify(routeProcessor).process(route, routeContext); 
    }
    
    @Test (expected = ServletException.class)
    public void testRouteForbbiden() throws Exception {
        when(route.isSecured()).thenReturn(true);
        doThrow(ServletException.class).when(securityProvider).isRouteAllowed(any(Route.class));
        securityHandler.process(route, routeContext());
    }
    
    private RouteContext routeContext() {
        return new RouteContext(request, response, routes);
    }

}
