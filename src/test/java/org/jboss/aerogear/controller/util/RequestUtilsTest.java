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

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.jboss.aerogear.controller.RoutesTest.Car;
import org.jboss.aerogear.controller.SampleController;
import org.jboss.aerogear.controller.router.MediaType;
import org.jboss.aerogear.controller.router.Parameter;
import org.jboss.aerogear.controller.router.RequestMethod;
import org.jboss.aerogear.controller.router.Route;
import org.jboss.aerogear.controller.router.RouteContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.base.Optional;

public class RequestUtilsTest {
    
    @Mock
    private ServletContext servletContext;
    @Mock
    private HttpServletRequest request;
    @Mock
    private Route route;
    @Mock
    private RouteContext routeContext;
    
    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        when(request.getServletContext()).thenReturn(servletContext);
    }
    
    @Test
    public void extractPath() {
        when(servletContext.getContextPath()).thenReturn("/myapp");
        when(request.getRequestURI()).thenReturn("/myapp/cars/1");
        assertThat(RequestUtils.extractPath(request)).isEqualTo("/cars/1");
    }
    
    @Test
    public void extractPathDefaultWebApp() {
        when(servletContext.getContextPath()).thenReturn("");
        when(request.getRequestURI()).thenReturn("/cars/1");
        assertThat(RequestUtils.extractPath(request)).isEqualTo("/cars/1");
    }
    
    @Test
    public void extractMethod() {
        when(request.getMethod()).thenReturn("GET");
        assertThat(RequestUtils.extractMethod(request)).isEqualTo(RequestMethod.GET);
        when(request.getMethod()).thenReturn("PUT");
        assertThat(RequestUtils.extractMethod(request)).isEqualTo(RequestMethod.PUT);
        when(request.getMethod()).thenReturn("POST");
        assertThat(RequestUtils.extractMethod(request)).isEqualTo(RequestMethod.POST);
        when(request.getMethod()).thenReturn("DELETE");
        assertThat(RequestUtils.extractMethod(request)).isEqualTo(RequestMethod.DELETE);
        when(request.getMethod()).thenReturn("OPTIONS");
        assertThat(RequestUtils.extractMethod(request)).isEqualTo(RequestMethod.OPTIONS);
        when(request.getMethod()).thenReturn("HEAD");
        assertThat(RequestUtils.extractMethod(request)).isEqualTo(RequestMethod.HEAD);
        when(request.getMethod()).thenReturn("PATCH");
        assertThat(RequestUtils.extractMethod(request)).isEqualTo(RequestMethod.PATCH);
    }
    
    @Test
    public void extractAcceptsHeaderMissing() {
        assertThat(RequestUtils.extractAcceptHeader(request).isEmpty()).isTrue();
    }
    
    @Test
    public void extractAcceptsHeader() {
        when(request.getHeader("Accept")).thenReturn("application/json, application/xml");
        assertThat(RequestUtils.extractAcceptHeader(request)).contains(MediaType.JSON.toString(), "application/xml");
    }
    
    @Test
    public void extractPathParameters() {
        when(route.getPath()).thenReturn("/cars/{id}");
        final String param = RequestUtils.extractPathParameters("/cars/2", route);
        assertThat(param).isEqualTo("2");
    }
    
    @Test
    public void extractIogiParams() throws Exception {
        when(route.getTargetMethod()).thenReturn(SampleController.class.getMethod("save", Car.class));
        final Optional<?> optional = RequestUtils.extractIogiParam(RequestParams.param("car.name", "Herbi").getParamMap(), route);
        assertThat(((Car)optional.get()).getName()).isEqualTo("Herbi");
    }
    
    @Test
    public void extractIogiParamsNone() throws Exception {
        when(route.getTargetMethod()).thenReturn(SampleController.class.getMethod("save", Car.class));
        final Optional<?> optional = RequestUtils.extractIogiParam(RequestParams.param("name", "Herbi").getParamMap(), route);
        assertThat(optional.isPresent()).isFalse();
    }
    
    @Test
    public void extractPathParam() {
        when(request.getParameterMap()).thenReturn(RequestParams.empty());
        when(route.getParameters()).thenReturn(Parameters.param("id", Parameter.Type.PATH).asList());
        when(route.getPath()).thenReturn("/cars/{id}");
        when(routeContext.getRequestPath()).thenReturn("/cars/2");
        when(routeContext.getRequest()).thenReturn(request);
        when(routeContext.getRoute()).thenReturn(route);
        
        final Object[] args = RequestUtils.extractArguments(routeContext);
        assertThat(args[0]).isEqualTo("2");
    }
    
    @Test
    public void extractFormParams() throws Exception{
        when(request.getParameterMap()).thenReturn(RequestParams.param("name", "Newman").getParamMap());
        when(route.getParameters()).thenReturn(Parameters.param("name", Parameter.Type.FORM).asList());
        when(route.getTargetMethod()).thenReturn(SampleController.class.getMethod("client", String.class));
        when(route.getPath()).thenReturn("/cars");
        when(routeContext.getRequestPath()).thenReturn("/cars");
        when(routeContext.getRequest()).thenReturn(request);
        when(routeContext.getRoute()).thenReturn(route);
        
        final Object[] args = RequestUtils.extractArguments(routeContext);
        assertThat(args[0]).isEqualTo("Newman");
    }
    
    @Test
    public void extractFormParamsWithDefaultValue() throws Exception{
        when(request.getParameterMap()).thenReturn(RequestParams.empty());
        when(route.getParameters()).thenReturn(Parameters.param("name", Parameter.Type.FORM, "defaultName").asList());
        when(route.getTargetMethod()).thenReturn(SampleController.class.getMethod("client", String.class));
        when(route.getPath()).thenReturn("/cars");
        when(routeContext.getRequestPath()).thenReturn("/cars");
        when(routeContext.getRequest()).thenReturn(request);
        when(routeContext.getRoute()).thenReturn(route);
        
        final Object[] args = RequestUtils.extractArguments(routeContext);
        assertThat(args[0]).isEqualTo("defaultName");
    }
    
    @Test
    public void extractFormParamIogi() throws Exception {
        when(request.getParameterMap()).thenReturn(RequestParams.param("car.name", "Herbi").getParamMap());
        final Parameters parameters = Parameters.param("car.name", Parameter.Type.FORM);
        when(route.getParameters()).thenReturn(parameters.asList());
        when(route.getTargetMethod()).thenReturn(SampleController.class.getMethod("save", Car.class, String.class));
        when(route.getPath()).thenReturn("/cars");
        when(routeContext.getRequestPath()).thenReturn("/cars");
        when(routeContext.getRequest()).thenReturn(request);
        when(routeContext.getRoute()).thenReturn(route);
        
        final Object[] args = RequestUtils.extractArguments(routeContext);
        assertThat(args[0] instanceof Car).isTrue();
        assertThat(((Car)args[0]).getName()).isEqualTo("Herbi");
    }
    
    @Test
    public void extractHeaderParam() {
        when(request.getHeader("x-header")).thenReturn("headerValue");
        when(route.getParameters()).thenReturn(Parameters.param("x-header", Parameter.Type.HEADER).asList());
        when(route.getPath()).thenReturn("/cars/{id}");
        when(routeContext.getRequestPath()).thenReturn("/cars/2");
        when(routeContext.getRequest()).thenReturn(request);
        when(routeContext.getRoute()).thenReturn(route);
        
        final Object[] args = RequestUtils.extractArguments(routeContext);
        assertThat(args[0]).isEqualTo("headerValue");
    }
    
    @Test
    public void extractHeaderParamWithDefaultValue() {
        when(route.getParameters()).thenReturn(Parameters.param("x-header", Parameter.Type.HEADER, "defaultHeader").asList());
        when(route.getPath()).thenReturn("/cars");
        when(routeContext.getRequestPath()).thenReturn("/cars");
        when(routeContext.getRequest()).thenReturn(request);
        when(routeContext.getRoute()).thenReturn(route);
        
        final Object[] args = RequestUtils.extractArguments(routeContext);
        assertThat(args[0]).isEqualTo("defaultHeader");
    }
    
    @Test
    public void extractQueryParams() {
        when(request.getParameterMap()).thenReturn(RequestParams.param("brand", "mini").add("year", "2006").getParamMap());
        when(route.getParameters()).thenReturn(Parameters.param("brand", Parameter.Type.QUERY).add("year", Parameter.Type.QUERY).asList());
        when(route.getPath()).thenReturn("/cars");
        when(routeContext.getRequestPath()).thenReturn("/cars");
        when(routeContext.getRequest()).thenReturn(request);
        when(routeContext.getRoute()).thenReturn(route);
        
        final Object[] args = RequestUtils.extractArguments(routeContext);
        assertThat(args[0]).isEqualTo("mini");
        assertThat(args[1]).isEqualTo("2006");
    }
    
    @Test
    public void extractQueryParamsWithDefaultValue() {
        when(request.getParameterMap()).thenReturn(RequestParams.param("brand", "mini").getParamMap());
        final Parameters parameters = Parameters.param("brand", Parameter.Type.QUERY).add("year", Parameter.Type.QUERY, "2012");
        when(route.getParameters()).thenReturn(parameters.asList());
        when(route.getPath()).thenReturn("/cars");
        when(routeContext.getRequestPath()).thenReturn("/cars");
        when(routeContext.getRequest()).thenReturn(request);
        when(routeContext.getRoute()).thenReturn(route);
        
        final Object[] args = RequestUtils.extractArguments(routeContext);
        assertThat(args[0]).isEqualTo("mini");
        assertThat(args[1]).isEqualTo("2012");
    }
    
    @Test
    public void extractCookieParam() {
        final Cookie cookie = mock(Cookie.class);
        when(cookie.getName()).thenReturn("testCookie");
        when(cookie.getValue()).thenReturn("cookieValue");
        when(request.getCookies()).thenReturn(new Cookie[] {cookie});
        when(route.getParameters()).thenReturn(Parameters.param("testCookie", Parameter.Type.COOKIE).asList());
        when(route.getPath()).thenReturn("/cars");
        when(routeContext.getRequestPath()).thenReturn("/cars");
        when(routeContext.getRequest()).thenReturn(request);
        when(routeContext.getRoute()).thenReturn(route);
        
        final Object[] args = RequestUtils.extractArguments(routeContext);
        assertThat(args[0]).isEqualTo("cookieValue");
    }
    
    private static class Parameters {
        private final List<Parameter> parameters = new LinkedList<Parameter>();
        
        public static Parameters param(final String name, final Parameter.Type type) {
            return new Parameters().add(name, type);
        }
        
        public static Parameters param(final String name, final Parameter.Type type, final String defaultValue)  {
            return new Parameters().add(name, type, defaultValue);
        }
        
        public Parameters add(final String name, final Parameter.Type type) {
            parameters.add(new Parameter(name, type));
            return this;
        }
        
        public Parameters add(final String name, final Parameter.Type type, final String defaultValue) {
            parameters.add(new Parameter(name, type, defaultValue));
            return this;
        }

        public List<Parameter> asList() {
            return parameters;
        }
        
    }
    
    private static class RequestParams {
        
        final Map<String, String[]> paramMap = new HashMap<String, String[]>();
        
        private RequestParams(final String name, final String value) {
            add(name, value);
        }
        
        public static RequestParams param(final String name, final String value) {
            return new RequestParams(name, value);
        }
        
        public static Map<String, String[]> empty() {
            return Collections.emptyMap();
        }
        
        public RequestParams add(final String name, final String value) {
            paramMap.put(name, new String[] {value});
            return this;
        }

        public Map<String, String[]> getParamMap() {
            return paramMap;
        }
        
    }
    
}
