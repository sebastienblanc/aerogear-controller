package org.jboss.aerogear.controller.router;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Concrete implementation of {@link RouteBuilder}.
 * 
 * @see RouteBuilder
 */
public class RouteBuilderImpl implements RouteBuilder {

    private final RouteDescriptor routeDescriptor = new RouteDescriptor();

    @Override
    public OnMethods from(String path) {
        routeDescriptor.setPath(path);
        return routeDescriptor;
    }
    
    @Override
    public Route build() {
        return new DefaultRoute(routeDescriptor.getPath(), routeDescriptor.getMethods(), routeDescriptor.getTargetClass(),
                routeDescriptor.getTargetMethod(), routeDescriptor.getRoles(), routeDescriptor.getThrowables());
    }

    @Override
    public String toString() {
        return "RouteBuilderImpl{" +
                "routeDescriptor=" + routeDescriptor +
                '}';
    }

    @Override
    public TargetEndpoint on(Class<? extends Throwable> exception, Class<?>... exceptions) {
        final Set<Class<? extends Throwable>> set = exceptions(exceptions);
        set.add(exception);
        routeDescriptor.setThrowables(set);
        return routeDescriptor;
    }
    
    @SuppressWarnings("unchecked")
    private Set<Class<? extends Throwable>> exceptions(final Class<?>... exceptions) {
        final List<Class<?>> list = Arrays.asList(exceptions);
        final HashSet<Class<? extends Throwable>> set = new HashSet<Class<? extends Throwable>>();
        for (Class<?> e : list) {
            if (!Throwable.class.isAssignableFrom(e)) {
                throw new IllegalArgumentException("Class '" + e.getName() + "' must be a subclass of Throwable");
            }
            set.add((Class<? extends Throwable>) e);
        }
        return new HashSet<Class<? extends Throwable>>(set);
    }

}
