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
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This Servlet is used as a default error handling servlet is a explicit error route was
 * not configured. 
 * </p>
 * 
 * @see ErrorHandler
 */
@WebServlet (value="/ErrorServlet")
public class ErrorServlet extends HttpServlet {

    private static final String TEMPLATE = "/org/jboss/aerogrear/controller/router/error.html";
    private static final long serialVersionUID = 1L;

    /**
     * Writes a general error page response to the client. 
     */
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final PrintWriter writer = resp.getWriter();
        final Throwable t = (Throwable) req.getAttribute(DefaultRouter.EXCEPTION_ATTRIBUTE_NAME);
        final String html = ErrorHandler.readTemplate(TEMPLATE, t);
        writer.write(html);
    }
    
}
