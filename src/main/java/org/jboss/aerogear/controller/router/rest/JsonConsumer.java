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

package org.jboss.aerogear.controller.router.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.aerogear.controller.router.AeroGearException;
import org.jboss.aerogear.controller.router.Consumer;
import org.jboss.aerogear.controller.router.MediaType;

/**
 * Concrete Consumer that is able to unmarshall a Http request body into a Java representation.
 * </p>
 * JSON support is provided by Jackson.
 */
public class JsonConsumer implements Consumer {
    
    @Override
    public String mediaType() {
        return MediaType.JSON.toString();
    }

    @Override
    public <T> T unmarshall(final HttpServletRequest request, final Class<T> type) {
        try {
            final ObjectMapper om = new ObjectMapper();
            return om.readValue(request.getInputStream(), type);
        } catch (final IOException e) {
            throw new AeroGearException(e);
        }
    }

    @Override
    public String toString() {
        return "JsonConsumer[mediaType=" + mediaType() + "]";
    }
}
