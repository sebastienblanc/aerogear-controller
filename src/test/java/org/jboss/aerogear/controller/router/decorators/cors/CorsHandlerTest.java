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

package org.jboss.aerogear.controller.router.decorators.cors;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import javax.enterprise.inject.Instance;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.aerogear.controller.router.Router;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CorsHandlerTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    public HttpServletResponse response;
    @Mock
    public FilterChain filterChain;
    @Mock
    public Router delegate;
    @Mock
    private CorsConfiguration corsConfig;
    @Mock
    private Instance<CorsConfiguration> corsInstance;

    private CorsHandler corsHandler;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        when(corsConfig.isCorsSupportEnabled()).thenReturn(true);
        when(corsInstance.isUnsatisfied()).thenReturn(false);
        when(corsInstance.get()).thenReturn(corsConfig);
        corsHandler = new CorsHandler(delegate, corsInstance);
    }

    @Test
    public void disableCorsSupport() throws Exception {
        when(corsConfig.isCorsSupportEnabled()).thenReturn(false);
        CorsTestUtil.setOriginRequestHeader(request, "http://someserver.com");
        corsHandler.dispatch(request, response, filterChain);
        verifyZeroInteractions(response);
        verify(delegate).dispatch(request, response, filterChain);
    }

    @Test
    public void exposeHeaders() throws Exception {
        setupValidSimpleRequest();
        CorsTestUtil.setExposeHeaders(corsConfig, "Header1", "Header2");
        corsHandler.dispatch(request, response, filterChain);
        verify(response).setHeader(Cors.ResponseHeader.EXPOSE_HEADERS.toString(), "Header1,Header2");
    }

    @Test
    public void echoOrigin() throws Exception {
        setupValidSimpleRequest();
        when(corsConfig.anyOrigin()).thenReturn(false);
        corsHandler.dispatch(request, response, filterChain);
        verify(response).setHeader(Cors.ResponseHeader.ALLOW_ORIGIN.toString(), "http://someserver.com");
    }

    @Test
    public void anyOrigin() throws Exception {
        setupValidSimpleRequest();
        when(corsConfig.anyOrigin()).thenReturn(true);
        corsHandler.dispatch(request, response, filterChain);
        verify(response).setHeader(Cors.ResponseHeader.ALLOW_ORIGIN.toString(), "*");
    }

    @Test
    public void allowCookies() throws Exception {
        setupValidSimpleRequest();
        when(corsConfig.allowCookies()).thenReturn(true);
        corsHandler.dispatch(request, response, filterChain);
        verify(response).setHeader(Cors.ResponseHeader.ALLOW_CREDENTIALS.toString(), Boolean.TRUE.toString());
    }

    @Test
    public void disallowCookies() throws Exception {
        setupValidSimpleRequest();
        when(corsConfig.allowCookies()).thenReturn(false);
        corsHandler.dispatch(request, response, filterChain);
        verify(response, never()).setHeader(eq(Cors.ResponseHeader.ALLOW_CREDENTIALS.toString()), anyString());
    }

    @Test
    public void simpleMethod() throws Exception {
        setupValidSimpleRequest();
        corsHandler.dispatch(request, response, filterChain);
        verify(response).setHeader(Cors.ResponseHeader.ALLOW_ORIGIN.toString(), "http://someserver.com");
        verify(response, never()).setHeader(eq(Cors.ResponseHeader.EXPOSE_HEADERS.toString()), anyString());
        verify(delegate).dispatch(request, response, filterChain);
    }

    @Test
    public void preflightValidateRequestMethod() throws Exception {
        setupValidPreflightRequest();
        corsHandler.dispatch(request, response, filterChain);
        verify(response).setHeader(Cors.ResponseHeader.ALLOW_ORIGIN.toString(), "http://someserver.com");
        verify(response).setHeader(Cors.ResponseHeader.ALLOW_METHODS.toString(), "GET,PUT,POST");
        verify(delegate, never()).dispatch(request, response, filterChain);
    }

    @Test
    public void preflightInvalidRequestMethod() throws Exception {
        setupValidPreflightRequest();
        CorsTestUtil.setValidRequestMethods(corsConfig, "GET");
        corsHandler.dispatch(request, response, filterChain);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(delegate, never()).dispatch(request, response, filterChain);
        verifyNoCorsHeaderSet();
    }

    @Test
    public void preflightRequestHeaders() throws Exception {
        setupValidPreflightRequest();
        CorsTestUtil.setValidRequestHeaders(corsConfig, "custom_header");
        when(request.getHeader(Cors.RequestHeader.HEADERS.toString())).thenReturn("CuStom_HeaDer");
        corsHandler.dispatch(request, response, filterChain);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(delegate, never()).dispatch(request, response, filterChain);
        verify(response).setHeader(Cors.ResponseHeader.ALLOW_ORIGIN.toString(), "http://someserver.com");
    }

    private void verifyNoCorsHeaderSet() {
        verify(response, never()).setHeader(eq(Cors.ResponseHeader.ALLOW_ORIGIN.toString()), anyString());
        verify(response, never()).setHeader(eq(Cors.ResponseHeader.ALLOW_HEADERS.toString()), anyString());
        verify(response, never()).setHeader(eq(Cors.ResponseHeader.MAX_AGE.toString()), anyString());
        verify(response, never()).setHeader(eq(Cors.ResponseHeader.ALLOW_CREDENTIALS.toString()), anyString());
    }

    @Test
    public void preflightInvalidRequestHeaders() throws Exception {
        setupValidPreflightRequest();
        when(request.getHeader(Cors.RequestHeader.HEADERS.toString())).thenReturn("NOT_ALLOWED_HEADER");
        corsHandler.dispatch(request, response, filterChain);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(delegate, never()).dispatch(request, response, filterChain);
        verifyNoCorsHeaderSet();
    }

    @Test
    public void preflightMaxAge() throws Exception {
        setupValidPreflightRequest();
        when(corsConfig.hasMaxAge()).thenReturn(true);
        when(corsConfig.getMaxAge()).thenReturn(300L);
        corsHandler.dispatch(request, response, filterChain);
        verify(response).setHeader(Cors.ResponseHeader.MAX_AGE.toString(), "300");
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(delegate, never()).dispatch(request, response, filterChain);
    }

    @Test
    public void preflight() throws Exception {
        setupValidPreflightRequest();
        corsHandler.dispatch(request, response, filterChain);
        verify(delegate, never()).dispatch(request, response, filterChain);
    }

    private void setupValidSimpleRequest() {
        CorsTestUtil.setOriginRequestHeader(request, "http://someserver.com");
        when(request.getMethod()).thenReturn("GET");
    }

    private void setupValidPreflightRequest() {
        CorsTestUtil.setValidRequestMethods(corsConfig, "GET", "PUT", "POST");
        CorsTestUtil.setOriginRequestHeader(request, "http://someserver.com");
        when(request.getHeader(Cors.RequestHeader.METHOD.toString())).thenReturn("PUT");
        when(request.getMethod()).thenReturn(Cors.RequestHeader.OPTIONS.toString());
    }

}
