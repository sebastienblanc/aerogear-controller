package org.jboss.aerogear.controller.router;

import org.jboss.aerogear.controller.RequestMethod;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DefaultRoute implements Route {
    private final String path;
    private final Class<?> targetClass;
    private final Method targetMethod;
    private Set<RequestMethod> methods;
    private String[] roles;

    public DefaultRoute(String path, RequestMethod[] methods, Class<?> targetClass, Method targetMethod) {
        this.path = path;
        this.methods = new HashSet<RequestMethod>(Arrays.asList(methods));
        this.targetClass = targetClass;
        this.targetMethod = targetMethod;
    }

    public DefaultRoute(String path, RequestMethod[] methods, Class<?> targetClass, Method targetMethod,
                        String[] roles) {
        this.path = path;
        this.methods = new HashSet<RequestMethod>(Arrays.asList(methods));
        this.targetClass = targetClass;
        this.targetMethod = targetMethod;
        this.roles = roles;
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

    @Override
    public boolean isParameterized() {
        return path.contains("{");
    }

    @Override
    public boolean isSecured() {
        if (roles != null) {
            return true;
        }
        return false;
    }

    @Override
    public String[] getRoles() {
        return roles;
    }
}
