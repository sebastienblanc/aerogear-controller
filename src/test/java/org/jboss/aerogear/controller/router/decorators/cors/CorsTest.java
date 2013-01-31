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

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CorsTest {
    
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private CorsConfiguration corsConfig;
    @InjectMocks
    private Cors cors;
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(corsConfig.isCorsSupportEnabled()).thenReturn(true);
        CorsTestUtil.setOriginRequestHeader(request, "http://someserver.com");
    }

    @Test
    public void isCorsRequest() {
        assertThat(cors.hasOriginHeader()).isTrue();
    }
    
    @Test
    public void isNotCorsRequest() {
        CorsTestUtil.setOriginRequestHeader(request, null);
        assertThat(cors.hasOriginHeader()).isFalse();
    }
    
    @Test
    public void canHandleRequest() {
        when(corsConfig.isCorsSupportEnabled()).thenReturn(true);
        assertThat(cors.canHandleRequest()).isTrue();
    }
    
    @Test
    public void isOptionsMethod() {
        when(request.getMethod()).thenReturn(Cors.RequestHeader.OPTIONS.toString());
        assertThat(cors.isOptionsMethod()).isTrue();
    }
    
    @Test
    public void isPreflightRequest() {
        when(request.getMethod()).thenReturn(Cors.RequestHeader.OPTIONS.toString());
        when(request.getHeader(Cors.RequestHeader.METHOD.toString())).thenReturn("GET");
        assertThat(cors.isPreflightRequest()).isTrue();
    }
    
    @Test
    public void validateRequestMethodNull() {
        when(request.getHeader(Cors.RequestHeader.METHOD.toString())).thenReturn(null);
        assertThat(cors.isRequestMethodValid(new HashSet<String>(Arrays.asList("GET", "POST")))).isFalse();
    }
    
    @Test
    public void validateRequestMethod() {
        when(request.getHeader(Cors.RequestHeader.METHOD.toString())).thenReturn("GET");
        assertThat(cors.isRequestMethodValid(new HashSet<String>(Arrays.asList("GET", "POST")))).isTrue();
    }
    
    @Test
    public void hasRequestHeaders() {
        when(request.getHeader(Cors.RequestHeader.HEADERS.toString())).thenReturn(null);
        assertThat(cors.hasRequestHeaders()).isFalse();
        when(request.getHeader(Cors.RequestHeader.HEADERS.toString())).thenReturn("X-Custom-Header");
        assertThat(cors.hasRequestHeaders()).isTrue();
    }
    
    @Test
    public void echoOrigin() {
        cors.setEchoOrigin(response);
        verify(response).setHeader(Cors.ResponseHeader.ALLOW_ORIGIN.toString(), "http://someserver.com");
    }
    
    @Test
    public void anyOrigin() {
        cors.setAnyOrigin(response);
        verify(response).setHeader(Cors.ResponseHeader.ALLOW_ORIGIN.toString(), "*");
    }
    
    @Test
    public void setOriginAny() {
        when(corsConfig.anyOrigin()).thenReturn(true);
        cors.setOrigin(response);
        verify(response).setHeader(Cors.ResponseHeader.ALLOW_ORIGIN.toString(), "*");
    }
    
    @Test
    public void setOriginEcho() {
        CorsTestUtil.setOriginRequestHeader(request, "http://myserver.com");
        when(corsConfig.anyOrigin()).thenReturn(false);
        cors.setOrigin(response);
        verify(response).setHeader(Cors.ResponseHeader.ALLOW_ORIGIN.toString(), "http://myserver.com");
    }
    
    @Test
    public void allowCredentials() {
        when(corsConfig.allowCookies()).thenReturn(true);
        cors.setAllowCredentials(response);
        verify(response).setHeader(Cors.ResponseHeader.ALLOW_CREDENTIALS.toString(), Boolean.TRUE.toString());
    }
    
    @Test
    public void exposeHeaders() {
        CorsTestUtil.setExposeHeaders(corsConfig, "Header1", "Header2");
        cors.setExposeHeaders(response);
        verify(response).setHeader(Cors.ResponseHeader.EXPOSE_HEADERS.toString(), "Header1,Header2");
    }
    
    @Test
    public void allowMethodsSet() {
        CorsTestUtil.setValidRequestMethods(corsConfig, "PUT", "GET", "POST");
        cors.setAllowMethods(response);
        verify(response).setHeader(Cors.ResponseHeader.ALLOW_METHODS.toString(), "PUT,GET,POST");
    }
    
    @Test
    public void maxAge() {
        when(corsConfig.getMaxAge()).thenReturn(600L);
        when(corsConfig.hasMaxAge()).thenReturn(true);
        cors.setMaxAge(response);
        verify(response).setHeader(Cors.ResponseHeader.MAX_AGE.toString(), "600");
    }
    
    @Test
    public void areRequestHeadersValid() {
        when(request.getHeader(Cors.RequestHeader.HEADERS.toString())).thenReturn("origin, X-Header2");
        assertThat(cors.areRequestHeadersValid(Arrays.asList("HEADER1", "x-header2", "origin"))).isTrue();
    }
    
    @Test
    public void validateNullRequestHeaders() {
        when(request.getHeader(Cors.RequestHeader.HEADERS.toString())).thenReturn(null);
        assertThat(cors.areRequestHeadersValid()).isTrue();
    }
    
    @Test
    public void allowHeaders() {
        CorsTestUtil.setValidRequestHeaders(corsConfig, "HEADER1", "HEADER2");
        cors.setAllowHeaders(response);
        verify(response).setHeader(Cors.ResponseHeader.ALLOW_HEADERS.toString(), "HEADER1,HEADER2");
    }
    
    @Test
    public void isSimpleMethod() {
        when(request.getMethod()).thenReturn("GET");
        assertThat(cors.isPreflightRequest()).isFalse();
    }
    
}
