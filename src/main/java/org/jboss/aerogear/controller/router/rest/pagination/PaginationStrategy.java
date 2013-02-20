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
package org.jboss.aerogear.controller.router.rest.pagination;

import java.util.Collection;
import java.util.Map;

import org.jboss.aerogear.controller.router.RouteContext;

/**
 * A strategy for implementing pagination in AeroGear Controller.
 */
public interface PaginationStrategy {
    
    /**
     * Creates a PaginationInfo instance.
     * How this information is gathered, be it from an Annotation on the target endpoint method, 
     * or by using separate request parameters is up to the concrete implementation.
     * 
     * @param routeContext the {@link RouteContext} of the route being processed.
     * @param arguments the extracted arguments from the current request.
     * @return {@link PaginationInfo} the information required for paging. 
     */
    PaginationInfo createPaginationInfo(RouteContext routeContext, Map<String, Object> arguments);
    
    /**
     * Called before the target endpoint method has been invoked and enables a concrete strategy to 
     * manipulate the arguments that will be passed to the target endpoint method.
     * 
     * @param pagingInfo the {@link PaginationInfo} instance created by this strategy.
     * @param arguments the extracted arguments from the current request.
     * @return {@code Object[]} the arguments that will be passed to the target endpoint method.
     */
    Object[] preInvocation(PaginationInfo pagingInfo, Map<String, Object> arguments); 
    
    /**
     * Called after the target endpoint method has been invoked and 
     * allows the strategy to set HTTP Response headers. 
     * 
     * @param results the result returned from the target endpoint method.
     * @param routeContext the {@link RouteContext}.
     * @param pagingInfo the {@link PaginationInfo} instance created by this strategy.
     * @return {@code Object} Either the unchanged result or a modified result depending on the underlying implementation.
     */
    Object postInvocation(Collection<?> results, RouteContext routeContext, PaginationInfo pagingInfo);
    

}
