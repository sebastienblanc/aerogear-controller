package org.jboss.aerogear.controller.spi;

import org.jboss.aerogear.controller.router.Route;

import javax.servlet.ServletException;

public interface SecurityProvider {
    public boolean isRouteAllowed(Route route) throws ServletException;
}
