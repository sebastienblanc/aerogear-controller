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

package org.jboss.aerogear.controller.view;

import org.jboss.aerogear.controller.router.MediaType;
import org.jboss.aerogear.controller.router.Responder;
import org.jboss.aerogear.controller.router.RouteContext;

/**
 * A Responder that "responds" to requests by forwarding them to a {@link View}.
 * 
 * @see ViewResolver
 */
public class AbstractViewResponder implements Responder {
    
    private final ViewResolver viewResolver;
    private final MediaType mediaType;
    
    public AbstractViewResponder(final ViewResolver viewResolver, final MediaType mediaType) {
        this.viewResolver = viewResolver;
        this.mediaType = mediaType;
    }

    @Override
    public boolean accepts(final String mediaType) {
        return this.mediaType.getMediaType().equals(mediaType);
    }

    @Override
    public void respond(final Object entity, final RouteContext routeContext) throws Exception {
        final String viewPath = viewResolver.resolveViewPathFor(routeContext.getRoute());
        final View view = new View(viewPath, entity);
        if (view.hasModelData()) {
            routeContext.getRequest().setAttribute(view.getModelName(), view.getModel());
        }
        routeContext.getRequest().getRequestDispatcher(view.getViewPath()).forward(routeContext.getRequest(), routeContext.getResponse());
    }

    @Override
    public MediaType mediaType() {
        return mediaType;
    }
    
}
