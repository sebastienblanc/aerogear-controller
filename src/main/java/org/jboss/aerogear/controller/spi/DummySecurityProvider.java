package org.jboss.aerogear.controller.spi;

import org.jboss.aerogear.controller.router.Route;

import javax.enterprise.context.ApplicationScoped;

public class DummySecurityProvider implements SecurityProvider {
    @Override
    public boolean isRouteAllowed(Route route) {
        return true;
    }
}
