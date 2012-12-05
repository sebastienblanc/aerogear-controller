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

package org.jboss.aerogear.controller.router;

import static org.jboss.aerogear.controller.util.RequestUtils.extractAcceptHeader;
import static org.jboss.aerogear.controller.util.RequestUtils.extractArguments;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import com.google.common.collect.Sets;

/**
 * Default implementation of {@link RouteProcessor}.
 * </p>
 * This implementation uses Context and Dependency Injection (CDI) to have various parts injected into it. Of<br>
 * particular interest for end users is the {@link RoutingModule} which is described in more detail in the section below.
 * 
 * <h3> RoutingModule </h3>
 * The CDI implementation will scan for an instance of {@link RoutingModule} upon deployment, and its<br> 
 * {@link RoutingModule#build()} method will be called to assemble the routes configured for this application.<br>
 * To simplify this process {@link AbstractRoutingModule} is provided, please refer its javadoc for sample usage.
 */
public class DefaultRouteProcessor implements RouteProcessor {
    
    private BeanManager beanManager;
    private ControllerFactory controllerFactory;
    private Set<Responder> responders = new HashSet<Responder>();
    
    public DefaultRouteProcessor() {
    }
    
    @Inject
    public DefaultRouteProcessor(BeanManager beanManager, Instance<Responder> responders, ControllerFactory controllerFactory) {
        this.beanManager = beanManager;
        this.controllerFactory = controllerFactory;
        for (Responder responder : responders) {
            this.responders.add(responder);
        }
    }

    @Override
    public void process(RouteContext routeContext) throws Exception {
        final Route route = routeContext.getRoute();
        final Object[] arguments = extractArguments(routeContext);
        final Object result = route.getTargetMethod().invoke(getController(route), arguments);
        
        for (String mediaType : Sets.intersection(route.produces(), extractAcceptHeader(routeContext.getRequest()))) {
            for (Responder responder : responders) {
                if (responder.accepts(mediaType)) {
                    responder.respond(result, routeContext);
                    return;
                }
            }
        }
    }
    
    private Object getController(Route route) {
        return controllerFactory.createController(route.getTargetClass(), beanManager);
    }

}
