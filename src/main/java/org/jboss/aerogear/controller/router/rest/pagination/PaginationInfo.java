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

import com.google.common.base.Optional;

/**
 * Holds information related to pagination in AeroGear Controller.
 * </p>
 * The parameter names will be provided using the {@code Paginated} annotation and the values will be
 * the values contained in the current request. If the configured parameters are missing from the request,
 * the default values specified in {@link Paginated} will be used. 
 * 
 * @see Paginated
 */
public class PaginationInfo {
    
    public static final String DEFAULT_OFFSET_PARAM_NAME = "offset";
    public static final String DEFAULT_LIMIT_PARAM_NAME = "limit";
    
    private final String offsetParamName;
    private final int offset;
    private final String limitParamName;
    private final int limit;
    private final boolean webLinking;
    private final Optional<String> headerPrefix;
    
    public PaginationInfo(final String offsetParamName, 
            final int offset, 
            final String limitParamName, 
            final int limit) {
        this(offsetParamName, offset, limitParamName, limit, true, Optional.<String>absent());
    }
    
    public PaginationInfo(final String offsetParamName, 
            final int offset, 
            final String limitParamName, 
            final int limit,
            final String headerPrefix) {
        this(offsetParamName, offset, limitParamName, limit, false, Optional.of(headerPrefix));
    }

    private PaginationInfo(final String offsetParamName, 
            final int offset, 
            final String limitParamName, 
            final int limit,
            final boolean webLinking,
            final Optional<String> headerPrefix) {
        this.offsetParamName = offsetParamName;
        this.offset = offset;
        this.limitParamName = limitParamName;
        this.limit = limit;
        this.webLinking = webLinking;
        this.headerPrefix = headerPrefix;
    }

    public String getOffsetParamName() {
        return offsetParamName;
    }

    public int getOffset() {
        return offset;
    }

    public String getLimitParamName() {
        return limitParamName;
    }

    public int getLimit() {
        return Integer.valueOf(limit);
    }
    
    public boolean webLinking() {
        return webLinking;
    }
    
    @Override
    public String toString() {
        return "PaginationInfo[offsetParamName=" + offsetParamName + ", offset=" + offset + 
                ", limitParamName=" + limitParamName + ", limit=" + limit + 
                ", webLinking=" + webLinking + ", headerPrefix=" + headerPrefix + "]";
    }
    
    public Optional<String> getHeaderPrefix() {
        return headerPrefix;
    }
    
}
