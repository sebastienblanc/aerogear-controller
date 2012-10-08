package org.jboss.aerogear.controller;

import org.jboss.aerogear.controller.router.AbstractRoutingModule;
import org.jboss.aerogear.controller.router.ErrorHandler;
import org.jboss.aerogear.controller.router.Route;
import org.jboss.aerogear.controller.router.Routes;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.jboss.aerogear.controller.RequestMethod.GET;
import static org.jboss.aerogear.controller.RequestMethod.POST;

public class RoutesTest {

    @Test
    public void basicRoute() {
        Routes routes = new AbstractRoutingModule() {
            @Override
            public void configuration() {
                route()
                        .from("/home")
                        .on(GET)
                        .to(SampleController.class).index();
                route()
                        .from("/client/:name")
                        .on(GET)
                        .to(SampleController.class).client(":name");
                route()
                        .from("/lol")
                        .on(GET, POST)
                        .to(SampleController.class).lol();

            }
        }.build();

    }

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
        assertThat(routes.hasRouteFor(POST, "/cars")).isTrue();
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
        assertThat(routes.hasRouteFor(GET, "/admin")).isTrue();
    }

    @Test
    public void routesWithPathParameters() {
        Routes routes = new AbstractRoutingModule(){
            @Override
            public void configuration() {
                route()
                        .from("/car/{id}")
                        .on(GET)
                        .to(SampleController.class).find(pathParam("id"));
            }
        }.build();
        assertThat(routes.hasRouteFor(GET, "/car/1")).isTrue();
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
        assertThat(route.getTargetClass()).isEqualTo(ErrorHandler.class);
    }

    public static class Car {

        private final String name;

        public Car(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "Car{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }
}
