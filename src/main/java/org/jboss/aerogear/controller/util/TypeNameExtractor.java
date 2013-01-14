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

package org.jboss.aerogear.controller.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collection;

public class TypeNameExtractor {
    public String nameFor(Type generic) {
        if (generic instanceof ParameterizedType) {
            return nameFor((ParameterizedType) generic);
        }

        if (generic instanceof WildcardType) {
            return nameFor((WildcardType) generic);
        }

        if (generic instanceof TypeVariable<?>) {
            return nameFor(((TypeVariable<?>) generic));
        }

        return nameFor((Class<?>) generic);
    }

    private String nameFor(Class<?> raw) {
        if (raw.isArray()) {
            return nameFor(raw.getComponentType()) + "List";
        }

        String name = raw.getSimpleName();

        return StringUtils.downCaseFirst(name);
    }

    private String nameFor(TypeVariable<?> variable) {
        return StringUtils.downCaseFirst(variable.getName());
    }

    private String nameFor(WildcardType wild) {
        if ((wild.getLowerBounds().length != 0)) {
            return nameFor(wild.getLowerBounds()[0]);
        } else {
            return nameFor(wild.getUpperBounds()[0]);
        }
    }

    private String nameFor(ParameterizedType type) {
        Class<?> raw = (Class<?>) type.getRawType();
        if (Collection.class.isAssignableFrom(raw)) {
            return nameFor(type.getActualTypeArguments()[0]) + "List";
        }
        return nameFor(raw);
    }

}
