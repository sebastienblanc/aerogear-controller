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

import java.util.Map;

import org.jboss.aerogear.controller.router.Route;
import org.jboss.aerogear.controller.router.RouteContext;

/**
 * A strategy for implementing pagination in AeroGear Controller.
 */
public interface PagingStrategy {
    
    PaginationInfo getPaginationInfo(final Route route, final Map<String, Object> arguments);
    
    /**
     * Allows for manipulating the result of an endpoint target method invocation. 
     * 
     * @param result the result returned from the target endpoint method.
     * @param routeContext the {@link RouteContext}.
     * @return {@code Object} Either the unchanged result or a modified result depending on the underlying implementation.
     */
    Object postProcess(Object result, RouteContext routeContext, PaginationInfo pagingInfo);
    

}
