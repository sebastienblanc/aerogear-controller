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

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import org.jboss.aerogear.controller.router.parameter.Parameter;

/**
 * An object that contains information required to route HTTP requests to a target class's method.
 */
public interface Route {

    /**
     * Gets the {@link RequestMethod}s for this Route.
     * 
     * @return the {@link RequestMethod}s, or an empty set.
     */
    Set<RequestMethod> getMethods();

    /**
     * Gets the path for this Route.
     * 
     * @return the path for this route, or null if no path exists for this Route.
     */
    String getPath();

    /**
     * Gets the {@link Method} of the target class for this Route.
     * 
     * @return the target method.
     */
    Method getTargetMethod();

    /**
     * Gets the target class for this Route
     * 
     * @return the target class.
     */
    Class<?> getTargetClass();

    /**
     * Determines if this Route can handle the {@link RequestMethod} and path combination.
     * 
     * @param method the http request methods.
     * @param path the request path.
     * @param acceptHeaders the accept headers provided, or an empty set if none were provided.
     * @return {@code true} if this Route can handle the method and path passed in, {@code false} otherwise.
     */
    boolean matches(RequestMethod method, String path, Set<String> acceptHeaders);

    /**
     * Determines if this Route's path is parameterized.
     * 
     * @return {@code true} if this Route's path parameterized, otherwise {@code false}.
     */
    boolean isParameterized();

    /**
     * Determines if this Route has roles configured.
     * 
     * @return {@code true} if this Route has roles associated with its Route.
     */
    boolean isSecured();

    /**
     * Gets this Routes associated roles.
     * 
     * @return the roles associated with this Route, or an empty set if there are no roles associated.
     */
    Set<String> getRoles();

    /**
     * Determines if this Route contains one or more exception routes.
     * 
     * @return {@code true} if this Route has one or more exception routes.
     */
    boolean hasExceptionsRoutes();

    /**
     * Determines if this Route can handle the throwable.
     * 
     * @param throwable
     * @return {@code true} if this Route can handle the Throwable, otherwise {@code false}.
     */
    boolean canHandle(Throwable throwable);

    /**
     * Returns the media types that this Route is capable of serving.
     * 
     * @return the media types that this routes can produce.
     */
    Set<MediaType> produces();

    /**
     * Returns the Parameter's that this route accepts.
     * 
     * @return the {@link Parameter}'s that this route accepts.
     */
    List<Parameter<?>> getParameters();

    /**
     * Returns the media types that this Route is capable of consuming.
     * 
     * @return the media types that this routes can consuming.
     */
    Set<String> consumes();

}
