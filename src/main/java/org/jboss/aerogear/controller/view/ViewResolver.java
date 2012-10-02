package org.jboss.aerogear.controller.view;

import org.jboss.aerogear.controller.router.Route;

/**
 * A ViewResolver is capable of resolving a paths for Routes.
 */
public interface ViewResolver {
    
    String resolveViewPathFor(Route route);
    
}
