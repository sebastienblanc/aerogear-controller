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

import org.jboss.aerogear.controller.router.RequestMethod;
import org.junit.Test;

public class CorsConfigTest {
    
    @Test
    public void defaultConfig() {
        final CorsConfiguration config = CorsConfig.defaultConfig();
        assertThat(config.isCorsSupportEnabled()).isTrue();
        assertThat(config.allowCookies()).isFalse();
        assertThat(config.anyOrigin()).isFalse();
        assertThat(config.exposeHeaders()).isFalse();
        assertThat(config.getExposeHeaders()).isEmpty();
        assertThat(config.hasMaxAge()).isTrue();
        assertThat(config.getMaxAge()).isEqualTo(0);
        assertThat(config.getValidRequestHeaders()).contains("origin");
        assertThat(config.getValidRequestMethods()).contains("GET", "PUT", "POST", "DELETE", "OPTIONS", "HEAD", "PATCH");
    }
    
    @Test
    public void disableCorsSupport() throws Exception  {
        final CorsConfiguration config = CorsConfig.disableCorsSupport();
        assertThat(config.isCorsSupportEnabled()).isFalse();
    }
    
    @Test
    public void exposeHeaders() throws Exception {
        final CorsConfiguration config = CorsConfig.enableCorsSupport()
                .echoOrigin()
                .disableCookies()
                .exposeHeaders("Header1", "Header2").build();
        assertThat(config.getExposeHeaders()).contains("Header1", "Header2");
    }
    
    @Test
    public void echoOrigin() throws Exception {
        final CorsConfiguration config = CorsConfig.enableCorsSupport().echoOrigin().build();
        assertThat(config.anyOrigin()).isFalse();
    }
    
    @Test
    public void anyOrigin() throws Exception {
        final CorsConfiguration config = CorsConfig.enableCorsSupport().anyOrigin().build();
        assertThat(config.anyOrigin()).isTrue();
    }
    
    @Test
    public void allowCookies() throws Exception {
        final CorsConfiguration config = CorsConfig.enableCorsSupport()
                .anyOrigin()
                .enableCookies().build();
        assertThat(config.allowCookies()).isTrue();
    }
    
    @Test
    public void disallowCookies() throws Exception {
        final CorsConfiguration config = CorsConfig.enableCorsSupport().anyOrigin().build();
        assertThat(config.allowCookies()).isFalse();
    }
    
    @Test
    public void maxAge() throws Exception {
        final CorsConfiguration config = CorsConfig.enableCorsSupport()
                .anyOrigin()
                .enableCookies()
                .maxAge(10000).build();
        assertThat(config.getMaxAge()).isEqualTo(10000);
    }
    
    @Test
    public void validRequestMethods() throws Exception {
        final CorsConfiguration config = CorsConfig.enableCorsSupport()
                .anyOrigin()
                .enableCookies()
                .maxAge(10L)
                .validRequestMethods(RequestMethod.GET, RequestMethod.POST).build();
        assertThat(config.getValidRequestMethods().size()).isEqualTo(2);
        assertThat(config.getValidRequestMethods()).contains("GET", "POST");
    }
    
    @Test
    public void enableAllRequestMethods() throws Exception {
        final CorsConfiguration config = CorsConfig.enableCorsSupport()
                .anyOrigin()
                .enableCookies()
                .maxAge(10L)
                .enableAllRequestMethods().build();
        assertThat(config.getValidRequestMethods()).contains("GET", "PUT", "POST", "DELETE", "OPTIONS", "HEAD", "PATCH");
    }
    
    @Test
    public void validRequestHeaders() throws Exception {
        final CorsConfiguration config = CorsConfig.enableCorsSupport()
                .anyOrigin()
                .enableCookies()
                .maxAge(10L)
                .validRequestMethods(RequestMethod.GET)
                .validRequestHeaders("Header1", "Header2");
        assertThat(config.getValidRequestHeaders()).contains("origin", "Header1", "Header2");
    }
    
    @Test
    public void buildOrdering() {
        CorsConfig.enableCorsSupport()
            .echoOrigin()
            .enableCookies()
            .maxAge(10l)
            .validRequestMethods(RequestMethod.GET).validRequestHeaders("");
    }
   
    
}
