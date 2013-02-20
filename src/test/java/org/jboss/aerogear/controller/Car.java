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

package org.jboss.aerogear.controller;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class Car {

    private final String color;
    private final String brand;

    @JsonCreator
    public Car(@JsonProperty("color") final String color, @JsonProperty("brand") final String brand) {
        this.color = color;
        this.brand = brand;
    }

    public String getColor() {
        return color;
    }

    public String getBrand() {
        return brand;
    }

    @Override
    public String toString() {
        return "Car[color=" + color + ", brand=" + brand + "]";
    }

}
