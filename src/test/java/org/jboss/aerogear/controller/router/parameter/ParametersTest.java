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

package org.jboss.aerogear.controller.router.parameter;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.jboss.aerogear.controller.Car;
import org.jboss.aerogear.controller.SampleController;
import org.jboss.aerogear.controller.router.Consumer;
import org.jboss.aerogear.controller.router.Route;
import org.jboss.aerogear.controller.router.RouteContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.base.Optional;


public class ParametersTest {

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
        when(routeContext.getRequest()).thenReturn(request);
        when(routeContext.getRoute()).thenReturn(route);
        when(route.getPath()).thenReturn("/cars");
        when(routeContext.getRequestPath()).thenReturn("/cars");
    }
    
    @Test
    public void extractPathParameter() {
        when(route.getPath()).thenReturn("/cars/{id}");
        when(routeContext.getRequestPath()).thenReturn("/cars/2");
        final Optional<String> param = Parameters.extractPathParam(routeContext);
        assertThat(param.get()).isEqualTo("2");
    }
    
    @Test
    public void extractPathParameterButNoParamInRequest() {
        when(route.getPath()).thenReturn("/cars/{id}");
        when(routeContext.getRequestPath()).thenReturn("/c");
        assertThat(Parameters.extractPathParam(routeContext).isPresent()).isFalse();
    }
    
    @Test
    public void extractIogiParams() throws Exception {
        when(route.getTargetMethod()).thenReturn(SampleController.class.getMethod("save", Car.class));
        final Map<String, String[]> paramMap = RequestParams.param("car.color", "red").add("car.brand","Ferrari").getParamMap();
        when(request.getParameterMap()).thenReturn(paramMap);
        final Optional<?> optional = Parameters.extractIogiParam(routeContext);
        assertThat(((Car)optional.get()).getColor()).isEqualTo("red");
    }
    
    @Test
    public void extractIogiParamsNone() throws Exception {
        when(route.getTargetMethod()).thenReturn(SampleController.class.getMethod("save", Car.class));
        final Map<String, String[]> paramMap = RequestParams.param("name", "Herbie").getParamMap();
        when(request.getParameterMap()).thenReturn(paramMap);
        final Optional<?> optional = Parameters.extractIogiParam(routeContext);
        assertThat(optional.isPresent()).isFalse();
    }
    
    @Test
    public void extractPathParam() {
        when(request.getParameterMap()).thenReturn(RequestParams.empty());
        when(route.getParameters()).thenReturn(asList(Parameters.param("id", String.class)));
        when(route.getPath()).thenReturn("/cars/{id}");
        when(routeContext.getRequestPath()).thenReturn("/cars/2");
        final Map<String, Object> args = Parameters.extractArguments(routeContext, Collections.<String, Consumer>emptyMap());
        assertThat(args.get("id")).isEqualTo("2");
    }
    
    @Test
    public void extractFormParams() throws Exception{
        when(request.getParameterMap()).thenReturn(RequestParams.param("name", "Newman").getParamMap());
        when(route.getParameters()).thenReturn(asList(Parameters.param("name", String.class)));
        when(route.getTargetMethod()).thenReturn(SampleController.class.getMethod("client", String.class));
        final Map<String, Object> args = Parameters.extractArguments(routeContext, Collections.<String, Consumer>emptyMap());
        assertThat(args.get("name")).isEqualTo("Newman");
    }
    
    @Test
    public void extractFormParamsWithDefaultValue() throws Exception{
        when(request.getParameterMap()).thenReturn(RequestParams.empty());
        when(route.getParameters()).thenReturn(asList(Parameters.param("name", "defaultName", String.class)));
        when(route.getTargetMethod()).thenReturn(SampleController.class.getMethod("client", String.class));
        final Map<String, Object> args = Parameters.extractArguments(routeContext, Collections.<String, Consumer>emptyMap());
        assertThat(args.get("name")).isEqualTo("defaultName");
    }
    
    @Test (expected = RuntimeException.class)
    public void shouldThrowIfFormParamIsMissingFromRequest() throws Exception {
        when(route.getParameters()).thenReturn(asList(Parameters.param("name", null, String.class)));
        when(route.getTargetMethod()).thenReturn(SampleController.class.getMethod("client", String.class));
        Parameters.extractArguments(routeContext, Collections.<String, Consumer>emptyMap());
    }
    
    @Test
    public void extractFormParamIogi() throws Exception {
        final Map<String, String[]> paramMap = RequestParams.param("car.color", "red").add("car.brand","Ferrari").getParamMap();
        when(request.getParameterMap()).thenReturn(paramMap);
        when(route.getParameters()).thenReturn(asList(Parameters.param(Car.class)));
        when(route.getTargetMethod()).thenReturn(SampleController.class.getMethod("save", Car.class, String.class));
        final Map<String, Object> args = Parameters.extractArguments(routeContext, Collections.<String, Consumer>emptyMap());
        assertThat(args.get("entity") instanceof Car).isTrue();
        assertThat(((Car)args.get("entity")).getColor()).isEqualTo("red");
    }
    
    @Test
    public void extractHeaderParam() {
        when(request.getHeader("x-header")).thenReturn("headerValue");
        final List<Parameter<?>> parameters = asList(Parameters.param("x-header", "def", String.class));
        when(route.getParameters()).thenReturn(parameters);
        when(route.getPath()).thenReturn("/cars");
        when(routeContext.getRequestPath()).thenReturn("/cars");
        final Map<String, Object> args = Parameters.extractArguments(routeContext, Collections.<String, Consumer>emptyMap());
        assertThat(args.get("x-header")).isEqualTo("headerValue");
    }
    
    @Test
    public void extractHeaderParamWithDefaultValue() {
        final List<Parameter<?>> parameters = asList(Parameters.param("x-header", "defaultHeader", String.class));
        when(route.getParameters()).thenReturn(parameters);
        final Map<String, Object> args = Parameters.extractArguments(routeContext, Collections.<String, Consumer>emptyMap());
        assertThat(args.get("x-header")).isEqualTo("defaultHeader");
    }
    
    @Test (expected = RuntimeException.class)
    public void shouldThrowIsHeaderParamIsMissingFromRequest() {
        final List<Parameter<?>> parameters = asList(Parameters.param("x-header", String.class));
        when(route.getParameters()).thenReturn(parameters);
        Parameters.extractArguments(routeContext, Collections.<String, Consumer>emptyMap());
    }
    
    @Test
    public void extractQueryParams() {
        when(request.getParameterMap()).thenReturn(RequestParams.param("brand", "mini").add("year", "2006").getParamMap());
        final List<Parameter<?>> parameters = asList(Parameters.param("brand", String.class));
        parameters.add(Parameters.param("year", String.class));
        when(route.getParameters()).thenReturn(parameters);
        final Map<String, Object> args = Parameters.extractArguments(routeContext, Collections.<String, Consumer>emptyMap());
        assertThat(args.get("brand")).isEqualTo("mini");
        assertThat(args.get("year")).isEqualTo("2006");
    }
    
    @Test (expected = RuntimeException.class)
    public void shouldThrowIfQueryParamsMissingFromRequest() {
        final List<Parameter<?>> parameters = asList(Parameters.param("brand", String.class));
        parameters.add(Parameters.param("year", String.class));
        when(route.getParameters()).thenReturn(parameters);
        Parameters.extractArguments(routeContext, Collections.<String, Consumer>emptyMap());
    }
    
    @Test
    public void extractQueryParamsWithDefaultValue() {
        when(request.getParameterMap()).thenReturn(RequestParams.param("brand", "mini").getParamMap());
        final List<Parameter<?>> parameters = asList(Parameters.param("brand", String.class));
        parameters.add(Parameters.param("year", "2012", String.class));
        when(route.getParameters()).thenReturn(parameters);
        when(route.getParameters()).thenReturn(parameters);
        final Map<String, Object> args = Parameters.extractArguments(routeContext, Collections.<String, Consumer>emptyMap());
        assertThat(args.get("brand")).isEqualTo("mini");
        assertThat(args.get("year")).isEqualTo("2012");
    }
    
    @Test
    public void extractCookieParam() {
        final Cookie cookie = mock(Cookie.class);
        when(cookie.getName()).thenReturn("testCookie");
        when(cookie.getValue()).thenReturn("cookieValue");
        when(request.getCookies()).thenReturn(new Cookie[] {cookie});
        final List<Parameter<?>> parameters = asList(Parameters.param("testCookie", String.class));
        when(route.getParameters()).thenReturn(parameters);
        final Map<String, Object> args = Parameters.extractArguments(routeContext, Collections.<String, Consumer>emptyMap());
        assertThat(args.get("testCookie")).isEqualTo("cookieValue");
    }
    
    @Test (expected = RuntimeException.class)
    public void extractCookieParamMissingFromRequest() {
        final List<Parameter<?>> parameters = asList(Parameters.param("testCookie", String.class));
        when(route.getParameters()).thenReturn(parameters);
        Parameters.extractArguments(routeContext, Collections.<String, Consumer>emptyMap());
    }
    
    private List<Parameter<?>> asList(final Parameter<?>... p) {
        return new LinkedList<Parameter<?>>(Arrays.asList(p));
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
