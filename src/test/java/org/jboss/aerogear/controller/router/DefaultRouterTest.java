package org.jboss.aerogear.controller.router;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
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

import org.jboss.aerogear.controller.RequestMethod;
import org.jboss.aerogear.controller.SampleController;
import org.jboss.aerogear.controller.SampleControllerException;
import org.jboss.aerogear.controller.spi.SecurityProvider;
import org.jboss.aerogear.controller.view.ViewResolver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DefaultRouterTest {

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

    private DefaultRouter router;

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
        router = new DefaultRouter(routingModule, beanManager, viewResolver, controllerFactory, securityProvider);
    }

    @Test
    public void testIt() throws ServletException {
        final SampleController controller = spy(new SampleController());
        when(controllerFactory.createController(eq(SampleController.class), eq(beanManager))).thenReturn(controller);
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/car/3");
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        router.dispatch(request, response, chain);
        verify(controller).find(eq("3"));
    }

    @Test
    public void testRouteAllowed() throws Exception {
        final SampleController controller = spy(new SampleController());
        when(route.isSecured()).thenReturn(true);

        when(controllerFactory.createController(eq(SampleController.class), eq(beanManager))).thenReturn(controller);
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/abc");
        when(request.getRequestURI()).thenReturn("/abc/car/3");
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        router.dispatch(request, response, chain);
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
        router.dispatch(request, response, chain);
        verify(controller).find(eq("3"));
    }
    
    @Test
    public void testOnException() throws Exception {
        final SampleController controller = spy(new SampleController());
        configureExceptionTestMocks(controller);
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() throws Exception {
                route()
                        .on(IllegalStateException.class)
                        .to(SampleController.class).errorPage();
                route()
                        .from("/home")
                        .on(RequestMethod.GET, RequestMethod.POST)
                        .to(SampleController.class).throwIllegalStateException();
            }
        };
        defaultRouter(routingModule).dispatch(request, response, chain);
        verify(controller).errorPage();
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testOnExceptions() throws Exception {
        final SampleController controller = spy(new SampleController());
        configureExceptionTestMocks(controller);
        final RoutingModule routingModule = new AbstractRoutingModule() {
            @Override
            public void configuration() throws Exception {
                route()
                        .on(SampleControllerException.class, IllegalStateException.class)
                        .to(SampleController.class).error(param(Exception.class));
                route()
                        .from("/home")
                        .on(RequestMethod.GET, RequestMethod.POST)
                        .to(SampleController.class).throwSampleControllerException();
            }
        };
        defaultRouter(routingModule).dispatch(request, response, chain);
        verify(controller).error(any(IllegalArgumentException.class));
        verify(requestDispatcher).forward(request, response);
    }
    
    private DefaultRouter defaultRouter(final RoutingModule routingModule) {
        return new DefaultRouter(routingModule, beanManager, viewResolver, controllerFactory, securityProvider);
    }
     
     private void configureExceptionTestMocks(final SampleController controller) {
        when(controllerFactory.createController(eq(SampleController.class), eq(beanManager))).thenReturn(controller);
        when(request.getMethod()).thenReturn(RequestMethod.GET.toString());
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn("/webapp");
        when(request.getRequestURI()).thenReturn("/webapp/home");
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(viewResolver.resolveViewPathFor((Route)anyObject())).thenReturn("WEB-INF/Home/error.jsp");
        when(request.getRequestDispatcher("WEB-INF/Home/error.jsp")).thenReturn(requestDispatcher);
     }
    
}
