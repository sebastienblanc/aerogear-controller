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

package org.jboss.aerogear.controller.router.parameter;


import com.google.common.base.Optional;

public class RequestParameter<T> extends Parameter<T> {

    private final String name;
    private final Optional<T> defaultValue;
    
    public RequestParameter(final String name, final Type parameterType, final Class<T> type) {
        this(name, parameterType, null, type);
    }
    
    public RequestParameter(final String name, final Type parameterType, final T defaultValue, final Class<T> type) {
        super(parameterType, type);
        this.name = name;
        this.defaultValue = Optional.fromNullable(defaultValue);
    }
    
    public String getName() {
        return name;
    }
    
    public Optional<T> getDefaultValue() {
        return defaultValue;
    }
    
    @Override
    public String toString() {
        return new StringBuilder("RequestParameter[name=")
                .append(name)
                .append(", parameterType=").append(getParameterType())
                .append(", type=").append(getType())
                .append(", defaultValue=").append(defaultValue)
                .append("]").toString();
    }

}
