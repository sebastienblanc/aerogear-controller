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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.aerogear.controller.util.RequestUtils;

/**
 * RouteContext holds information related to processing of a Route.
 */
public class RouteContext {

    private final Route route;
    private final String requestPath;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final Routes routes;

    /**
     * Constructor.
     * 
     * @param route the {@link HttpServletRequest}.
     * @param request the {@link HttpServletRequest}.
     * @param response the {@link HttpServletRequest}.
     * @param routes the {@link Routes} to enables querying of information about configured routes.
     */
    public RouteContext(final Route route, final HttpServletRequest request, final HttpServletResponse response,
            final Routes routes) {
        this(route, RequestUtils.extractPath(request), request, response, routes);
    }

    public RouteContext(final Route route, final String requestPath, final HttpServletRequest request,
            final HttpServletResponse response, final Routes routes) {
        this.route = route;
        this.requestPath = requestPath;
        this.request = request;
        this.response = response;
        this.routes = routes;
    }

    /**
     * Returns the current Route
     * 
     * @return {@link Route} the current route.
     */
    public Route getRoute() {
        return route;
    }

    /**
     * Returns the request path minus the context path (suffix) for the current request.
     * 
     * @return {@code String} the request path minus the context path (suffix) for the current request.
     */
    public String getRequestPath() {
        return requestPath;
    }

    /**
     * Returns the current {@link HttpServletRequest}.
     * 
     * @return {@link HttpServletRequest} the current {@link HttpServletRequest}.
     */
    public HttpServletRequest getRequest() {
        return request;
    }

    /**
     * Returns the current {@link HttpServletResponse}.
     * 
     * @return {@link HttpServletResponse} the current {@link HttpServletResponse}.
     */
    public HttpServletResponse getResponse() {
        return response;
    }

    /**
     * Returns the {@link Routes} instance for the current application.
     * 
     * @return {@link Routes} instance which contains all of the routes of the current application.
     */
    public Routes getRoutes() {
        return routes;
    }

}
