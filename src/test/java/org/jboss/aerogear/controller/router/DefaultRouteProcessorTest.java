/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors
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
package org.jboss.aerogear.controller.router;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.aerogear.controller.Car;
import org.jboss.aerogear.controller.SampleController;
import org.jboss.aerogear.controller.router.decorators.ResponseHandler;
import org.jboss.aerogear.controller.router.parameter.MissingRequestParameterException;
import org.jboss.aerogear.controller.router.rest.AbstractRestResponder;
import org.jboss.aerogear.controller.router.rest.JsonConsumer;
import org.jboss.aerogear.controller.router.rest.JsonResponder;
import org.jboss.aerogear.controller.router.rest.pagination.PaginationInfo;
import org.jboss.aerogear.controller.spi.SecurityProvider;
import org.jboss.aerogear.controller.view.HtmlViewResponder;
import org.jboss.aerogear.controller.view.JspViewResponder;
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
    private Instance<Consumer> consumers;
    @Mock
    private JspViewResponder jspResponder;
    @Mock
    private HtmlViewResponder htmlResponder;
    
    private Responders responders;
    private RouteProcessor router;
    private SampleController controller;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        instrumentResponders();
        instrumentConsumers();
        controller = spy(new SampleController());
        when(controllerFactory.createController(eq(SampleController.class), eq(beanManager))).thenReturn(controller);
        router = new ResponseHandler(new DefaultRouteProcessor(beanManager, consumers, controllerFactory), responders);
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
        doThrow(new ServletException()).when(securityProvider).isRouteAllowed(route);
    
        when(route.isSecured()).thenReturn(true);
        //TODO it must be fixed with Mockito
        securityProvider.isRouteAllowed(route);
    
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/car/3");
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        final Route route = routes.routeFor(RequestMethod.GET, "/car/{id}", Collections.<String>emptySet());
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
                        .produces(mockDefault())
                        .to(SampleController.class).find(param("id"));
            }
        };
        final Routes routes = routingModule.build();
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/car/3");
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        final Route route = routes.routeFor(RequestMethod.GET, "/car/{id}", Collections.<String>emptySet());
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
                        .produces(mockJsp(), mockJson())
                        .to(SampleController.class).find(param("id"));
            }
        };
        final Routes routes = routingModule.build();
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/car/3");
        when(request.getHeader("Accept")).thenReturn("application/json");
        when(jsonResponder.accepts("application/json")).thenReturn(true);
        when(jsonResponder.mediaType()).thenReturn(mockJson());
        final Route route = routes.routeFor(RequestMethod.GET, "/car/{id}", acceptHeaders(MediaType.JSON.getMediaType()));
        router.process(new RouteContext(route, request, response, routes));
        verify(jsonResponder).respond(anyObject(), any(RouteContext.class));
    }
    
    @Test
    public void testFormParameters() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/cars")
                        .on(RequestMethod.POST)
                        .produces(mockDefault())
                        .to(SampleController.class).save(param("color"), param("brand"));
            }
        };
        final Routes routes = routingModule.build();
        when(request.getParameterMap()).thenReturn(new RequestParams("color", "red").param("brand", "Ferrari").asMap());
        when(request.getMethod()).thenReturn(RequestMethod.POST.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/cars");
        final Route route = routes.routeFor(RequestMethod.POST, "/cars", acceptHeaders(MediaType.HTML.getMediaType()));
        router.process(new RouteContext(route, request, response, routes));
        verify(controller).save("red", "Ferrari");
    }
    
    @Test
    public void testFormParametersWithOneDefaultValue() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/cars")
                        .on(RequestMethod.POST)
                        .produces(mockDefault())
                        .to(SampleController.class).save(param("color", "gray"), param("brand", "Lada"));
            }
        };
        final Routes routes = routingModule.build();
        when(request.getParameterMap()).thenReturn(new RequestParams("color", "gray").asMap());
        when(request.getMethod()).thenReturn(RequestMethod.POST.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/cars");
        final Route route = routes.routeFor(RequestMethod.POST, "/cars", acceptHeaders(MediaType.HTML.getMediaType()));
        router.process(new RouteContext(route, request, response, routes));
        verify(controller).save("gray", "Lada");
    }
    
    @Test
    public void testEntityFormParameter() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/cars")
                        .on(RequestMethod.POST)
                        .produces(mockDefault())
                        .to(SampleController.class).save(param(Car.class));
            }
        };
        final Routes routes = routingModule.build();
        when(request.getParameterMap()).thenReturn(new RequestParams("car.color", "Blue").param("car.brand", "BMW").asMap());
        when(request.getMethod()).thenReturn(RequestMethod.POST.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/cars");
        final Route route = routes.routeFor(RequestMethod.POST, "/cars", acceptHeaders(MediaType.HTML.getMediaType()));
        router.process(new RouteContext(route, request, response, routes));
        verify(controller).save(any(Car.class));
    }
    
    @Test
    public void testQueryParameters() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/cars")
                        .on(RequestMethod.GET)
                        .produces(mockDefault())
                        .to(SampleController.class).find(param("color"), param("brand", "Ferrari"));
            }
        };
        final Routes routes = routingModule.build();
        when(request.getParameterMap()).thenReturn(new RequestParams("color", "red").asMap());
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/cars");
        final Route route = routes.routeFor(RequestMethod.GET, "/cars", acceptHeaders(MediaType.HTML.getMediaType()));
        router.process(new RouteContext(route, request, response, routes));
        verify(controller).find("red", "Ferrari");
    }
    
    @Test
    public void testPathAndQueryParameters() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/cars/{color}")
                        .on(RequestMethod.GET)
                        .produces(mockDefault())
                        .to(SampleController.class).find(param("color"), param("brand", "BMW"));
            }
        };
        final Routes routes = routingModule.build();
        when(request.getParameterMap()).thenReturn(new RequestParams().asMap());
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/cars/blue");
        final Route route = routes.routeFor(RequestMethod.GET, "/cars/blue", acceptHeaders(MediaType.HTML.getMediaType()));
        router.process(new RouteContext(route, request, response, routes));
        verify(controller).find("blue", "BMW");
    }
    
    @Test
    public void testHeaderParameters() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/cars")
                        .on(RequestMethod.GET)
                        .produces(mockDefault())
                        .to(SampleController.class).find(param("color"), param("brand", "Ferrari"));
            }
        };
        final Routes routes = routingModule.build();
        when(request.getHeader("color")).thenReturn("red");
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/cars");
        final Route route = routes.routeFor(RequestMethod.GET, "/cars", acceptHeaders(MediaType.HTML.getMediaType()));
        router.process(new RouteContext(route, request, response, routes));
        verify(controller).find("red", "Ferrari");
    }
    
    @Test
    public void testHeaderAndPathParameters() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/cars/{color}")
                        .on(RequestMethod.GET)
                        .produces(mockDefault())
                        .to(SampleController.class).find(param("color"), param("brand"));
            }
        };
        final Routes routes = routingModule.build();
        when(request.getHeader("brand")).thenReturn("Ferrari");
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/cars/red");
        final Route route = routes.routeFor(RequestMethod.GET, "/cars/red", acceptHeaders(MediaType.HTML.getMediaType()));
        router.process(new RouteContext(route, request, response, routes));
        verify(controller).find("red", "Ferrari");
    }
    
    @Test
    public void testCookieParameters() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/cars")
                        .on(RequestMethod.GET)
                        .produces(mockDefault())
                        .to(SampleController.class).find(param("color"), param("brand", "Ferrari"));
            }
        };
        final Routes routes = routingModule.build();
        final Cookie colorCookie = mock(Cookie.class);
        when(colorCookie.getName()).thenReturn("color");
        when(colorCookie.getValue()).thenReturn("red");
        when(request.getCookies()).thenReturn(new Cookie[] {colorCookie});
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/cars");
        final Route route = routes.routeFor(RequestMethod.GET, "/cars", acceptHeaders(MediaType.HTML.getMediaType()));
        router.process(new RouteContext(route, request, response, routes));
        verify(controller).find("red", "Ferrari");
    }
    
    @Test
    public void testCookieAndPathParameters() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/cars/{color}")
                        .on(RequestMethod.GET)
                        .produces(mockDefault())
                        .to(SampleController.class).find(param("color"), param("brand"));
            }
        };
        final Routes routes = routingModule.build();
        final Cookie colorCookie = mock(Cookie.class);
        when(colorCookie.getName()).thenReturn("brand");
        when(colorCookie.getValue()).thenReturn("Ferrari");
        when(request.getCookies()).thenReturn(new Cookie[] {colorCookie});
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/cars/red");
        final Route route = routes.routeFor(RequestMethod.GET, "/cars/red", acceptHeaders(MediaType.HTML.getMediaType()));
        router.process(new RouteContext(route, request, response, routes));
        verify(controller).find("red", "Ferrari");
    }
    
    @Test
    public void testQueryAndCookieParameters() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/cars")
                        .on(RequestMethod.GET)
                        .produces(mockDefault())
                        .to(SampleController.class).find(param("color"), param("brand"));
            }
        };
        final Routes routes = routingModule.build();
        final Cookie colorCookie = mock(Cookie.class);
        when(request.getParameterMap()).thenReturn(new RequestParams("color", "red").asMap());
        when(colorCookie.getName()).thenReturn("brand");
        when(colorCookie.getValue()).thenReturn("Ferrari");
        when(request.getCookies()).thenReturn(new Cookie[] {colorCookie});
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/cars");
        final Route route = routes.routeFor(RequestMethod.GET, "/cars", acceptHeaders(MediaType.HTML.getMediaType()));
        router.process(new RouteContext(route, request, response, routes));
        verify(controller).find("red", "Ferrari");
    }
    
    @Test
    public void testQueryAndHeaderParameters() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/cars")
                        .on(RequestMethod.GET)
                        .produces(mockDefault())
                        .to(SampleController.class).find(param("color"), param("brand"));
            }
        };
        final Routes routes = routingModule.build();
        when(request.getParameterMap()).thenReturn(new RequestParams("color", "red").param("brand", "Ferrari").asMap());
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/cars");
        final Route route = routes.routeFor(RequestMethod.GET, "/cars", acceptHeaders(MediaType.HTML.getMediaType()));
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
                        .produces(new MediaType("custom/type", CustomResponder.class))
                        .to(SampleController.class).find(param("id"));
            }
        };
        final Routes routes = routingModule.build();
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
    public void testDefaultResponder() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/cars")
                        .on(RequestMethod.POST)
                        .produces(mockJsp())
                        .to(SampleController.class).save(param("color", "gray"), param("brand", "Lada"));
            }
        };
        final Routes routes = routingModule.build();
        when(request.getParameterMap()).thenReturn(new RequestParams("color", "gray").asMap());
        when(request.getMethod()).thenReturn(RequestMethod.POST.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/cars");
        final Route route = routes.routeFor(RequestMethod.POST, "/cars", acceptHeaders(MediaType.HTML.getMediaType()));
        router.process(new RouteContext(route, request, response, routes));
        verify(controller).save("gray", "Lada");
        verify(jspResponder).respond(anyObject(), any(RouteContext.class));
    }
    
    @Test
    public void testAnyResponder() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/car/{id}").roles("admin")
                        .on(RequestMethod.GET)
                        .produces(mockJson())
                        .to(SampleController.class).find(param("id"));
            }
        };
        final Routes routes = routingModule.build();
        when(request.getHeader("Accept")).thenReturn(MediaType.ANY);
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/car/3");
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(jsonResponder.accepts("application/json")).thenReturn(true);
        final Route route = routes.routeFor(RequestMethod.GET, "/car/{id}", acceptHeaders(MediaType.JSON.getMediaType()));
        router.process(new RouteContext(route, request, response, routes));
        verify(jsonResponder).respond(anyObject(), any(RouteContext.class));
    }
    
    @Test
    public void testAnyResponderEmptyAcceptHeader() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/car/{id}")
                        .on(RequestMethod.GET)
                        .produces(mockJsp())
                        .to(SampleController.class).find(param("id"));
            }
        };
        final Routes routes = routingModule.build();
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/car/3");
        when(request.getHeader("Accept")).thenReturn(MediaType.ANY);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        final Route route = routes.routeFor(RequestMethod.GET, "/car/{id}", Collections.<String>emptySet());
        router.process(new RouteContext(route, request, response, routes));
        verify(jspResponder).respond(anyObject(), any(RouteContext.class));
    }

    private void instrumentConsumers() {
        final Iterator<Consumer> iterator = new HashSet<Consumer>(Arrays.asList(new JsonConsumer())).iterator();
        when(consumers.iterator()).thenReturn(iterator);
    }
    
    @Test
    public void testConsumes() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/cars").roles("admin")
                        .on(RequestMethod.POST)
                        .consumes(JSON)
                        .produces(mockJson())
                        .to(SampleController.class).save(param(Car.class));
            }
        };
        final Routes routes = routingModule.build();
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/cars");
        when(request.getHeader("Accept")).thenReturn("application/json");
        when(request.getInputStream()).thenReturn(inputStream("{\"color\":\"red\", \"brand\":\"mini\"}"));
        when(jsonResponder.accepts("application/json")).thenReturn(true);
        final Route route = routes.routeFor(RequestMethod.POST, "/cars", acceptHeaders(MediaType.JSON.getMediaType()));
        router.process(new RouteContext(route, request, response, routes));
        verify(controller).save(any(Car.class));
    }
    
    @Test
    public void testOrderMultipleAcceptHeaders() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/car/{id}").roles("admin")
                        .on(RequestMethod.GET)
                        .produces(mockJsp(), mockJson())
                        .to(SampleController.class).find(param("id"));
            }
        };
        final Routes routes = routingModule.build();
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/car/3");
        when(request.getHeader("Accept")).thenReturn("application/json," + MediaType.HTML);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        final Route route = routes.routeFor(RequestMethod.GET, "/car/{id}", new LinkedHashSet<String>(Arrays.asList("application/json", "text/html")));
        router.process(new RouteContext(route, request, response, routes));
        verify(jsonResponder).respond(anyObject(), any(RouteContext.class));
    }
    
    @Test (expected = RuntimeException.class) 
    public void testNoConsumers() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/cars").roles("admin")
                        .on(RequestMethod.POST)
                        .consumes(JSON)
                        .produces(mockJson())
                        .to(SampleController.class).save(param(Car.class));
            }
        };
        final Routes routes = routingModule.build();
        when(consumers.iterator()).thenReturn(new HashSet<Consumer>().iterator());
        router = new DefaultRouteProcessor(beanManager, consumers, controllerFactory);
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/cars");
        when(request.getHeader("Accept")).thenReturn("application/json");
        when(request.getInputStream()).thenReturn(inputStream("{\"color\":\"red\", \"brand\":\"mini\"}"));
        when(jsonResponder.accepts("application/json")).thenReturn(true);
        final Route route = routes.routeFor(RequestMethod.POST, "/cars", acceptHeaders(MediaType.JSON.getMediaType()));
        router.process(new RouteContext(route, request, response, routes));
    }
    
    @Test
    public void testPagedEndpointWithWebLinking() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/ints")
                        .on(RequestMethod.GET)
                        .produces(MediaType.JSON)
                        .to(SampleController.class).findByWithDefaults(param(PaginationInfo.class), param("color"));
            }
        };
        final Routes routes = routingModule.build();
        when(request.getParameterMap()).thenReturn(new RequestParams("color", "blue").asMap());
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/ints");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/abc/ints"));
        when(request.getQueryString()).thenReturn("color=blue");
        when(request.getHeader("Accept")).thenReturn("application/json");
        final StringWriter stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(printWriter(stringWriter));
        final Route route = routes.routeFor(RequestMethod.GET, "/ints", acceptHeaders(MediaType.JSON.getMediaType()));
        final JsonResponder spy = spy(new JsonResponder());
        final List<Responder> spyResponders = new LinkedList<Responder>(Arrays.asList(spy));
        when(this.responderInstance.iterator()).thenReturn(spyResponders.iterator());
        final Responders responders = new Responders(responderInstance);
        final RouteProcessor router = new ResponseHandler(new DefaultRouteProcessor(beanManager, consumers, controllerFactory), responders);
        router.process(new RouteContext(route, request, response, routes));
        verify(response).setHeader(eq("Link"), anyString());
    }
    
    @Test
    public void testPagedEndpointWithCustomHeadersDefaultPrefix() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/ints")
                        .on(RequestMethod.GET)
                        .produces(MediaType.JSON)
                        .to(SampleController.class).findByWithCustomHeadersDefaultPrefix(param(PaginationInfo.class), param("color"));
            }
        };
        final Routes routes = routingModule.build();
        when(request.getParameterMap()).thenReturn(new RequestParams("color", "blue").asMap());
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/ints");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/abc/ints"));
        when(request.getQueryString()).thenReturn("color=blue");
        when(request.getHeader("Accept")).thenReturn("application/json");
        final StringWriter stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(printWriter(stringWriter));
        final Route route = routes.routeFor(RequestMethod.GET, "/ints", acceptHeaders(MediaType.JSON.getMediaType()));
        final JsonResponder spy = spy(new JsonResponder());
        final List<Responder> spyResponders = new LinkedList<Responder>(Arrays.asList(spy));
        when(this.responderInstance.iterator()).thenReturn(spyResponders.iterator());
        final Responders responders = new Responders(responderInstance);
        final RouteProcessor router = new ResponseHandler(new DefaultRouteProcessor(beanManager, consumers, controllerFactory), responders);
        router.process(new RouteContext(route, request, response, routes));
        verify(response).setHeader("AG-Links-Next", "http://localhost:8080/abc/ints?color=blue&offset=10&limit=10");
        verify(response, never()).setHeader(eq("AG-Links-Previous"), anyString());
        assertThat(stringWriter.toString()).isEqualTo("[0,1,2,3,4,5,6,7,8,9]");
    }
    
    @Test
    public void testPagedEndpointCustomHeadersPrefix() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/ints")
                        .on(RequestMethod.GET)
                        .produces(MediaType.JSON)
                        .to(SampleController.class).findByWithCustomHeadersPrefix(param(PaginationInfo.class), param("color"));
            }
        };
        final Routes routes = routingModule.build();
        when(request.getParameterMap()).thenReturn(new RequestParams("offset", "0").param("limit", "5").param("color", "blue").asMap());
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/ints");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/abc/ints"));
        when(request.getQueryString()).thenReturn("offset=0&color=blue&limit=5");
        when(request.getHeader("Accept")).thenReturn("application/json");
        final StringWriter stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(printWriter(stringWriter));
        final Route route = routes.routeFor(RequestMethod.GET, "/ints", acceptHeaders(MediaType.JSON.getMediaType()));
        final JsonResponder spy = spy(new JsonResponder());
        final List<Responder> spyResponders = new LinkedList<Responder>(Arrays.asList(spy));
        when(this.responderInstance.iterator()).thenReturn(spyResponders.iterator());
        final Responders responders = new Responders(responderInstance);
        final RouteProcessor router = new ResponseHandler(new DefaultRouteProcessor(beanManager, consumers, controllerFactory), responders);
        router.process(new RouteContext(route, request, response, routes));
        verify(response, never()).setHeader(eq("Test-Links-Previous"), anyString());
        verify(response).setHeader("Test-Links-Next", "http://localhost:8080/abc/ints?offset=5&color=blue&limit=5");
        assertThat(stringWriter.toString()).isEqualTo("[0,1,2,3,4]");
    }
    
    @Test
    public void testPagedEndpointMiddlePage() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/ints")
                        .on(RequestMethod.GET)
                        .produces(MediaType.JSON)
                        .to(SampleController.class).findByWithCustomHeadersPrefix(param(PaginationInfo.class), param("color"));
            }
        };
        final Routes routes = routingModule.build();
        when(request.getParameterMap()).thenReturn(new RequestParams("offset", "5").param("limit", "5").param("color", "blue").asMap());
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/ints");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/abc/ints"));
        when(request.getQueryString()).thenReturn("color=blue&offset=5&limit=5");
        when(request.getHeader("Accept")).thenReturn("application/json");
        final StringWriter stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(printWriter(stringWriter));
        final Route route = routes.routeFor(RequestMethod.GET, "/ints", acceptHeaders(MediaType.JSON.getMediaType()));
        final JsonResponder spy = spy(new JsonResponder());
        final List<Responder> spyResponders = new LinkedList<Responder>(Arrays.asList(spy));
        when(this.responderInstance.iterator()).thenReturn(spyResponders.iterator());
        final Responders responders = new Responders(responderInstance);
        final RouteProcessor router = new ResponseHandler(new DefaultRouteProcessor(beanManager, consumers, controllerFactory), responders);
        router.process(new RouteContext(route, request, response, routes));
        verify(response).setHeader("Test-Links-Previous", "http://localhost:8080/abc/ints?color=blue&offset=0&limit=5");
        verify(response).setHeader("Test-Links-Next", "http://localhost:8080/abc/ints?color=blue&offset=10&limit=5");
        assertThat(stringWriter.toString()).isEqualTo("[5,6,7,8,9]");
    }
    
    @Test
    public void testPagedEndpointBeyondLastPage() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/ints")
                        .on(RequestMethod.GET)
                        .produces(MediaType.JSON)
                        .to(SampleController.class).findByWithCustomParamNames(param(PaginationInfo.class), param("brand"));
            }
        };
        final Routes routes = routingModule.build();
        when(request.getParameterMap()).thenReturn(new RequestParams("myoffset", "50").param("mylimit", "5").param("brand", "BMW").asMap());
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/ints");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/abc/ints"));
        when(request.getQueryString()).thenReturn("brand=BMW&myoffset=0&mylimit=5");
        when(request.getHeader("Accept")).thenReturn("application/json");
        final StringWriter stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));
        final Route route = routes.routeFor(RequestMethod.GET, "/ints", acceptHeaders(MediaType.JSON.getMediaType()));
        final JsonResponder spy = spy(new JsonResponder());
        final List<Responder> spyResponders = new LinkedList<Responder>(Arrays.asList(spy));
        when(this.responderInstance.iterator()).thenReturn(spyResponders.iterator());
        final Responders responders = new Responders(responderInstance);
        final RouteProcessor router = new ResponseHandler(new DefaultRouteProcessor(beanManager, consumers, controllerFactory), responders);
        router.process(new RouteContext(route, request, response, routes));
        verify(response).setHeader("TS-Links-Previous", "http://localhost:8080/abc/ints?brand=BMW&myoffset=45&mylimit=5");
        verify(response, never()).setHeader(eq("TS-Links-Next"), anyString());
        assertThat(stringWriter.toString()).isEqualTo("[]");
    }
    
    @Test (expected = MissingRequestParameterException.class) 
    public void testMissingQueryParameter() throws Exception {
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/cars")
                        .on(RequestMethod.GET)
                        .produces(mockDefault())
                        .to(SampleController.class).find(param("color"), param("brand", "Ferrari"));
            }
        };
        final Routes routes = routingModule.build();
        when(request.getParameterMap()).thenReturn(new RequestParams().asMap());
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/cars");
        final Route route = routes.routeFor(RequestMethod.GET, "/cars", acceptHeaders(MediaType.HTML.getMediaType()));
        router.process(new RouteContext(route, request, response, routes));
    }    
    
    private PrintWriter printWriter(StringWriter writer) {
        return new PrintWriter(writer);
    }
   
    private ServletInputStream inputStream(final String json) {
        final ByteArrayInputStream ba = new ByteArrayInputStream(json.getBytes());
        return new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return ba.read();
            }
        };
    }
        
    private MediaType mockJson() {
        return new MediaType(MediaType.JSON.getMediaType(), jsonResponder.getClass());
    }

    private MediaType mockHtml() {
        return new MediaType(MediaType.HTML.getMediaType(), htmlResponder.getClass()); 
    }

    private MediaType mockJsp() {
        return new MediaType(MediaType.JSP.getMediaType(), jspResponder.getClass()); 
    }
    
    private MediaType mockDefault() {
        return mockJsp();
    }

    private void instrumentResponders() {
        when(jspResponder.accepts(MediaType.HTML.getMediaType())).thenReturn(true);
        when(jspResponder.mediaType()).thenReturn(mockJsp());
        when(jspResponder.accepts(MediaType.ANY)).thenReturn(true);
        
        when(htmlResponder.accepts(MediaType.HTML.getMediaType())).thenReturn(true);
        when(htmlResponder.mediaType()).thenReturn(mockHtml());
        
        when(jsonResponder.accepts(MediaType.JSON.getMediaType())).thenReturn(true);
        when(jsonResponder.mediaType()).thenReturn(mockJson());
        final List<Responder> responders = new LinkedList<Responder>(Arrays.asList(jspResponder, jsonResponder, htmlResponder));
        when(this.responderInstance.iterator()).thenReturn(responders.iterator());
        this.responders = new Responders(responderInstance);
    }

    private Set<String> acceptHeaders(String... mediaTypes) {
        return new HashSet<String>(Arrays.asList(mediaTypes));
    }
    
    private static class RequestParams {
        private Map<String, String[]> params = new HashMap<String, String[]>();
        
        public RequestParams() {
        }
        
        public RequestParams(final String key, final String value) {
            params.put(key, new String[]{value});
        }
        
        public RequestParams param(final String key, final String value) {
            params.put(key, new String[]{value});
            return this;
        }
        
        public Map<String, String[]> asMap() {
            return params;
        }
    }
    
    private class CustomResponder extends AbstractRestResponder {
        
        public CustomResponder(String mediaType) {
            super(new MediaType("application/custom", CustomResponder.class));
        }

        @Override
        public void writeResponse(Object entity, RouteContext routeContext) throws Exception {
            //NoOp
        }
        
    }
}
