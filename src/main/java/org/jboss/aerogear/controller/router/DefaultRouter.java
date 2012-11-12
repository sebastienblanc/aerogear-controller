package org.jboss.aerogear.controller.router;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.aerogear.controller.util.RequestUtils;

/**
 * Default implementation of {@link Router}.
 * </p>
 * This implementation uses Context and Dependency Injection (CDI) to have various parts injected into it. Of<br>
 * particular interest for end users is the {@link RoutingModule} which is described in more detail in the section below.
 * 
 * <h3> RoutingModule </h3>
 * The CDI implementation will scan for an instance of {@link RoutingModule} upon deployment, and its<br> 
 * {@link RoutingModule#build()} method will be called to assemble the routes configured for this application.<br>
 * To simplify this process {@link AbstractRoutingModule} is provided, please refer its javadoc for sample usage.
 */
public class DefaultRouter implements Router {
    
    private Routes routes;
    private RouteProcessor routeProcessor;
    
    public DefaultRouter() {
    }
    
    @Inject
    public DefaultRouter(RoutingModule routes, RouteProcessor routeProcessor) {
        this.routes = routes.build();
        this.routeProcessor = routeProcessor;
    }

    @Override
    public boolean hasRouteFor(HttpServletRequest httpServletRequest) {
        return routes.hasRouteFor(RequestUtils.extractMethod(httpServletRequest), RequestUtils.extractPath(httpServletRequest));
    }

    @Override
    public void dispatch(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException {
        try {
            RouteContext routeContext = new RouteContext(request, response, routes);
            Route route = routes.routeFor(RequestUtils.extractMethod(request), routeContext.getRequestPath());
            routeProcessor.process(route, new RouteContext(request, response, routes));
        } catch (Exception e) {
            throw new ServletException(e.getMessage(), e);
        }
    }
    
}
