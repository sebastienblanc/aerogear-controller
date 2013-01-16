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

import org.jboss.aerogear.controller.router.RouteContext;

import com.google.common.base.Optional;

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
    private PaginationInfo pagingInfo;
    private final Optional<String> headerPrefix;
    
    public OffsetPagingStrategy(final PaginationInfo paginationInfo) {
        this(paginationInfo, Optional.<String>absent());
    }
    
    public OffsetPagingStrategy(final PaginationInfo paginationInfo, final String headerPrefix) {
        this(paginationInfo, Optional.fromNullable(headerPrefix));
    }
    
    private OffsetPagingStrategy(final PaginationInfo paginationInfo, final Optional<String> headerPrefix) {
        this.pagingInfo = paginationInfo;
        this.headerPrefix = headerPrefix;
    }

    @Override
    public Object process(final Object result, final RouteContext routeContext) {
        if (!(result instanceof Collection)) {
            return result;
        }
        final Collection<?> results = (Collection<?>) result;
        final Map<String, String> headers = createMetadata(routeContext).getHeaders(results.size());
        final HttpServletResponse response = routeContext.getResponse();
        for (Entry<String, String> entry : headers.entrySet()) {
            response.setHeader(entry.getKey(), entry.getValue());
        }
        return results;
    }
    
    private PagingMetadata createMetadata(final RouteContext routeContext) {
        final RequestPathParser requestPathParser = new RequestPathParser(pagingInfo, getResourcePath(routeContext));
        final PagingProperties pagingProperties = new PagingProperties(pagingInfo.getOffset(), pagingInfo.getLimit());
        if (headerPrefix.isPresent()) {
            return new PagingMetadata(pagingProperties, requestPathParser, headerPrefix.get());
        } else {
            return new PagingMetadata(pagingProperties, requestPathParser);
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
    public PaginationInfo getPagingationInfo() {
        return pagingInfo;
    }
    
}
