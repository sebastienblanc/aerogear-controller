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
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.aerogear.controller.router.Route;
import org.jboss.aerogear.controller.router.RouteContext;

/**
 * A PagingStrategy that uses an offset/limit to add HTTP headers to the response.
 * </p>
 * The current implementation adds the following HTTP headers:
 * <ul>
 * <li>AG-Next</li>
 * <li>AG-Previous</li>
 * </ul>
 * The above headers acts as links to the next/previous pages of data.
 */
public class OffsetPagingStrategy implements PagingStrategy {
    
    public static final String DEFAULT_OFFSET_PARAM_NAME = "offset";
    public static final String DEFAULT_LIMIT_PARAM_NAME = "limit";
    
    public OffsetPagingStrategy() {
    }
    
    @Override
    public Object postProcess(final Object result, final RouteContext routeContext, final PaginationInfo pagingInfo) {
        if (!(result instanceof Collection)) {
            return result;
        }
        final Collection<?> results = (Collection<?>) result;
        final Map<String, String> headers = createMetadata(routeContext, pagingInfo).getHeaders(results.size());
        final HttpServletResponse response = routeContext.getResponse();
        for (Entry<String, String> entry : headers.entrySet()) {
            response.setHeader(entry.getKey(), entry.getValue());
        }
        return results;
    }
    
    private PagingMetadata createMetadata(final RouteContext routeContext, final PaginationInfo pagingInfo) {
        final RequestPathParser requestPathParser = new RequestPathParser(pagingInfo, getResourcePath(routeContext));
        final PagingProperties pagingProperties = new PagingProperties(pagingInfo.getOffset(), pagingInfo.getLimit());
        if (pagingInfo.webLinking()) {
            return new PagingMetadata(pagingProperties, requestPathParser);
        } else {
            return new PagingMetadata(pagingProperties, requestPathParser, pagingInfo.getHeaderPrefix().get());
        }
    }
    
    private String getResourcePath(final RouteContext routeContext) {
        final HttpServletRequest request = routeContext.getRequest();
        final String resourcePath = request.getRequestURL().toString() + "?" + routeContext.getRequest().getQueryString();
        if (resourcePath.startsWith("/")) {
            return resourcePath.substring(1);
        }
        return resourcePath;
    }

    @Override
    public PaginationInfo getPaginationInfo(final Route route, final Map<String, Object> args) {
        final Paginated paginated = route.getTargetMethod().getAnnotation(Paginated.class);
        final String customHeader = paginated.customHeadersPrefix();
        return Pagination.offset(paginated.offsetParamName(), argAsString(args, paginated.offsetParamName()))
                .limitParam(paginated.limitParamName(), argAsString(args, paginated.limitParamName()))
                .customHeadersPrefix(customHeader)
                .webLinking(paginated.webLinking())
                .build();
    }
    
    private String argAsString(final Map<String, Object> args, final String argName) {
        return (String) args.get(argName);
    }
    

}
