package org.jboss.aerogear.controller.router;

import static org.fest.assertions.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.fest.assertions.Fail;
import org.jboss.aerogear.controller.SampleController;
import org.junit.Test;

public class DefaultRouteTest {
    
    private static final RequestMethod[] GET = new RequestMethod[]{RequestMethod.GET};
    private static final Class<?> TARGET_CLASS = SampleController.class;
    private static final Method TARGET_METHOD = indexMethod(TARGET_CLASS, "index");
    
    @Test
    public void construct() throws Exception {
        final Route route = new DefaultRoute("/car/{id}", GET, TARGET_CLASS, TARGET_METHOD);
        assertThat(route.getPath()).isEqualTo("/car/{id}");
        assertThat(route.getMethods().size()).isEqualTo(1);
        assertThat(route.getMethods()).contains(RequestMethod.GET);
        assertThat(route.getTargetClass()).isEqualTo(SampleController.class);
        assertThat(route.getTargetMethod()).isEqualTo(TARGET_METHOD);
        assertThat(route.getRoles()).isEmpty();
        assertThat(route.isSecured()).isFalse();
        assertThat(route.hasExceptionsRoutes()).isFalse();
    }
    
    @Test
    public void createRouteWithNullHttpMethods() throws Exception {
        final Route route = new DefaultRoute("/car/{id}", null, TARGET_CLASS, TARGET_METHOD);
        assertThat(route.getMethods()).isEmpty();
    }

    @Test (expected = NullPointerException.class)
    public void constructWithNullTargetClass() throws Exception {
        new DefaultRoute("/home", GET, null, TARGET_METHOD);
    }

    @Test (expected = NullPointerException.class)
    public void constructWithNullTargetMethod() throws Exception {
        new DefaultRoute("/home", GET, TARGET_CLASS, null);
    }

    @Test
    public void constructWithNullRoles() throws Exception {
        final Route route = new DefaultRoute("/car/{id}", GET, TARGET_CLASS, TARGET_METHOD, (String[]) null);
        assertThat(route.getRoles()).isEmpty();
    }

    @Test
    public void constructWithNullExceptions() throws Exception {
        final Route route = new DefaultRoute("/home", GET, TARGET_CLASS, TARGET_METHOD, (Set<Class<? extends Throwable>>) null);
        assertThat(route.hasExceptionsRoutes()).isFalse();
    }

    @Test
    public void constructWithRoles() throws Exception {
        final Route route = new DefaultRoute("/car/{id}", GET, TARGET_CLASS, TARGET_METHOD, new String[] {"admin"});
        assertThat(route.getRoles().size()).isEqualTo(1);
        assertThat(route.getRoles()).contains("admin");
    }
    
    @Test
    public void constructWithExceptions() throws Exception {
        @SuppressWarnings("unchecked")
        final Set<Class<? extends Throwable>> exceptions = exceptions(IllegalArgumentException.class);
        final Route route = new DefaultRoute("/home", GET, TARGET_CLASS, TARGET_METHOD, exceptions);
        assertThat(route.hasExceptionsRoutes()).isTrue();
    }
    
    @Test (expected = UnsupportedOperationException.class)
    public void tryToModifyRoles() throws Exception {
        final Route route = new DefaultRoute("/car/{id}", GET, TARGET_CLASS, TARGET_METHOD, new String[] {"admin"});
        route.getRoles().remove("admin");
    }

    @Test (expected = UnsupportedOperationException.class)
    public void tryToModifyHttpMethods() throws Exception {
        final Route route = new DefaultRoute("/car/{id}", GET, TARGET_CLASS, TARGET_METHOD);
        route.getMethods().remove(RequestMethod.GET);
    }

    @Test
    public void matchesUnparameterizedRoutePaths() throws NoSuchMethodException {
        final Route route = new DefaultRoute("/index", GET, TARGET_CLASS, TARGET_METHOD);
        assertThat(route.matches(RequestMethod.GET, "/index")).isTrue();
    }

    @Test
    public void matchesParameterizedRoutePaths() throws NoSuchMethodException {
        final Route route = new DefaultRoute("/car/{id}", GET, TARGET_CLASS, TARGET_METHOD);
        assertThat(route.matches(RequestMethod.GET, "/car/3")).isTrue();
    }
    
    @Test
    public void toStringTest() {
        final Route route = new DefaultRoute("/car/{id}", GET, TARGET_CLASS, TARGET_METHOD);
        final String expected = "DefaultRoute[path=/car/{id}, " +
            "targetClass=class org.jboss.aerogear.controller.SampleController, " +
            "targetMethod=public void org.jboss.aerogear.controller.SampleController.index(), " +
            "roles=[], throwables=[]]";
        assertThat(route.toString()).isEqualTo(expected);
    }

    private static Method indexMethod(final Class<?> targetClass, final String methodName) {
        Method m = null;
        try {
            m = targetClass.getDeclaredMethod(methodName);
        } catch (final Exception e) {
            Fail.fail("Could not find a method named '" + methodName + "' on target class", e);
        }
        return m;
    }
    
    private Set<Class<? extends Throwable>> exceptions(final Class<? extends Throwable>... exs) {
        return new HashSet<Class<? extends Throwable>>(Arrays.asList(exs));
    }
    
}
