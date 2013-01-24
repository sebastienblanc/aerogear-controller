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

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Optional;

public class PagingMetadata {
    
    public static final String DEFAULT_HEADER_PREFIX = "AG-";
    private final Links links;
    private final PagingProperties params;
    private final Optional<String> headerPrefix;
    
    public PagingMetadata(final PagingProperties paging, final RequestPathParser requestPathParser) {
        this(paging, requestPathParser, Optional.<String>absent());
    }
    
    public PagingMetadata(final PagingProperties paging, final RequestPathParser requestPathParser, final String headerPrefix) {
        this(paging, requestPathParser, Optional.of(headerPrefix));
    }
    
    private PagingMetadata(final PagingProperties params, final RequestPathParser requestPathParser, final Optional<String> headerPrefix) {
        this.params = params;
        this.headerPrefix = headerPrefix;
        links = new Links(requestPathParser, params);
    }

    public Links getLinks() {
        return links;
    }

    public Map<String, String> getHeaders(final int resultsSize) {
        final Map<String, String> headers = new HashMap<String, String>();
        if (headerPrefix.isPresent()) {
            if (!params.isFirstOffset()) {
                headers.put(headerPrefix.get() + "Links-Previous", links.getPrevious());
            }
            if (resultsSize == params.limit()) {
                headers.put(headerPrefix.get() + "Links-Next", links.getNext());
            }
        } 
        return headers;
    }
    
    @Override
    public String toString() {
        return "PagingMetadata[params=" + params + ", links=" + links + ", headerPrefix=" + headerPrefix + "]";
        
    }
    
}
