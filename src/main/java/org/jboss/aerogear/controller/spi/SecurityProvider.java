package org.jboss.aerogear.controller.spi;

import org.apache.deltaspike.security.api.User;

public interface SecurityProvider {
    public boolean hasRole(User user, String param);
}
