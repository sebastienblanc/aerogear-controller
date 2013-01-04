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

import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.aerogear.controller.router.MediaType;
import org.jboss.aerogear.controller.router.Responder;
import org.jboss.aerogear.controller.router.RouteContext;

/**
 * A RESTFul {@link Responder} that is able to return JSON responses.
 * </p>
 * This implementation uses Jackson for JSON support.
 */
public class JsonResponder extends AbstractRestResponder {

    public JsonResponder() {
        super(MediaType.JSON.toString());
    }

    @Override
    public void writeResponse(final Object entity, final RouteContext routeContext) throws Exception {
        final ObjectMapper om = new ObjectMapper();
        om.writeValue(routeContext.getResponse().getWriter(), entity);
    }

    @Override
    public String mediaType() {
        return MediaType.JSON.toString();
    }

}
