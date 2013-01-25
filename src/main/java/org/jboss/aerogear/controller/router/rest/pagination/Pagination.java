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
    
    public static OffsetStrategyBuilder offsetValue(String value) {
        return offset(OffsetPagingStrategy.DEFAULT_OFFSET_PARAM_NAME, value);
    }
    
    public static OffsetStrategyBuilder offset(final String offsetParamName, final String value) {
        return new OffsetStrategyBuilderImpl().offsetParam(offsetParamName, value);
    }
    
    public static interface OffsetStrategyBuilder {
        OffsetStrategyBuilder offsetParam(String paramName, String value);
        OffsetStrategyBuilder limitParam(String paramName, String value);
        OffsetStrategyBuilder limitValue(String value);
        OffsetStrategyBuilder customHeaders();
        OffsetStrategyBuilder customHeadersPrefix(String prefix);
        OffsetStrategyBuilder webLinking(boolean enabled);
        OffsetPagingStrategy build();
    }
    
    public static class OffsetStrategyBuilderImpl implements OffsetStrategyBuilder {
        
        private String offsetParamName = OffsetPagingStrategy.DEFAULT_OFFSET_PARAM_NAME;
        private String limitParamName = OffsetPagingStrategy.DEFAULT_LIMIT_PARAM_NAME;
        private String headerPrefix;
        private String offsetParamValue;
        private String limitParamValue;
        private boolean webLinking;

        @Override
        public OffsetStrategyBuilder offsetParam(final String paramName, String value) {
            offsetParamName = paramName;
            offsetParamValue = value;
            return this;
        }
        
        @Override
        public OffsetStrategyBuilder limitParam(final String paramName, String value) {
            limitParamName = paramName;
            limitParamValue = value;
            return this;
        }
        
        @Override
        public OffsetStrategyBuilder customHeadersPrefix(final String prefix) {
            this.headerPrefix = prefix;
            return this;
        }
        
        @Override
        public OffsetStrategyBuilder customHeaders() {
            this.headerPrefix = PagingMetadata.DEFAULT_HEADER_PREFIX;
            return this;
        }
        
        public OffsetPagingStrategy build() {
            final PaginationInfo info = new PaginationInfo(offsetParamName, offsetParamValue, limitParamName, limitParamValue);
            return webLinking ? new OffsetPagingStrategy(info) : new OffsetPagingStrategy(info, headerPrefix);
        }

        @Override
        public OffsetStrategyBuilder limitValue(String value) {
            limitParamValue = value;
            return this;
        }

        @Override
        public OffsetStrategyBuilder webLinking(boolean enabled) {
            webLinking = enabled;
            return this;
        }

    }

}
