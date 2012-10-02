package org.jboss.aerogear.controller.spi;

import org.jboss.aerogear.controller.router.Route;

import javax.enterprise.inject.Alternative;

/**
 * Temporary SecurityProvider implementation.
 */
@Alternative
public class DummySecurityProvider implements SecurityProvider {
    
    @Override
    public void isRouteAllowed(Route route) {
        //TODO might be interesting some action here
    }
}
