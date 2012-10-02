package org.jboss.aerogear.controller.spi;

import org.jboss.aerogear.controller.router.Route;

import javax.servlet.ServletException;

/**
 * Provides authorization for Routes in AeroGear Controller.
 */
public interface SecurityProvider {
    
    /**
     * Determines whether a request to the passed-in {@link Route} is allowed.
     * 
     * @param route the {@link Route} for which this provider to determine access.
     * @throws ServletException if this security provider denies access to the passed-in route.
     */
    void isRouteAllowed(Route route) throws ServletException;
}
