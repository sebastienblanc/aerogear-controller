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

package org.jboss.aerogear.controller.router.rest;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.jboss.aerogear.controller.router.MediaType;
import org.jboss.aerogear.controller.router.Responder;
import org.jboss.aerogear.controller.router.Route;
import org.jboss.aerogear.controller.router.RouteContext;

/**
 * AbstractRestResponder is a Responder capable of returning a response to a request.
 * </p>
 * This class handles common task such as implementing {@link #accepts(String)} and making
 * sure that certain HTTP headers are always set on the {@link HttpServletResponse}, for example
 * that the 'Content-Type' header is set to the media type that this Responder accepts.
 * 
 */
public abstract class AbstractRestResponder implements Responder {
    
    private final MediaType mediaType;
    
    /**
     * Sole constructor that subclasses should call from tier no-args constructor
     * 
     * @param mediaType the media type that this responder accepts.
     */
    public AbstractRestResponder(final MediaType mediaType) {
        this.mediaType = mediaType;
    }
    
    /**
     * Writes the passed-in entity to the {@link HttpServletResponse} enabling concrete implementation
     * to add additional headers of in other ways process the response. 
     * 
     * @param entity the entity returned from a {@link Route} endpoint.
     * @param routeContext the current {@link RouteContext}.
     * @throws Exception if an error occurs while responding.
     */
    public abstract void writeResponse(final Object entity, final RouteContext routeContext) throws Exception;

    @Override
    public boolean accepts(final String mediaType) {
        return this.mediaType.getMediaType().equals(mediaType);
    }
    
    @Override
    public void respond(final Object entity, final RouteContext routeContext) throws Exception {
        final HttpServletResponse response = routeContext.getResponse();
        response.setContentType(mediaType.getMediaType());
        response.setCharacterEncoding("UTF-8");
        if (entity instanceof ResponseHeaders) {
            final ResponseHeaders responseHeaders = (ResponseHeaders) entity;
            final Map<String, String> headers = responseHeaders.headers();
            for (Entry<String, String> entrySet : headers.entrySet()) {
                response.setHeader(entrySet.getKey(), entrySet.getValue());
            }
        }
        writeResponse(entity, routeContext);
    }
    
    @Override
    public MediaType mediaType() {
        return mediaType;
    }
    
}
