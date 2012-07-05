package org.jboss.aerogear.controller.view;

import org.jboss.aerogear.controller.router.Route;

public interface ViewResolver {
    String resolveViewPathFor(Route route);
}
