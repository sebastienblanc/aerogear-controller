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

package org.jboss.aerogear.controller.router.decorators.cors;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.LinkedHashSet;

import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Joiner;

public class CorsTestUtil {
    
    private CorsTestUtil() {
    }
    
    public static void setOriginRequestHeader(final HttpServletRequest request, final String url) {
        when(request.getHeader(Cors.RequestHeader.ORIGIN.toString())).thenReturn(url);
    }
    
    public static void setValidRequestHeaders(final CorsConfiguration config, final String... headers) {
        when(config.getValidRequestHeaders()).thenReturn(asList(headers));
    }
    
    public static void setValidRequestMethods(final CorsConfiguration config, final String... methods) {
        when(config.getValidRequestMethods()).thenReturn(asList(methods));
    }
    
    public static void setExposeHeaders(final CorsConfiguration config, final String... headers) {
        when(config.exposeHeaders()).thenReturn(true);
        when(config.getExposeHeaders()).thenReturn(Joiner.on(',').join(headers));
    }
    
    private static LinkedHashSet<String> asList(final String... values) {
        return new LinkedHashSet<String>(Arrays.asList(values));
    }

}
