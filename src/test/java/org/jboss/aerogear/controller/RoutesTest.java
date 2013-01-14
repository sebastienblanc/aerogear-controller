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

package org.jboss.aerogear.controller;

import static org.fest.assertions.Assertions.assertThat;
import static org.jboss.aerogear.controller.router.RequestMethod.GET;
import static org.jboss.aerogear.controller.router.RequestMethod.POST;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jboss.aerogear.controller.router.AbstractRoutingModule;
import org.jboss.aerogear.controller.router.MediaType;
import org.jboss.aerogear.controller.router.Route;
import org.jboss.aerogear.controller.router.Routes;
import org.jboss.aerogear.controller.router.error.ErrorTarget;
import org.junit.Test;

public class RoutesTest {

    @Test
    public void routesWithParameters() {
        Routes routes = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/cars")
                        .on(POST)
                        .to(SampleController.class).save(param(Car.class));
            }
        }.build();
        assertThat(routes.hasRouteFor(POST, "/cars", MediaType.defaultAcceptHeader())).isTrue();
    }

    @Test
    public void routesWithRoles() {
        Routes routes = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/admin").roles("manager")
                        .on(GET)
                        .to(SampleController.class).admin();
            }
        }.build();
        assertThat(routes.hasRouteFor(GET, "/admin", MediaType.defaultAcceptHeader())).isTrue();
    }

    @Test
    public void routesWithPathParameters() {
        Routes routes = new AbstractRoutingModule(){
            @Override
            public void configuration() {
                route()
                        .from("/car/{id}")
                        .on(GET)
                        .to(SampleController.class).find(param("id"));
            }
        }.build();
        assertThat(routes.hasRouteFor(GET, "/car/1", MediaType.defaultAcceptHeader())).isTrue();
    }
    
    @Test
    public void restfulRoute() {
        Routes routes = new AbstractRoutingModule(){
            @Override
            public void configuration() {
                route()
                        .from("/car/{id}")
                        .on(GET)
                        .produces(MediaType.JSON)
                        .produces("application/custom")
                        .produces(MediaType.HTML)
                        .to(SampleController.class).find(param("id"));
            }
        }.build();
        final Set<String> acceptHeaders = new HashSet<String>(Arrays.asList(MediaType.JSON.toString(), "application/custom"));
        Route route = routes.routeFor(GET, "/car/1", acceptHeaders);
        assertThat(route.produces()).contains(MediaType.JSON.toString(), "application/custom", MediaType.HTML.toString());
    }
    
    @Test
    public void restfulRouteWithMultipleMediaTypes() {
        Routes routes = new AbstractRoutingModule(){
            @Override
            public void configuration() {
                route()
                        .from("/car/{id}")
                        .on(GET)
                        .produces(MediaType.JSON)
                        .to(SampleController.class).find(param("id"));
            }
        }.build();
        final Set<String> acceptHeaders = new HashSet<String>(Arrays.asList(MediaType.JSON.toString()));
        assertThat(routes.hasRouteFor(GET, "/car/1", acceptHeaders)).isTrue();
    }
    
    @Test
    public void routesWithDefaultExceptionRoute() {
        Routes routes = new AbstractRoutingModule() {
            @Override
            public void configuration() throws Exception {
                route()
                        .from("/home")
                        .on(GET)
                        .to(SampleController.class).index();
            }
        }.build();
        Route route = routes.routeFor(new IllegalStateException());
        assertThat(route).isNotNull();
        assertThat(route.canHandle(new IllegalStateException())).isTrue();
        assertThat(route.canHandle(new Throwable())).isTrue();
        assertThat(route.getTargetClass()).isEqualTo(ErrorTarget.class);
    }
    
    @Test
    public void exceptionRoutesOrder() {
        Routes routes = new AbstractRoutingModule() {
            @Override
            public void configuration() throws Exception {
                route().on(SubException.class).to(SampleController.class).subException();
                route().on(SuperException.class).to(SampleController.class).superException();
                route().on(Exception.class).to(SampleController.class).error(param(Exception.class));
                route()
                        .from("/home")
                        .on(GET)
                        .to(SampleController.class).index();
            }
        }.build();
        final Route superErrorRoute = routes.routeFor(new SuperException());
        assertThat(superErrorRoute.canHandle(new SuperException())).isTrue();
        assertThat(superErrorRoute.canHandle(new SubException())).isTrue();
        assertThat(superErrorRoute.getTargetMethod().getName()).isEqualTo("superException");
        
        final Route subErrorRoute = routes.routeFor(new SubException());
        assertThat(subErrorRoute.canHandle(new SubException())).isTrue();
        assertThat(subErrorRoute.canHandle(new SuperException())).isFalse();
        assertThat(subErrorRoute.getTargetMethod().getName()).isEqualTo("subException");
        
        final Route genErrorRoute = routes.routeFor(new Exception());
        assertThat(genErrorRoute.canHandle(new SuperException())).isTrue();
        assertThat(genErrorRoute.canHandle(new SubException())).isTrue();
        assertThat(genErrorRoute.getTargetMethod().getName()).isEqualTo("error");
    }

    public static class SuperException extends Exception {
        private static final long serialVersionUID = 1L;
    }
    public static class SubException extends SuperException {
        private static final long serialVersionUID = 1L;
    }
}
