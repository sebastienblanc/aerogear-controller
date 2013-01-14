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


/**
 * Parameter represents a single target endpoint method parameter.
 * 
 * @param T the type of this parameter.
 */
public class Parameter <T> {
    
    public enum Type {
        REQUEST,
        ENTITY,
    }
    
    private final Type parameterType;
    private final Class<T> type;
    
    public Parameter(final Type parameterType, final Class<T> type) {
        this.parameterType = parameterType;
        this.type = type;
    }
    
    public Type getParameterType() {
        return parameterType;
    }
    
    public Class<?> getType() {
        return type;
    }
    
    @Override
    public String toString() {
        return "Parameter[type=" + parameterType + ", type=" + type + "]";
    }

}
