/*
 * JBoss, Home of Professional Open Source Copyright 2012, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.jboss.aerogear.controller.router;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.enterprise.inject.spi.BeanManager;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.aerogear.controller.SampleController;
import org.jboss.aerogear.controller.spi.SecurityProvider;
import org.jboss.aerogear.controller.view.ViewResolver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DefaultRouteProcessorTest {

    @Mock
    private SecurityProvider securityProvider;

    @Mock
    private Route route;
    @Mock
    private BeanManager beanManager;
    @Mock
    private ViewResolver viewResolver;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain chain;
    @Mock
    private ControllerFactory controllerFactory;
    @Mock
    private ServletContext servletContext;
    @Mock
    private RequestDispatcher requestDispatcher;

    private DefaultRouteProcessor router;
    private Routes routes;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        final RoutingModule routingModule = new AbstractRoutingModule() {

            @Override
            public void configuration() {
                route()
                        .from("/car/{id}").roles("admin")
                        .on(RequestMethod.GET)
                        .to(SampleController.class).find(pathParam("id"));
            }
        };
        routes = routingModule.build();
        router = new DefaultRouteProcessor(beanManager, viewResolver, controllerFactory);
    }

    @Test
    public void testIt() throws Exception {
        final SampleController controller = spy(new SampleController());
        when(controllerFactory.createController(eq(SampleController.class), eq(beanManager))).thenReturn(controller);
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/car/3");
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        final Route route = routes.routeFor(RequestMethod.GET, "/car/{id}");
        router.process(route, new RouteContext(request, response, routes));
        verify(controller).find(eq("3"));
    }

    @Test(expected = ServletException.class)
    public void testRouteForbbiden() throws Exception {
        final SampleController controller = spy(new SampleController());
        doThrow(new ServletException()).when(securityProvider).isRouteAllowed(route);

        when(route.isSecured()).thenReturn(true);
        //TODO it must be fixed with mockito
        securityProvider.isRouteAllowed(route);

        when(controllerFactory.createController(eq(SampleController.class), eq(beanManager))).thenReturn(controller);
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/car/3");
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        final Route route = routes.routeFor(RequestMethod.GET, "/car/{id}");
        router.process(route, new RouteContext(request, response, routes));
        verify(controller).find(eq("3"));
    }
    
}
