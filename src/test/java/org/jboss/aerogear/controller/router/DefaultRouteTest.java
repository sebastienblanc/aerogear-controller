package org.jboss.aerogear.controller.router;

import org.jboss.aerogear.controller.RequestMethod;
import org.jboss.aerogear.controller.SampleController;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.fest.assertions.Assertions.assertThat;
import static org.jboss.aerogear.controller.RequestMethod.GET;

public class DefaultRouteTest {

    @Test
    public void matchesUnparameterizedRoutePaths() throws NoSuchMethodException {
        final Method targetMethod = SampleController.class.getDeclaredMethod("index");
        final Route route = new DefaultRoute("/index", new RequestMethod[]{GET}, SampleController.class, targetMethod);
        assertThat(route.matches(GET, "/index")).isTrue();
    }

    @Test
    public void matchesParameterizedRoutePaths() throws NoSuchMethodException {
        final Method targetMethod = SampleController.class.getDeclaredMethod("find", String.class);
        final Route route = new DefaultRoute("/car/{id}", new RequestMethod[]{GET}, SampleController.class, targetMethod);
        assertThat(route.matches(GET, "/car/3")).isTrue();
    }

}
