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
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.aerogear.controller.log.AeroGearLogger;
import org.mvel2.templates.TemplateRuntime;

/**
 * This Servlet is used as a default error handling servlet when an explicit error route has
 * not been configured. 
 * </p>
 * 
 * @see ErrorHandler
 */
@WebServlet (value="/ErrorServlet")
public class ErrorServlet extends HttpServlet {

    private static final String TEMPLATE = "/org/jboss/aerogear/controller/router/error.html";
    private static final long serialVersionUID = 1L;
    
    /**
     * Writes a general error page response to the client. 
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        @SuppressWarnings("resource")
        final PrintWriter writer = resp.getWriter();
        final Throwable t = (Throwable) req.getAttribute(DefaultRouter.EXCEPTION_ATTRIBUTE_NAME);
        final String html = ErrorServlet.readTemplate(TEMPLATE, t);
        writer.write(html);
    }

    /**
     * Reads the template and makes Throwable available as a variable named 'exception'. 
     * </p>
     * The template language used by this method is MVEL2 (http://mvel.codehaus.org/).
     * 
     * @param templatePath the path to the template used for displaying the exception.
     * @param throwable the exception to be used in the target template.
     * @return {@code String} the result of  processing the passed-in template.
     */
    @SuppressWarnings("resource")
    public static String readTemplate(final String templatePath, final Throwable throwable) {
        InputStream in = null;
        try {
            in = ErrorServlet.class.getResourceAsStream(templatePath);
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
    
}
