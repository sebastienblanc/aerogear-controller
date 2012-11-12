package org.jboss.aerogear.controller.router;

import org.jboss.aerogear.controller.RequestMethod;
import org.jboss.aerogear.controller.SampleController;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DefaultRouterTest {

    @Mock
    private RouteProcessor routeProcessor;
    private DefaultRouter router;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        final RoutingModule routingModule = new AbstractRoutingModule() {

            @Override
            public void configuration() {
                route()
                        .from("/car/{id}").roles("admin")
                        .on(RequestMethod.GET)
                        .to(SampleController.class).find(pathParam("id"));
            }
        };
        router = new DefaultRouter(routingModule, routeProcessor);
    }
    
    @Test
    public void test() {
        //TODO
    }

    
}
