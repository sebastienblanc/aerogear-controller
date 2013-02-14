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
package org.jboss.aerogear.controller.router.decorators;

import static org.jboss.aerogear.controller.util.ParameterExtractor.extractArguments;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.jboss.aerogear.controller.router.Consumer;
import org.jboss.aerogear.controller.router.EndpointInvoker;
import org.jboss.aerogear.controller.router.ProcessResult;
import org.jboss.aerogear.controller.router.RouteContext;
import org.jboss.aerogear.controller.router.RouteProcessor;
import org.jboss.aerogear.controller.router.rest.pagination.AbstractPaginationStrategy;
import org.jboss.aerogear.controller.router.rest.pagination.Paginated;
import org.jboss.aerogear.controller.router.rest.pagination.PaginationInfo;
import org.jboss.aerogear.controller.router.rest.pagination.PaginationMetadata;
import org.jboss.aerogear.controller.router.rest.pagination.PaginationStrategy;

@Decorator
public class PaginationHandler implements RouteProcessor {
    
    private final RouteProcessor delegate;
    private final PaginationStrategy pagingStrategy;
    private final Map<String, Consumer> consumers = new HashMap<String, Consumer>();
    private final EndpointInvoker endpointInvoker;
    
    @Inject
    public PaginationHandler(final @Delegate RouteProcessor delegate, 
            final Instance<PaginationStrategy> pagingStrategies,
            final Instance<Consumer> consumers,
            final EndpointInvoker endpointInvoker) {
        this.delegate = delegate;
        for (Consumer consumer : consumers) {
            this.consumers.put(consumer.mediaType(), consumer);
        }
        this.pagingStrategy = pagingStrategies.isUnsatisfied() ? defaultPagingStrategy(): pagingStrategies.get();
        this.endpointInvoker = endpointInvoker;
    }

    @Override
    public ProcessResult process(final RouteContext routeContext) throws Exception {
        if (hasPaginatedAnnotation(routeContext.getRoute().getTargetMethod())) {
            final Map<String, Object> arguments = extractArguments(routeContext, consumers);
            final PaginationInfo paginationInfo = pagingStrategy.createPaginationInfo(routeContext, arguments);
            final List<Object> pagingArgs = merge(paginationInfo, arguments);
            final Object result = endpointInvoker.invoke(routeContext, pagingArgs.toArray());
            return new ProcessResult(pagingStrategy.postProcess(result, routeContext, paginationInfo), routeContext);
        } else {
            return delegate.process(routeContext);
        }
    }
    
    private List<Object> merge(final PaginationInfo paginationInfo, final Map<String, Object> arguments) {
        final List<Object> methodArguments = new LinkedList<Object>();
        arguments.remove(paginationInfo.getOffsetParamName());
        arguments.remove(paginationInfo.getLimitParamName());
        methodArguments.add(paginationInfo);
        methodArguments.addAll(arguments.values());
        return methodArguments;
    }
    
    private boolean hasPaginatedAnnotation(final Method targetMethod) {
        return targetMethod.getAnnotation(Paginated.class) != null;
    }
    
    public static PaginationStrategy defaultPagingStrategy() {
        return new AbstractPaginationStrategy() {
            @Override
            public void setResponseHeaders(final PaginationMetadata metadata, final HttpServletResponse response, final int resultSize) {
                for (Entry<String, String> entry : metadata.getHeaders(resultSize).entrySet()) {
                    response.setHeader(entry.getKey(), entry.getValue());
                }
            }
        };
    }

}
