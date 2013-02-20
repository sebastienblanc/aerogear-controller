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

package org.jboss.aerogear.controller.router.decorators.cors;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.aerogear.controller.log.AeroGearLogger;
import org.jboss.aerogear.controller.router.RequestMethod;
import org.jboss.aerogear.controller.router.Router;
import org.jboss.aerogear.controller.util.RequestUtils;

/**
 * CorsHandler is a CDI decorator that decorates {@link Router} adding <a href="http://www.w3.org/TR/cors/">CORS</a> support.
 * 
 * @see Cors
 * @see CorsConfiguration
 */
@Decorator
public class CorsHandler implements Router {

    private final Router delegate;
    private final CorsConfiguration corsConfig;

    @Inject
    public CorsHandler(final @Delegate Router delegate, final Instance<CorsConfiguration> corsConfigInstance) {
        this.delegate = delegate;
        this.corsConfig = corsConfigInstance.isUnsatisfied() ? CorsConfig.defaultConfig() : corsConfigInstance.get();
    }

    @Override
    public boolean hasRouteFor(final HttpServletRequest request) {
        if (corsConfig.isCorsSupportEnabled() && RequestUtils.extractMethod(request).equals(RequestMethod.OPTIONS)) {
            return true;
        }
        return delegate.hasRouteFor(request);
    }

    @Override
    public void dispatch(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
            throws ServletException {
        final Cors cors = new Cors(corsConfig, request);
        if (cors.canHandleRequest()) {
            if (cors.isPreflightRequest()) {
                handlePreflight(cors, response);
                return;
            } else {
                handleSimpleRequest(cors, response);
            }
        }
        delegate.dispatch(request, response, chain);
    }

    private void handleSimpleRequest(final Cors cors, final HttpServletResponse response) {
        cors.setAllowCredentials(response).setOrigin(response).setExposeHeaders(response);
    }

    private void handlePreflight(final Cors cors, final HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
        if (!cors.isRequestMethodValid()) {
            AeroGearLogger.LOGGER.badCorsRequestMethod(cors.getRequestMethod(), cors.getAllowedRequestMethods());
            return;
        }

        if (cors.hasRequestHeaders()) {
            if (!cors.areRequestHeadersValid()) {
                AeroGearLogger.LOGGER.badCorsRequestHeaders(cors.getRequestHeaders(), cors.getAllowedRequestHeaders());
                return;
            } else {
                cors.setAllowHeaders(response);
            }
        }
        cors.setAllowMethods(response).setAllowCredentials(response).setOrigin(response).setMaxAge(response);
    }

}
