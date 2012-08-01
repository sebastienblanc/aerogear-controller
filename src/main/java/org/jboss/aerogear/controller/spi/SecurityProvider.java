package org.jboss.aerogear.controller.spi;

import org.jboss.aerogear.controller.router.Route;

public interface SecurityProvider {
    public boolean isRouteAllowed(Route route);
}
