package org.jboss.aerogear.controller.router;

import org.jboss.aerogear.controller.RequestMethod;

public interface RouteBuilder {

    TargetEndpoint on(Class<? extends Throwable> exception, Class<?>... exceptions);
    
    OnMethods from(String path);

    public static interface OnMethods {
        TargetEndpoint on(RequestMethod... methods);
        OnMethods roles(String... roles);
    }

    public static interface TargetEndpoint {
        <T> T to(Class<T> clazz);
    }

    Route build();
}
