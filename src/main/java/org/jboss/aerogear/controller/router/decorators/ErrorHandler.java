/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc., and individual contributors
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

package org.jboss.aerogear.controller.router.decorators;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.aerogear.controller.router.ControllerFactory;
import org.jboss.aerogear.controller.router.Route;
import org.jboss.aerogear.controller.router.RouteContext;
import org.jboss.aerogear.controller.router.RouteProcessor;
import org.jboss.aerogear.controller.router.error.ErrorRoute;
import org.jboss.aerogear.controller.spi.HttpStatusAwareException;
import org.jboss.aerogear.controller.view.ErrorViewResolver;
import org.jboss.aerogear.controller.view.View;
import org.jboss.aerogear.controller.view.ViewResolver;

import com.google.common.base.Throwables;

/**
 * ErrorHandler is a CDI Decorator that decorates a {@link RouteProcessor}.
 * </p>
 * By wrapping the call to {@link RouteProcessor#process(Route, RouteContext)} with a try catch block, this class 
 * will handle any exception thrown and either forward to a the appropriate error route configured, or if no error 
 * route exists, forward to the default error view.
 */
@Decorator
public class ErrorHandler implements RouteProcessor {
    
    private final RouteProcessor delegate;
    private final ControllerFactory controllerFactory;
    private final BeanManager beanManager;
    private final ViewResolver errorViewResolver;
    
    @Inject
    public ErrorHandler(final @Delegate RouteProcessor delegate, final ViewResolver viewResolver, 
            final ControllerFactory controllerFactory, final BeanManager beanManager) {
        this.delegate = delegate;
        this.controllerFactory = controllerFactory;
        this.beanManager = beanManager;
        errorViewResolver = new ErrorViewResolver(viewResolver);
    }

    @Override
    public void process(Route route, RouteContext routeContext) throws Exception {
        try {
            delegate.process(route, routeContext);
        } catch (Throwable t) {
            if (t instanceof HttpStatusAwareException) {
                routeContext.getResponse().setStatus(((HttpStatusAwareException) t).getStatus());
            }
            final Throwable rootCause = Throwables.getRootCause(t);
            final Route errorRoute = routeContext.getRoutes().routeFor(rootCause);
            invokeErrorRoute(errorRoute, rootCause);
            forwardErrorToView(errorRoute, rootCause, routeContext.getRequest(), routeContext.getResponse());
        }
    }
    
    private void invokeErrorRoute(final Route errorRoute, final Throwable t) throws ServletException {
        try {
            final Method targetMethod = errorRoute.getTargetMethod();
            if (targetMethod.getParameterTypes().length == 0) {
                targetMethod.invoke(getController(errorRoute));
            } else {
                targetMethod.invoke(getController(errorRoute), t);
            }
        } catch (final Exception e) {
            throw new ServletException(e.getMessage(), e);
        }
    }
    
    private void forwardErrorToView(final Route errorRoute, final Throwable rootCause, 
            final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
        try {
            final View view = new View(errorViewResolver.resolveViewPathFor(errorRoute), rootCause);
            request.setAttribute(ErrorRoute.DEFAULT.getExceptionAttrName(), view.getModel());
            request.getRequestDispatcher(view.getViewPath()).forward(request, response);
        } catch (IOException e) {
            throw new ServletException(e.getMessage(), e);
        }
    }
    
    private Object getController(Route route) {
        return controllerFactory.createController(route.getTargetClass(), beanManager);
    }

}
