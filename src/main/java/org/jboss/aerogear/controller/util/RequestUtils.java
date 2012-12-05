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

package org.jboss.aerogear.controller.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.jboss.aerogear.controller.log.AeroGearLogger;
import org.jboss.aerogear.controller.log.LoggerMessages;
import org.jboss.aerogear.controller.router.Parameter;
import org.jboss.aerogear.controller.router.RequestMethod;
import org.jboss.aerogear.controller.router.Route;
import org.jboss.aerogear.controller.router.RouteContext;

import br.com.caelum.iogi.Iogi;
import br.com.caelum.iogi.reflection.Target;
import br.com.caelum.iogi.util.DefaultLocaleProvider;
import br.com.caelum.iogi.util.NullDependencyProvider;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;

/**
 * Utility methods for various {@link HttpServletRequest} operation.
 */
public class RequestUtils {
    
    private static final Iogi IOGI = new Iogi(new NullDependencyProvider(), new DefaultLocaleProvider());
    
    private RequestUtils() {
    }
    
    /**
     * Returns the path of the current request with out the context path.
     * </p>
     * For example, if the web application was deployed with a context path of '/myapp',
     * and the request submitted was '/myapp/cars/12', this method would return '/cars/12'.
     * 
     * @param request the {@link HttpServletRequest}.
     * @return {@code String} the request path without the context path (suffix)
     */
    public static String extractPath(final HttpServletRequest request) {
        final String contextPath = request.getServletContext().getContextPath();
        return request.getRequestURI().substring(contextPath.length());
    }
    
    /**
     * Returns the {@link RequestMethod} for the passed-in {@link HttpServletRequest}.
     * 
     * @param httpServletRequest the {@link HttpServletRequest}
     * @return {@link RequestMethod} matching the Http Method of the request.
     */
    public static RequestMethod extractMethod(final HttpServletRequest httpServletRequest) {
        return RequestMethod.valueOf(httpServletRequest.getMethod());
    }
    
    /**
     * Returns the {@code Accept header} from the passed-in {@code HttpServletRequest}.
     * 
     * @param request the {@link HttpServletRequest}
     * @return {@code Set<String>} of the values of the Http Accept Header, or an empty list if
     * there was not Accept header
     */
    public static Set<String> extractAcceptHeader(final HttpServletRequest request) {
        final String acceptHeader = request.getHeader("Accept");
        if (acceptHeader == null) {
            return Collections.emptySet();
        }
        
        final Set<String> acceptHeaders = new LinkedHashSet<String>();
        for (String header : Splitter.on(',').trimResults().split(acceptHeader)) {
            acceptHeaders.add(header);
        }
        return acceptHeaders;
    }
    
    /**
     * Extracts a path parameter from the passed in request path.
     * 
     * @param requestPath  the path of a Http request.
     * @param route  the route which will be used to match the request path. Will contains a '{}'
     *               which will be used to match a request paramter value.  
     * @return {@code String} the extracted path parameter value.
     */
    public static String extractPathParameters(final String requestPath, final Route route) {
        // TODO: extract this from Resteasy
        final int paramOffset = route.getPath().indexOf('{');
        final CharSequence param = requestPath.subSequence(paramOffset, requestPath.length());
        return param.toString();
    }

    /**
     * Returns an instance of the type used in the parameter names using Iogi.
     * </p>
     * For example, having form parameters named 'car.color', 'car.brand', this method
     * would try to use those values to instantiate a new Car instance.
     * 
     * @param paramMap  the parameter map from the HttpServletRequest.
     * @param route  the route from which information about the target endpoint method can be inspected.
     * @return {@link Optional}  may contain the instantiated instance, else isPresent will return false.
     */
    public static Optional<?> extractIogiParam(final Map<String, String[]> paramMap, final Route route) {
        final LinkedList<br.com.caelum.iogi.parameters.Parameter> parameters = new LinkedList<br.com.caelum.iogi.parameters.Parameter>();
        for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
            final String[] value = entry.getValue();
            if (value.length == 1) {
                parameters.add(new br.com.caelum.iogi.parameters.Parameter(entry.getKey(), value[0]));
            } else {
                AeroGearLogger.LOGGER.multivaluedParamsUnsupported();
            }
        }
        final Class<?>[] parameterTypes = route.getTargetMethod().getParameterTypes();
        final Class<?> parameterType = parameterTypes[0];
        final Target<?> target = Target.create(parameterType, StringUtils.downCaseFirst(parameterType.getSimpleName()));
        return Optional.fromNullable(IOGI.instantiate(target, parameters.toArray(new br.com.caelum.iogi.parameters.Parameter[parameters.size()])));
    }
    
    /**
     * Extracts the arguments from the current request for the target route.
     * 
     * @param routeContext the {@link RouteContext}.
     * @return {@code Object[]} an array of Object matching the route targets parameters.
     */
    public static Object[] extractArguments(final RouteContext routeContext) {
        final Route route = routeContext.getRoute();
        final LinkedList<Object> args = new LinkedList<Object>();
        final Map<String, String[]> requestParameters = routeContext.getRequest().getParameterMap();
        
        for (Parameter parameter : route.getParameters()) {
            switch (parameter.getParameterType()) {
            case PATH:
                args.addAll(Arrays.asList(extractPathParameters(routeContext.getRequestPath(), route)));
                break;
            case FORM:
                final Optional<?> iogiArg = extractIogiParam(requestParameters, route);
                args.add(iogiArg.isPresent() ? iogiArg.get() : extractArgument(requestParameters, parameter));
                break;
            case HEADER:
                final String header = routeContext.getRequest().getHeader(parameter.getName());
                args.add(header != null ? header: parameter.getDefaultValue());
                break;
            case COOKIE:
                args.add(extractCookieValue(routeContext, parameter));
                break;
            default:
                args.add(extractArgument(requestParameters, parameter));
                break;
            }
        }
        return args.toArray();
    }
    
    private static String extractCookieValue(final RouteContext routeContext, final Parameter parameter) {
        final Cookie[] cookies = routeContext.getRequest().getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(parameter.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return parameter.getDefaultValue();
    }
    
    private static String extractArgument(final Map<String, String[]> requestParameters, final Parameter parameter) {
        final String[] values = requestParameters.get(parameter.getName());
        if (values == null) {
            return parameter.getDefaultValue();
        } else if (values.length == 1) {
            return values[0];
        } else {
            throw LoggerMessages.MESSAGES.multivaluedParamsUnsupported(parameter.getName());
        }
    }

}
