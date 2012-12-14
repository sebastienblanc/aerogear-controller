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

package org.jboss.aerogear.controller.router;

/**
 * Parameter represents a single target endpoint method parameter.
 */
public class Parameter {
    
    public enum Type {
        PATH,
        HEADER,
        QUERY,
        COOKIE,
        FORM,
        MATRIX
    }
    
    private final String name;
    private final Type parameterType;
    private final String defaultValue;
    
    public Parameter(final Type parameterType) {
        this("", parameterType, "");
    }
    
    public Parameter(final String name, final Type parameterType) {
        this(name, parameterType, "");
    }
    
    public Parameter(final String name, final Type parameterType, final String defaultValue) {
        this.name = name;
        this.parameterType = parameterType;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public Type getParameterType() {
        return parameterType;
    }
    
    public String getDefaultValue() {
        return defaultValue;
    }
    
    public String toString() {
        return "Parameter[name=" + name + ", type=" + parameterType + ", defaultValue=" + defaultValue + "]";
    }

}
