package org.jboss.aerogear.controller.router;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

class ControllerFactory {

    public Object createController(Class<?> targetClass, BeanManager beanManager) {
        Bean next = beanManager.getBeans(targetClass).iterator().next();
        return next.create(beanManager.createCreationalContext(next));
    }
}
