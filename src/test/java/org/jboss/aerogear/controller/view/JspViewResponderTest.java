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

package org.jboss.aerogear.controller.view;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.aerogear.controller.SampleController;
import org.jboss.aerogear.controller.router.MediaType;
import org.jboss.aerogear.controller.router.Route;
import org.jboss.aerogear.controller.router.RouteContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class JspViewResponderTest {
    
    @Mock
    private Route route;
    @Mock
    private HttpServletRequest request;
    @Mock
    private RequestDispatcher dispatcher;
    @Mock 
    private HttpServletResponse response;
    @Mock
    private RouteContext routeContext;
    
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        doReturn(SampleController.class).when(route).getTargetClass();
        doReturn(SampleController.class.getMethod("index")).when(route).getTargetMethod();
        when(routeContext.getRoute()).thenReturn(route);
        when(routeContext.getRequest()).thenReturn(request);
        when(routeContext.getResponse()).thenReturn(response);
    }

    @Test
    public void acceptsJspViewResolver() {
        final JspViewResponder responder = new JspViewResponder();
        assertThat(responder.accepts(MediaType.HTML.getMediaType())).isTrue();
        assertThat(responder.accepts(null)).isFalse();
    }
    
    @Test
    public void respondWithJspViewResolver() throws Exception {
        final String viewPath = "/WEB-INF/pages/SampleController/index.jsp";
        final Car car = new Car();
        when(request.getRequestDispatcher(viewPath)).thenReturn(dispatcher);
        new JspViewResponder().respond(car, routeContext);
        verify(request).setAttribute("car", car);
        verify(request).getRequestDispatcher(viewPath);
        verify(dispatcher).forward(request, response);
    }
    
    private static class Car {
    }
    
}
