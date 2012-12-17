package org.jboss.aerogear.controller.router;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.NoOp;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

import org.jboss.aerogear.controller.router.RouteBuilder.TargetEndpoint;

/**
 * Describes/configures a single route in AeroGear controller.
 */
public class RouteDescriptor implements RouteBuilder.OnMethods, RouteBuilder.TargetEndpoint {
    private String path;
    private Method targetMethod;
    private Object[] args;
    private RequestMethod[] methods;
    private Class<?> targetClass;
    private String[] roles;
    private String[] produces;
    private Set<Class<? extends Throwable>> throwables;
    private final static FinalizeFilter FINALIZE_FILTER = new FinalizeFilter();

    public RouteDescriptor() {
    }
    
    /**
     * Set the path for this instance. 
     * </p>
     * A RouteDescriptor may have an empty path if it is an error route. 
     * 
     * @param path the from path for this route.
     */
    public RouteDescriptor setPath(String path) {
        this.path = path;
        return this;
    }

    @Override
    public RouteBuilder.TargetEndpoint on(RequestMethod... methods) {
        this.methods = methods;
        return this;
    }

    @Override
    public RouteBuilder.OnMethods roles(String... roles) {
        this.roles = roles;
        return this;
    }

    @Override
    public <T> T to(Class<T> clazz) {
        this.targetClass = clazz;
        try {
            Object o = Enhancer.create(clazz, null, FINALIZE_FILTER, new Callback[] {new MyMethodInterceptor(this), NoOp.INSTANCE});
            return (T) o;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getPath() {
        return path;
    }

    public RequestMethod[] getMethods() {
        return methods;
    }

    public Method getTargetMethod() {
        return targetMethod;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public String[] getRoles() {
        return roles;
    }

    private static class MyMethodInterceptor implements MethodInterceptor {
        private final RouteDescriptor routeDescriptor;

        public MyMethodInterceptor(RouteDescriptor routeDescriptor) {
            this.routeDescriptor = routeDescriptor;
        }

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            this.routeDescriptor.targetMethod = method;
            this.routeDescriptor.args = args;
            return null;
        }
    }

    @Override
    public String toString() {
        return "RouteDescriptor{" +
                "path='" + path + '\'' +
                ", targetMethod=" + targetMethod +
                ", args=" + (args == null ? null : Arrays.asList(args)) +
                '}';
    }

    public RouteDescriptor setThrowables(Set<Class<? extends Throwable>> throwables) {
        this.throwables = throwables;
        return this;
    }
    
    public Set<Class<? extends Throwable>> getThrowables() {
        return throwables;
    }
    @Override
    public TargetEndpoint produces(String... produces) {
        this.produces = produces;
        return this;
    }
    
    public String[] getProduces() {
        return produces;
    }
    
    private static class FinalizeFilter implements CallbackFilter {
        
        /* Indexes into the callback array */
        private static final int OUR_INTERCEPTOR = 0;
        private static final int NO_OP = 1;

        @Override
        public int accept(Method method) {
            return method.getName().equals("finalize") ? NO_OP : OUR_INTERCEPTOR;
        }
        
    }
}
