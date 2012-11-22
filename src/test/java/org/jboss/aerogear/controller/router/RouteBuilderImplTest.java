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

import static org.fest.assertions.Assertions.assertThat;

import org.jboss.aerogear.controller.SampleController;
import org.junit.Test;

public class RouteBuilderImplTest {
    
    @Test
    public void testNoExceptions() {
        final RouteBuilderImpl rb = defaultRouteBuilder();
        assertThat(rb.build().hasExceptionsRoutes()).isFalse();
    }
    
    @Test (expected = IllegalArgumentException.class)
    public void testWrongExceptionType() {
        final RouteBuilderImpl rb = defaultRouteBuilder();
        rb.on(IllegalStateException.class, String.class);
    }

    @Test
    public void testOnOneException() {
        final RouteBuilderImpl rb = defaultRouteBuilder();
        rb.on(IllegalStateException.class);
        final Route route = rb.build();
        assertThat(route.hasExceptionsRoutes()).isTrue();
        assertThat(route.canHandle(new IllegalArgumentException())).isFalse();
        assertCanHandle(route, new IllegalStateException());
    }
    
    @Test
    public void testOnExceptionSubtype() {
        final RouteBuilderImpl rb = defaultRouteBuilder();
        rb.on(Exception.class);
        final Route route = rb.build();
        assertThat(route.canHandle(new IllegalArgumentException())).isTrue();
        assertThat(route.canHandle(new Exception())).isTrue();
        assertThat(route.canHandle(new Throwable())).isFalse();
    }
    
    @Test
    public void testOnMultipleExceptions() {
        final RouteBuilderImpl rb = defaultRouteBuilder();
        rb.on(IllegalStateException.class, IllegalArgumentException.class, UnsupportedOperationException.class);
        assertCanHandle(rb.build(), new IllegalStateException(), new IllegalArgumentException(), new UnsupportedOperationException());
    }
    
    private void assertCanHandle(final Route route, final Throwable... t) {
        for (Throwable throwable : t) {
            assertThat(route.canHandle(throwable)).isTrue();
        }
    }
    
    private RouteBuilderImpl defaultRouteBuilder() {
        final RouteBuilderImpl rb = new RouteBuilderImpl();
        rb.from("/path").on(RequestMethod.GET).to(SampleController.class).index();
        return rb;
    }

}
