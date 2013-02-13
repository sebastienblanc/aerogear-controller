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

/**
 * Factory utility methods for creating PagingStrategies. 
 */
public final class Pagination {
    
    private Pagination() {
    }
    
    public static PaginationInfoBuilder offsetValue(String value) {
        return offset(PaginationInfo.DEFAULT_OFFSET_PARAM_NAME, value);
    }
    
    public static PaginationInfoBuilder offset(final String offsetParamName, final String value) {
        return new PaginationInfoBuilderImpl().offsetParam(offsetParamName, value);
    }
    
    public static interface PaginationInfoBuilder {
        PaginationInfoBuilder offsetParam(String paramName, String value);
        PaginationInfoBuilder limitParam(String paramName, String value);
        PaginationInfoBuilder limitValue(String value);
        PaginationInfoBuilder customHeaders();
        PaginationInfoBuilder customHeadersPrefix(String prefix);
        PaginationInfoBuilder webLinking(boolean enabled);
        PaginationInfo build();
    }
    
    public static class PaginationInfoBuilderImpl implements PaginationInfoBuilder {
        
        private String offsetParamName = PaginationInfo.DEFAULT_OFFSET_PARAM_NAME;
        private String limitParamName = PaginationInfo.DEFAULT_LIMIT_PARAM_NAME;
        private String headerPrefix;
        private String offsetParamValue;
        private String limitParamValue;
        private boolean webLinking;

        @Override
        public PaginationInfoBuilder offsetParam(final String paramName, String value) {
            offsetParamName = paramName;
            offsetParamValue = value;
            return this;
        }
        
        @Override
        public PaginationInfoBuilder limitParam(final String paramName, String value) {
            limitParamName = paramName;
            limitParamValue = value;
            return this;
        }
        
        @Override
        public PaginationInfoBuilder customHeadersPrefix(final String prefix) {
            this.headerPrefix = prefix;
            return this;
        }
        
        @Override
        public PaginationInfoBuilder customHeaders() {
            this.headerPrefix = PagingMetadata.DEFAULT_HEADER_PREFIX;
            return this;
        }
        
        public PaginationInfo build() {
            if (webLinking) {
                return new PaginationInfo(offsetParamName, offsetParamValue, limitParamName, limitParamValue);
            } else {
                return new PaginationInfo(offsetParamName, offsetParamValue, limitParamName, limitParamValue, headerPrefix);
            }
        }

        @Override
        public PaginationInfoBuilder limitValue(String value) {
            limitParamValue = value;
            return this;
        }

        @Override
        public PaginationInfoBuilder webLinking(boolean enabled) {
            webLinking = enabled;
            return this;
        }

    }

}
