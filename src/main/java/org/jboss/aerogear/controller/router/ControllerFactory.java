package org.jboss.aerogear.controller.router;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

/**
 * ControllerFactory is a factory for target endpoint classes in AeroGear.
 */
public class ControllerFactory {

    /**
     * Creates an instance of the passed-in type by delegating to CDI (beanManager).
     * 
     * @param targetClass the type of the target endpoint class.
     * @param beanManager the CDI bean manager that should be used to look up the type.
     * @return Object an instance of the target class.
     */
    public Object createController(Class<?> targetClass, BeanManager beanManager) {
        Bean next = beanManager.getBeans(targetClass).iterator().next();
        return next.create(beanManager.createCreationalContext(next));
    }
}
