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

package org.jboss.aerogear.controller.router;

import javax.inject.Inject;

import org.jboss.aerogear.controller.view.View;
import org.jboss.aerogear.controller.view.ViewResolver;

/**
 * A Responder that "responds" to request by forwarding them to a {@link View}.
 */
public class MvcResponder implements Responder {
    
    private ViewResolver viewResolver;

    @Inject
    public MvcResponder(final ViewResolver viewResolver) {
        this.viewResolver = viewResolver;
    }

    @Override
    public boolean accepts(final String mediaType) {
        return MediaType.HTML.toString().equals(mediaType);
    }

    @Override
    public void respond(final Object entity, final RouteContext routeContext) throws Exception {
        String viewPath = viewResolver.resolveViewPathFor(routeContext.getRoute());
        View view = new View(viewPath, entity);
        if (view.hasModelData()) {
            routeContext.getRequest().setAttribute(view.getModelName(), view.getModel());
        }
        routeContext.getRequest().getRequestDispatcher(view.getViewPath()).forward(routeContext.getRequest(), routeContext.getResponse());
        
    }

    @Override
    public String mediaType() {
        return MediaType.HTML.toString();
    }

}
