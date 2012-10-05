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

package org.jboss.aerogear.controller.router;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.servlet.annotation.WebServlet;

import org.jboss.aerogear.controller.RequestMethod;
import org.jboss.aerogear.controller.log.AeroGearLogger;
import org.mvel2.templates.TemplateRuntime;

/**
 * ErrorHandler is used as default target for a {@link Route} when a route has not defined any 
 * explicit error route.
 */
public class ErrorHandler {
    
    private static final Route DEFAULT_ERROR_ROUTE = ErrorHandler.createDefaultErrorRoute();
    
    /**
     * Simply logs the exception thown.
     * 
     * @param throwable the error to be logged.
     */
    public void error(final Throwable throwable) {
        AeroGearLogger.LOGGER.routeCatchAllException(throwable);
    }
    
    /**
     * Returns an {@link Route} which is configured to route to an instance of {@link ErrorHandler}.
     * This Route is provided as a fallback when a route has no explicit error route  defined.
     */
    public static Route defaultErrorRoute() {
        return DEFAULT_ERROR_ROUTE;
    }
    
    /**
     * Reads the template and makes Throwable available as a variable named 'exception'. 
     * </p>
     * The template language used by this method is MVEL2 (http://http://mvel.codehaus.org/).
     * 
     * @param templatePath the path to the template used for displaying the exception.
     * @param throwable the exception to be used in the target template.
     * @return {@code String} the result of  processing the passed-in template.
     */
    public static String readTemplate(final String templatePath, final Throwable throwable) {
        InputStream in = null;
        try {
            in = ErrorHandler.class.getResourceAsStream(templatePath);
            final Map<String, Object> vars = new HashMap<String, Object>();
            vars.put("exception", throwable);
            return (String) TemplateRuntime.eval(in, vars);
        } finally {
            safeClose(in);
        }
    }

    private static void safeClose(InputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (final IOException e) {
                AeroGearLogger.LOGGER.closeInputStream(e);
            }
        }
        
    }

    @SuppressWarnings("unchecked")
    private static Route createDefaultErrorRoute() {
        return new DefaultRoute(ErrorServlet.class.getAnnotation(WebServlet.class).value()[0], 
                new RequestMethod[]{RequestMethod.GET}, 
                ErrorHandler.class, targetMethod("error"),
                new HashSet<Class<? extends Throwable>>(Arrays.asList(Throwable.class)));
    }

    private static final Method targetMethod(final String methodName) {
        try {
            return ErrorHandler.class.getDeclaredMethod(methodName, Throwable.class);
        } catch (final Exception e) {
            throw new AeroGearException("Could not find a method named '" + methodName + "' on target class", e);
        }
    }

}
