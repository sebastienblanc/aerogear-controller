/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
