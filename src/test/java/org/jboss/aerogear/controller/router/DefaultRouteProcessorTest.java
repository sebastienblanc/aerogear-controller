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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.aerogear.controller.Car;
import org.jboss.aerogear.controller.SampleController;
import org.jboss.aerogear.controller.router.rest.JsonResponder;
import org.jboss.aerogear.controller.spi.SecurityProvider;
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
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private ControllerFactory controllerFactory;
    @Mock
    private ServletContext servletContext;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private Instance<Responder> responderInstance;
    @Mock
    private JsonResponder jsonResponder;
    @Mock
    private MvcResponder mvcResponder;
    private Responders responders;
    private DefaultRouteProcessor router;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        instrumentResponders();
        router = new DefaultRouteProcessor(beanManager, responders, controllerFactory);
        when(request.getHeader("Accept")).thenReturn("text/html");
    }
    
    @Test(expected = ServletException.class)
    public void testRouteForbidden() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/car/{id}").roles("admin")
                        .on(RequestMethod.GET)
                        .to(SampleController.class).find(param("id"));
            }
        };
        final Routes routes = routingModule.build();
        final SampleController controller = spy(new SampleController());
        doThrow(new ServletException()).when(securityProvider).isRouteAllowed(route);
    
        when(route.isSecured()).thenReturn(true);
        //TODO it must be fixed with Mockito
        securityProvider.isRouteAllowed(route);
    
        when(controllerFactory.createController(eq(SampleController.class), eq(beanManager))).thenReturn(controller);
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/car/3");
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        final Route route = routes.routeFor(RequestMethod.GET, "/car/{id}", MediaType.defaultAcceptHeader());
        router.process(new RouteContext(route, request, response, routes));
        verify(controller).find("3");
    }

    @Test
    public void testMvcRouteWithPathParam() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/car/{id}").roles("admin")
                        .on(RequestMethod.GET)
                        .to(SampleController.class).find(param("id"));
            }
        };
        final Routes routes = routingModule.build();
        final SampleController controller = spy(new SampleController());
        when(controllerFactory.createController(eq(SampleController.class), eq(beanManager))).thenReturn(controller);
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/car/3");
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        final Route route = routes.routeFor(RequestMethod.GET, "/car/{id}", MediaType.defaultAcceptHeader());
        router.process(new RouteContext(route, request, response, routes));
        verify(controller).find("3");
    }

    @Test
    public void testRestRouteWithPathParam() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/car/{id}").roles("admin")
                        .on(RequestMethod.GET)
                        .produces(MediaType.HTML, MediaType.JSON)
                        .to(SampleController.class).find(param("id"));
            }
        };
        final Routes routes = routingModule.build();
        final SampleController controller = spy(new SampleController());
        when(controllerFactory.createController(eq(SampleController.class), eq(beanManager))).thenReturn(controller);
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/car/3");
        when(request.getHeader("Accept")).thenReturn("application/json");
        when(jsonResponder.accepts("application/json")).thenReturn(true);
        when(jsonResponder.mediaType()).thenReturn("application/json");
        final Set<String> acceptHeaders = new LinkedHashSet<String>(Arrays.asList(MediaType.JSON.toString()));
        final Route route = routes.routeFor(RequestMethod.GET, "/car/{id}", acceptHeaders);
        router.process(new RouteContext(route, request, response, routes));
        verify(jsonResponder).respond(anyObject(), any(RouteContext.class));
    }
    
    @Test
    public void testFormParmeters() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/cars")
                        .on(RequestMethod.POST)
                        .to(SampleController.class).save(param("color"), param("brand"));
            }
        };
        final Routes routes = routingModule.build();
        final Map<String, String[]> requestParamMap = new HashMap<String, String[]>();
        requestParamMap.put("color", new String[] {"red"});
        requestParamMap.put("brand", new String[] {"Ferrari"});
        when(request.getParameterMap()).thenReturn(requestParamMap);
        final SampleController controller = spy(new SampleController());
        when(controllerFactory.createController(eq(SampleController.class), eq(beanManager))).thenReturn(controller);
        when(request.getMethod()).thenReturn(RequestMethod.POST.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/cars");
        final Route route = routes.routeFor(RequestMethod.POST, "/cars", MediaType.defaultAcceptHeader());
        router.process(new RouteContext(route, request, response, routes));
        verify(controller).save("red", "Ferrari");
    }
    
    @Test
    public void testFormParmetersWithOneDefaultValue() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/cars")
                        .on(RequestMethod.POST)
                        .to(SampleController.class).save(param("color", "gray"), param("brand", "Lada"));
            }
        };
        final Routes routes = routingModule.build();
        final Map<String, String[]> requestParamMap = new HashMap<String, String[]>();
        requestParamMap.put("color", new String[] {"gray"});
        when(request.getParameterMap()).thenReturn(requestParamMap);
        final SampleController controller = spy(new SampleController());
        when(controllerFactory.createController(eq(SampleController.class), eq(beanManager))).thenReturn(controller);
        when(request.getMethod()).thenReturn(RequestMethod.POST.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/cars");
        final Route route = routes.routeFor(RequestMethod.POST, "/cars", MediaType.defaultAcceptHeader());
        router.process(new RouteContext(route, request, response, routes));
        verify(controller).save("gray", "Lada");
    }
    
    @Test
    public void testEntityFormParmeter() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/cars")
                        .on(RequestMethod.POST)
                        .to(SampleController.class).save(param(Car.class));
            }
        };
        final Routes routes = routingModule.build();
        final Map<String, String[]> requestParamMap = new HashMap<String, String[]>();
        requestParamMap.put("car.color", new String[] {"Blue"});
        requestParamMap.put("car.brand", new String[] {"BMW"});
        when(request.getParameterMap()).thenReturn(requestParamMap);
        final SampleController controller = spy(new SampleController());
        when(controllerFactory.createController(eq(SampleController.class), eq(beanManager))).thenReturn(controller);
        when(request.getMethod()).thenReturn(RequestMethod.POST.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/cars");
        final Route route = routes.routeFor(RequestMethod.POST, "/cars", MediaType.defaultAcceptHeader());
        router.process(new RouteContext(route, request, response, routes));
        verify(controller).save(any(Car.class));
    }
    
    @Test
    public void testQueryParmeters() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/cars")
                        .on(RequestMethod.GET)
                        .to(SampleController.class).find(param("color"), param("brand", "Ferrari"));
            }
        };
        final Routes routes = routingModule.build();
        final Map<String, String[]> requestParamMap = new HashMap<String, String[]>();
        requestParamMap.put("color", new String[] {"red"});
        when(request.getParameterMap()).thenReturn(requestParamMap);
        final SampleController controller = spy(new SampleController());
        when(controllerFactory.createController(eq(SampleController.class), eq(beanManager))).thenReturn(controller);
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/cars");
        final Route route = routes.routeFor(RequestMethod.GET, "/cars", MediaType.defaultAcceptHeader());
        router.process(new RouteContext(route, request, response, routes));
        verify(controller).find("red", "Ferrari");
    }
    
    @Test
    public void testPathAndQueryParmeters() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/cars/{color}")
                        .on(RequestMethod.GET)
                        .to(SampleController.class).find(param("color"), param("brand", "BMW"));
            }
        };
        final Routes routes = routingModule.build();
        when(request.getParameterMap()).thenReturn(Collections.<String, String[]>emptyMap());
        final SampleController controller = spy(new SampleController());
        when(controllerFactory.createController(eq(SampleController.class), eq(beanManager))).thenReturn(controller);
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/cars/blue");
        final Route route = routes.routeFor(RequestMethod.GET, "/cars/blue", MediaType.defaultAcceptHeader());
        router.process(new RouteContext(route, request, response, routes));
        verify(controller).find("blue", "BMW");
    }
    
    @Test
    public void testHeaderParmeters() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/cars")
                        .on(RequestMethod.GET)
                        .to(SampleController.class).find(param("color"), param("brand", "Ferrari"));
            }
        };
        final Routes routes = routingModule.build();
        when(request.getHeader("color")).thenReturn("red");
        final SampleController controller = spy(new SampleController());
        when(controllerFactory.createController(eq(SampleController.class), eq(beanManager))).thenReturn(controller);
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/cars");
        final Route route = routes.routeFor(RequestMethod.GET, "/cars", MediaType.defaultAcceptHeader());
        router.process(new RouteContext(route, request, response, routes));
        verify(controller).find("red", "Ferrari");
    }
    
    @Test
    public void testHeaderAndPathParmeters() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/cars/{color}")
                        .on(RequestMethod.GET)
                        .to(SampleController.class).find(param("color"), param("brand"));
            }
        };
        final Routes routes = routingModule.build();
        when(request.getHeader("brand")).thenReturn("Ferrari");
        final SampleController controller = spy(new SampleController());
        when(controllerFactory.createController(eq(SampleController.class), eq(beanManager))).thenReturn(controller);
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/cars/red");
        final Route route = routes.routeFor(RequestMethod.GET, "/cars/red", MediaType.defaultAcceptHeader());
        router.process(new RouteContext(route, request, response, routes));
        verify(controller).find("red", "Ferrari");
    }
    
    @Test
    public void testCookieParmeters() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/cars")
                        .on(RequestMethod.GET)
                        .to(SampleController.class).find(param("color"), param("brand", "Ferrari"));
            }
        };
        final Routes routes = routingModule.build();
        final Cookie colorCookie = mock(Cookie.class);
        when(colorCookie.getName()).thenReturn("color");
        when(colorCookie.getValue()).thenReturn("red");
        when(request.getCookies()).thenReturn(new Cookie[] {colorCookie});
        final SampleController controller = spy(new SampleController());
        when(controllerFactory.createController(eq(SampleController.class), eq(beanManager))).thenReturn(controller);
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/cars");
        final Route route = routes.routeFor(RequestMethod.GET, "/cars", MediaType.defaultAcceptHeader());
        router.process(new RouteContext(route, request, response, routes));
        verify(controller).find("red", "Ferrari");
    }
    
    @Test
    public void testCookieAndPathParmeters() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/cars/{color}")
                        .on(RequestMethod.GET)
                        .to(SampleController.class).find(param("color"), param("brand"));
            }
        };
        final Routes routes = routingModule.build();
        final Cookie colorCookie = mock(Cookie.class);
        when(colorCookie.getName()).thenReturn("brand");
        when(colorCookie.getValue()).thenReturn("Ferrari");
        when(request.getCookies()).thenReturn(new Cookie[] {colorCookie});
        final SampleController controller = spy(new SampleController());
        when(controllerFactory.createController(eq(SampleController.class), eq(beanManager))).thenReturn(controller);
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/cars/red");
        final Route route = routes.routeFor(RequestMethod.GET, "/cars/red", MediaType.defaultAcceptHeader());
        router.process(new RouteContext(route, request, response, routes));
        verify(controller).find("red", "Ferrari");
    }
    
    @Test
    public void testQueryAndCookieParmeters() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/cars")
                        .on(RequestMethod.GET)
                        .to(SampleController.class).find(param("color"), param("brand"));
            }
        };
        final Routes routes = routingModule.build();
        final Cookie colorCookie = mock(Cookie.class);
        final Map<String, String[]> requestParamMap = new HashMap<String, String[]>();
        requestParamMap.put("color", new String[] {"red"});
        when(request.getParameterMap()).thenReturn(requestParamMap);
        when(colorCookie.getName()).thenReturn("brand");
        when(colorCookie.getValue()).thenReturn("Ferrari");
        when(request.getCookies()).thenReturn(new Cookie[] {colorCookie});
        final SampleController controller = spy(new SampleController());
        when(controllerFactory.createController(eq(SampleController.class), eq(beanManager))).thenReturn(controller);
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/cars");
        final Route route = routes.routeFor(RequestMethod.GET, "/cars", MediaType.defaultAcceptHeader());
        router.process(new RouteContext(route, request, response, routes));
        verify(controller).find("red", "Ferrari");
    }
    
    @Test
    public void testQueryAndHeaderParmeters() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/cars")
                        .on(RequestMethod.GET)
                        .to(SampleController.class).find(param("color"), param("brand"));
            }
        };
        final Routes routes = routingModule.build();
        final Map<String, String[]> requestParamMap = new HashMap<String, String[]>();
        requestParamMap.put("color", new String[] {"red"});
        when(request.getHeader("brand")).thenReturn("Ferrari");
        when(request.getParameterMap()).thenReturn(requestParamMap);
        final SampleController controller = spy(new SampleController());
        when(controllerFactory.createController(eq(SampleController.class), eq(beanManager))).thenReturn(controller);
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/cars");
        final Route route = routes.routeFor(RequestMethod.GET, "/cars", MediaType.defaultAcceptHeader());
        router.process(new RouteContext(route, request, response, routes));
        verify(controller).find("red", "Ferrari");
    }
    
    
    @Test (expected = RuntimeException.class)
    public void testNoRespondersForMediaType() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/car/{id}")
                        .on(RequestMethod.GET)
                        .produces("custom/type")
                        .to(SampleController.class).find(param("id"));
            }
        };
        final Routes routes = routingModule.build();
        final SampleController controller = spy(new SampleController());
        when(controllerFactory.createController(eq(SampleController.class), eq(beanManager))).thenReturn(controller);
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/car/3");
        when(request.getHeader("Accept")).thenReturn("custom/type");
        final Set<String> acceptHeaders = new LinkedHashSet<String>(Arrays.asList("custom/type"));
        final Route route = routes.routeFor(RequestMethod.GET, "/car/{id}", acceptHeaders);
        router.process(new RouteContext(route, request, response, routes));
    }
    
    @Test
    public void testAnyResponder() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/car/{id}").roles("admin")
                        .on(RequestMethod.GET)
                        .produces(MediaType.JSON)
                        .to(SampleController.class).find(param("id"));
            }
        };
        final Routes routes = routingModule.build();
        final SampleController controller = spy(new SampleController());
        when(controllerFactory.createController(eq(SampleController.class), eq(beanManager))).thenReturn(controller);
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/car/3");
        when(request.getHeader("Accept")).thenReturn(MediaType.ANY.toString());
        when(jsonResponder.accepts("application/json")).thenReturn(true);
        final Set<String> acceptHeaders = new LinkedHashSet<String>(Arrays.asList(MediaType.JSON.toString()));
        final Route route = routes.routeFor(RequestMethod.GET, "/car/{id}", acceptHeaders);
        router.process(new RouteContext(route, request, response, routes));
        verify(mvcResponder).respond(anyObject(), any(RouteContext.class));
    }
    
    @Test
    public void testAnyResponderEmptyAcceptHeader() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/car/{id}")
                        .on(RequestMethod.GET)
                        .to(SampleController.class).find(param("id"));
            }
        };
        final Routes routes = routingModule.build();
        final SampleController controller = spy(new SampleController());
        when(controllerFactory.createController(eq(SampleController.class), eq(beanManager))).thenReturn(controller);
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/car/3");
        when(request.getHeader("Accept")).thenReturn(MediaType.ANY.toString());
        when(jsonResponder.accepts("application/json")).thenReturn(true);
        final Route route = routes.routeFor(RequestMethod.GET, "/car/{id}", Collections.<String>emptySet());
        router.process(new RouteContext(route, request, response, routes));
        verify(mvcResponder).respond(anyObject(), any(RouteContext.class));
    }

    private void instrumentResponders() {
        when(mvcResponder.accepts(MediaType.HTML.toString())).thenReturn(true);
        when(mvcResponder.accepts(MediaType.ANY.toString())).thenReturn(true);
        when(jsonResponder.accepts(MediaType.JSON.toString())).thenReturn(true);
        final List<Responder> responders = new LinkedList<Responder>();
        responders.add(mvcResponder);
        responders.add(jsonResponder);
        when(this.responderInstance.iterator()).thenReturn(responders.iterator());
        this.responders = new Responders(responderInstance);
    }
    
}
