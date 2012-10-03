package org.jboss.aerogear.controller.spi;

public interface HttpStatusAwareException {

    public int getStatus();
    public String getMessage();
}
