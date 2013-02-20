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

package org.jboss.aerogear.controller.filter;

import static javax.servlet.DispatcherType.FORWARD;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.jboss.aerogear.controller.log.AeroGearLogger;
import org.jboss.aerogear.controller.router.decorators.ErrorHandler;
import org.jboss.aerogear.controller.router.error.ErrorRoute;
import org.mvel2.templates.TemplateRuntime;

/**
 * This Filter is used for default error handling when no explicit error route has configured. </p>
 * 
 * @see ErrorHandler
 */
@WebFilter(filterName = "aerogear-error-filter", urlPatterns = { "/ErrorFilter" }, dispatcherTypes = { FORWARD })
public class ErrorFilter implements Filter {

    private static final String TEMPLATE = "/org/jboss/aerogear/controller/router/error.html";

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    /**
     * Writes a general error page response to the client.
     */
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        final Throwable throwable = (Throwable) request.getAttribute(ErrorRoute.DEFAULT.getExceptionAttrName());
        final String html = ErrorFilter.readTemplate(TEMPLATE, throwable);
        response.getWriter().write(html);
    }

    /**
     * Reads the template and makes Throwable available as a variable named 'exception'. </p> The template language used by this
     * method is MVEL2 (http://mvel.codehaus.org/).
     * 
     * @param templatePath the path to the template used for displaying the exception.
     * @param throwable the exception to be used in the target template.
     * @return {@code String} the result of processing the passed-in template.
     */
    @SuppressWarnings("resource")
    public static String readTemplate(final String templatePath, final Throwable throwable) {
        InputStream in = null;
        try {
            in = ErrorFilter.class.getResourceAsStream(templatePath);
            final Map<String, Object> templateParameters = new HashMap<String, Object>();
            templateParameters.put("exception", throwable);
            return (String) TemplateRuntime.eval(in, templateParameters);
        } finally {
            safeClose(in);
        }
    }

    private static void safeClose(final InputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (final IOException e) {
                AeroGearLogger.LOGGER.closeInputStream(e);
            }
        }
    }

    @Override
    public void destroy() {

    }

}
