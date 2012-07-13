package org.jboss.aerogear.controller.router;

import org.jboss.aerogear.controller.RequestMethod;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DefaultRoute implements Route {
    private final String path;
    private final Class<?> targetClass;
    private final Method targetMethod;
    private Set<RequestMethod> methods;

    public DefaultRoute(String path, RequestMethod[] methods, Class<?> targetClass, Method targetMethod) {
        this.path = path;
        this.methods = new HashSet<RequestMethod>(Arrays.asList(methods));
        this.targetClass = targetClass;
        this.targetMethod = targetMethod;
    }

    @Override
    public Set<RequestMethod> getMethods() {
        return methods;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Class<?> getTargetClass() {
        return targetClass;
    }

    @Override
    public boolean matches(RequestMethod method, String path) {
        return this.methods.contains(method) && isPathCompatible(path);
    }

    private boolean isPathCompatible(String path) {
        if (isParameterized()) {
            final int paramStart = this.path.indexOf('{');
            return this.path.subSequence(0, paramStart).equals(path.subSequence(0, paramStart));
        }
        return this.path.equals(path);
    }

    @Override
    public Method getTargetMethod() {
        return targetMethod;
    }

    public boolean isParameterized() {
        return path.contains("{");
    }
}
