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
package org.jboss.aerogear.controller.mocks;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.aerogear.controller.router.ControllerFactory;
import org.jboss.aerogear.controller.router.EndpointInvoker;
import org.jboss.aerogear.controller.router.Route;
import org.jboss.aerogear.controller.router.error.ErrorTarget;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class MockEndpointInvoker {
    @Mock
    private BeanManager beanManager;
    @Mock
    private ControllerFactory controllerFactory;
    private ErrorTarget errorTarget;

    public MockEndpointInvoker() {
        MockitoAnnotations.initMocks(this);
        instrumentErrorController();
    }

    private void instrumentErrorController() {
        errorTarget = spy(new ErrorTarget());
        when(controllerFactory.createController(eq(ErrorTarget.class), eq(beanManager))).thenReturn(errorTarget);
    }

    public EndpointInvoker getEndpointInvoker() {
        return new EndpointInvoker(controllerFactory, beanManager);
    }

    public Object setController(Object controller, final Route route) {
        if (controller == null) {
            controller = mock(route.getTargetClass());
        }
        when(controllerFactory.createController(eq(route.getTargetClass()), eq(beanManager))).thenReturn(controller);
        return controller;
    }

    public MockEndpointInvoker addController(final Object controller) {
        when(controllerFactory.createController(eq(controller.getClass()), eq(beanManager))).thenReturn(controller);
        return this;
    }

    public ErrorTarget getErrorTarget() {
        return errorTarget;
    }

}
