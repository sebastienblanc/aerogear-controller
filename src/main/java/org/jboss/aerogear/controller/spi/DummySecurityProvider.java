package org.jboss.aerogear.controller.spi;

import org.jboss.aerogear.controller.router.Route;

public class DummySecurityProvider implements SecurityProvider {
    @Override
    public boolean isRouteAllowed(Route route) {
        //TODO might be interesting some action here
        return true;
    }
}
