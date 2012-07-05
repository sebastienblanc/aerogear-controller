package org.jboss.aerogear.controller.router;

import org.jboss.aerogear.controller.RequestMethod;
import org.jboss.aerogear.controller.log.AeroGearLogger;
import org.jboss.aerogear.controller.router.Route;
import org.jboss.aerogear.controller.router.RouteBuilder;
import org.jboss.aerogear.controller.router.RouteBuilderImpl;

import java.util.ArrayList;
import java.util.List;

public class Routes {

    private final List<Route> routes = new ArrayList<Route>();

    public Routes(List<RouteBuilder> routeBuilders) {
        for (RouteBuilder routeBuilder : routeBuilders) {
            routes.add(routeBuilder.build());
        }
    }

    public static RouteBuilder route() {
        return new RouteBuilderImpl();
    }

    public static Routes from(List<RouteBuilder> routes) {
        return new Routes(routes);
    }

    @Override
    public String toString() {
        return "Routes{" +
                "routes=" + routes +
                '}';
    }

    public boolean hasRouteFor(RequestMethod method, String requestURI) {
        AeroGearLogger.LOGGER.requestedRoute(method, requestURI);
        for (Route route : routes) {
            if (route.getMethods().contains(method) && route.getPath().equals(requestURI)) {
                return true;
            }
        }
        return false;
    }

    public Route routeFor(RequestMethod method, String requestURI) {
        for (Route route : routes) {
            if (route.getMethods().contains(method) && route.getPath().equals(requestURI)) {
                return route;
            }
        }
        throw AeroGearLogger.LOGGER.routeNotFound(method, requestURI);
    }
}
