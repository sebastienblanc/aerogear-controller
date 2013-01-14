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

public class SampleController {

    public void index() {
    }

    public void client(String name) {
    }

    public void lol() {
    }

    public void save(Car car) {
    }
    
    public void save(String color, String brand) {
    }
    
    public void save(Car car, String metadata) {
    }

    public void find(String id) {
    }
    
    public void find(String color, String brand) {
    }

    public void admin() {
    }
    
    public void error(final Exception e) {
    }

    public void throwSampleControllerException() throws SampleControllerException {
        throw new SampleControllerException("Bogus exception");
    }
    
    public void throwIllegalStateException() {
        throw new IllegalStateException("Bogus exception");
    }

    public void errorPage() {
    }
    
    public void superException() {
    }
    
    public void subException() {
    }
}
