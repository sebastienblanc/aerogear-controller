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

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;

import org.jboss.aerogear.controller.router.Route;
import org.jboss.aerogear.controller.router.RouteContext;
import org.jboss.aerogear.controller.router.RouteProcessor;
import org.jboss.aerogear.controller.spi.SecurityProvider;

/**
 * SecurityHandler is a CDI Decorator that decorates a {@link RouteProcessor}.
 * </p>
 */
@Decorator
public class SecurityHandler implements RouteProcessor {
    
    private final RouteProcessor delegate;
    private final SecurityProvider securityProvider;
    
    /**
     * Sole constructor which will have its parameters injected by CDI.
     * 
     * @param delegate the target {@link RouteProcessor}.
     * @param securityProvider the security provider to be used.
     */
    @Inject
    public SecurityHandler(final @Delegate RouteProcessor delegate, final SecurityProvider securityProvider) {
        this.delegate = delegate;
        this.securityProvider = securityProvider;
    }

    /**
     * This method will use the injected {@link SecurityProvider} to access to the route is allowed.
     * If access is allowed this methods simply delegates to the target {@link RouteProcessor}.
     * 
     * @throws Exception if access to the Route is denied.
     */
    @Override
    public void process(final Route route, final RouteContext routeContext) throws Exception {
        if (route.isSecured()) {
            securityProvider.isRouteAllowed(route);
        }
        delegate.process(route, routeContext);
    }
    
}
