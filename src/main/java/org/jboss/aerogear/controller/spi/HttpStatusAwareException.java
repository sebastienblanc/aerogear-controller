package org.jboss.aerogear.controller.spi;

public interface HttpStatusAwareException {

    int getStatus();
    
    String getMessage();
}
