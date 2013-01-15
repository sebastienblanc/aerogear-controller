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

import static org.jboss.aerogear.controller.util.ParameterExtractor.extractArguments;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.aerogear.controller.router.rest.pagination.Paginated;
import org.jboss.aerogear.controller.router.rest.pagination.Pagination;
import org.jboss.aerogear.controller.router.rest.pagination.PaginationInfo;
import org.jboss.aerogear.controller.router.rest.pagination.PagingStrategy;

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
    private Responders responders;
    private final Map<String, Consumer> consumers = new HashMap<String, Consumer>();
    
    public DefaultRouteProcessor() {
    }
    
    @Inject
    public DefaultRouteProcessor(BeanManager beanManager, Instance<Consumer> consumers, Responders responders, ControllerFactory controllerFactory) {
        this.beanManager = beanManager;
        this.controllerFactory = controllerFactory;
        this.responders = responders;
        for (Consumer consumer : consumers) {
            this.consumers.put(consumer.mediaType(), consumer);
        }
    }

    @Override
    public void process(RouteContext routeContext) throws Exception {
        final Route route = routeContext.getRoute();
        final Map<String, Object> arguments = extractArguments(routeContext, consumers);
        if (hasPaginatedAnnotation(route.getTargetMethod())) {
            processPaged(routeContext, arguments);
        } else {
            responders.respond(routeContext, route.getTargetMethod().invoke(getController(route), arguments.values().toArray()));
        }
    }
    
    private void processPaged(RouteContext routeContext, Map<String, Object> arguments) throws Exception {
        final Route route = routeContext.getRoute();
        final PagingStrategy pagingStrategy = getPagingStrategy(route, arguments);
        final PaginationInfo paginationInfo = pagingStrategy.getPaginationInfo();
        final List<Object> pagingArgs = merge(paginationInfo, arguments);
        final Object result = route.getTargetMethod().invoke(getController(route), pagingArgs.toArray());
        responders.respond(routeContext, pagingStrategy.process(result, routeContext));
    }
    
    private List<Object> merge(final PaginationInfo paginationInfo, final Map<String, Object> arguments) {
        final List<Object> methodArguments = new LinkedList<Object>();
        arguments.remove(paginationInfo.getOffsetParamName());
        arguments.remove(paginationInfo.getLimitParamName());
        methodArguments.add(paginationInfo);
        methodArguments.addAll(arguments.values());
        return methodArguments;
    }
    
    public PagingStrategy getPagingStrategy(final Route route, final Map<String, Object> args) {
        final Paginated paginated = route.getTargetMethod().getAnnotation(Paginated.class);
        final String customHeader = paginated.customHeadersPrefix();
        return Pagination.offset(paginated.offsetParamName(), (String) args.get(paginated.offsetParamName()))
                .limitParam(paginated.limitParamName(), (String) args.get(paginated.limitParamName()))
                .customHeadersPrefix(customHeader).build();
    }
    
    private boolean hasPaginatedAnnotation(final Method targetMethod) {
        return targetMethod.getAnnotation(Paginated.class) != null;
    }
    
    private Object getController(Route route) {
        return controllerFactory.createController(route.getTargetClass(), beanManager);
    }

}
