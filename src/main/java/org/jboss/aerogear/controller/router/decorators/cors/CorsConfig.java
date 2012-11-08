/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc., and individual contributors
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

package org.jboss.aerogear.controller.router.decorators.cors;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jboss.aerogear.controller.router.RequestMethod;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

/**
 * Concrete implementation of {@link CorsConfiguration} which provides configuration of CORS in AeroGear Controller.
 * 
 * @see CorsHandler
 */
public class CorsConfig implements CorsConfiguration {
    
    private final boolean corsSupportEnabled;
    private final String exposeHeaders;
    private final boolean anyOrigin;
    private final boolean allowCookies;
    private final long maxAge;
    private final Set<String> validRequestMethods;
    private final Set<String> validRequestHeaders;
    
    private CorsConfig(final Builder builder) {
        this.corsSupportEnabled = builder.corsSupportEnabled;
        this.exposeHeaders = builder.exposeheaders;
        this.anyOrigin = builder.anyOrigin;
        this.allowCookies = builder.allowCookies;
        this.maxAge = builder.maxAge;
        this.validRequestMethods = Collections.unmodifiableSet(builder.validRequestMethods);
        this.validRequestHeaders = Collections.unmodifiableSet(builder.validRequestHeaders);
    }
    
    /**
     * Returns a {@link Builder} instance which can be configured as needed. instance which can be configured as needed.
     * 
     * @return {@link Builder} to be used to configure and build a {@link CorsConfiguration} instance.
     */
    public static Builder create() {
        return new Builder();
    }
    
    /**
     * Returns a {@link CorsConfiguration} instance with the default values for all properties.
     * 
     * @return {@link CorsConfiguration} with default properites set.
     */
    public static CorsConfiguration defaultConfig() {
        return CorsConfig.create().build();
    }
    
    @Override
    public boolean isCorsSupportEnabled() {
        return corsSupportEnabled;
    }
    
    @Override
    public boolean exposeHeaders() {
        return exposeHeaders != null;
    }
    
    @Override
    public String getExposeHeaders() {
        return exposeHeaders;
    }
    
    @Override
    public boolean anyOrigin() {
        return anyOrigin;
    }
    
    @Override
    public boolean allowCookies() {
        return allowCookies;
    }
    
    @Override
    public boolean hasMaxAge() {
        return maxAge > -1;
    }
    
    @Override
    public long getMaxAge() {
        return maxAge;
    }
    
    @Override
    public Set<String> getValidRequestMethods() {
        return validRequestMethods;
    }
    
    @Override
    public Set<String> getValidRequestHeaders() {
        return validRequestHeaders;
    }
    
    @Override
    public String toString() {
        return new StringBuilder("CorsConfiguration[")
            .append("corsSupportEnabled=").append(corsSupportEnabled)
            .append(", exposeHeaders=").append(exposeHeaders)
            .append(", anyOrigin=").append(anyOrigin)
            .append(", allowCookies=").append(allowCookies)
            .append(", maxAge=").append(maxAge)
            .append(", validRequestHeaders=").append(validRequestHeaders)
            .append(", validRequestMethods=").append(validRequestMethods)
            .append("]").toString();
    }
    
    public static class Builder {
        private boolean corsSupportEnabled = true;
        private boolean anyOrigin;
        private boolean allowCookies;
        private long maxAge;
        private String exposeheaders;
        private Set<String> validRequestMethods = new HashSet<String>();
        private Set<String> validRequestHeaders = new HashSet<String>();
        
        public Builder() {
        }
        
        public Builder enableCorsSupport(final boolean enable) {
            corsSupportEnabled = enable;
            return this;
        }
        
        public Builder exposeHeaders(final String headers) {
            exposeheaders = headers;
            return this;
        }
        
        public Builder anyOrigin(final boolean anyOrigin) {
            this.anyOrigin = anyOrigin;
            return this;
        }
        
        public Builder allowCookies(final boolean allow) {
            allowCookies = allow;
            return this;
        }
        
        public Builder maxAge(final long age) {
            maxAge = age; 
            return this;
        }
        
        public Builder validRequestMethods(final RequestMethod... requestMethods) {
            validRequestMethods.addAll(asSet(requestMethods));
            return this;
        }
        
        public Builder validRequestHeaders(final String validHeaders) {
            validRequestHeaders.addAll(asSet(validHeaders, true));
            return this;
        }
        
        private Set<String> asSet(final RequestMethod... requestMethods) {
            final String join = Joiner.on(",").join(requestMethods);
            return asSet(join, false);
        }
        
        private Set<String> asSet(final String csvString, final boolean toLowerCase) {
            if (csvString == null) {
                return Collections.emptySet();
            }
            
            final Set<String> strings = new LinkedHashSet<String>();
            for (String string : Splitter.on(',').trimResults().split(csvString)) {
                strings.add(toLowerCase ? string.toLowerCase() : string);
            }
            return strings;
        }
        
        public CorsConfiguration build() {
            validRequestHeaders.add("origin");
            if (validRequestMethods.isEmpty()) {
                validRequestMethods(RequestMethod.values());
            }
            return new CorsConfig(this);
        }
        
    }
    
}
