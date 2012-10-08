package org.jboss.aerogear.controller.router;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.jboss.aerogear.controller.RequestMethod;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

public class RouteDescriptor implements RouteBuilder.OnMethods, RouteBuilder.TargetEndpoint {
    private String path;
    private Method targetMethod;
    private Object[] args;
    private RequestMethod[] methods;
    private Class<?> targetClass;
    private String[] roles;
    private Set<Class<? extends Throwable>> throwables;

    public RouteDescriptor() {
    }
    
    public void setPath(String path) {
        this.path = path;
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
            Object o = Enhancer.create(clazz, new MyMethodInterceptor(this));
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

    public void setThrowables(Set<Class<? extends Throwable>> throwable) {
        this.throwables = throwable;
    }
    
    public Set<Class<? extends Throwable>> getThrowables() {
        return throwables;
    }
}
