package org.jboss.aerogear.controller.router;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Objects.firstNonNull;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * An immutable implementation of {@link Route}.
 */
public class DefaultRoute implements Route {
    private final String path;
    private final Class<?> targetClass;
    private final Method targetMethod;
    private final Set<RequestMethod> methods;
    private final Set<String> roles;
    private final Set<Class<? extends Throwable>> throwables;

    /**
     * Constructs a Route without any roles or exceptions associated with it.
     * 
     * @param path the path for this Route. Can be {@code null}.
     * @param methods the {@link RequestMethod}s that this Route should handle. Can be {@code null}.
     * @param targetClass the target {@link Class} that is the target for this Route. Must not be {@code null}
     * @param targetMethod the target method in the {@link #targetClass}. Must not be {@code null}
     */
    public DefaultRoute(String path, RequestMethod[] methods, Class<?> targetClass, Method targetMethod) {
        this(path, methods, targetClass, targetMethod, new String[]{}, emptyThrowableSet());
    }

    /**
     * Constructs a Route with the specified roles.
     * 
     * @param path the path for this Route. Can be {@code null}.
     * @param methods the {@link RequestMethod}s that this Route should handle. Can be {@code null}.
     * @param targetClass the target {@link Class} that is the target for this Route. Must not be {@code null}
     * @param targetMethod the target method in the {@link #targetClass}. Must not be {@code null}
     * @param roles the roles to associate with this Route. Can be {@code null}.
     */
    public DefaultRoute(String path, RequestMethod[] methods, Class<?> targetClass, Method targetMethod, String[] roles) {
        this(path, methods, targetClass, targetMethod, roles, emptyThrowableSet());
    }
    
    /**
     * Constructs a Route with the specified exceptions associated with it.
     * 
     * @param methods the {@link RequestMethod}s that this Route should handle. Can be {@code null}.
     * @param targetClass the target {@link Class} that is the target for this Route. Must not be {@code null}
     * @param targetMethod the target method in the {@link #targetClass}. Must not be {@code null}
     * @param throwables the exceptions that this Route can handle. Can be {@code null}.
     */
    public DefaultRoute(String path, RequestMethod[] methods, Class<?> targetClass, Method targetMethod, 
            Set<Class<? extends Throwable>> throwables ) {
        this(path, methods, targetClass, targetMethod, new String[]{}, throwables);
    }

    /**
     * Constructs a Route with the specified roles and exceptions associated with it.
     * 
     * @param path the path for this Route. Can be {@code null}.
     * @param methods the {@link RequestMethod}s that this Route should handle. Can be {@code null}.
     * @param targetClass the target {@link Class} that is the target for this Route. Must not be {@code null}
     * @param targetMethod the target method in the {@link #targetClass}. Must not be {@code null}
     * @param roles the roles to associate with this Route. Can be {@code null}.
     * @param throwables the exceptions that this Route can handle. Can be {@code null}.
     */
    public DefaultRoute(String path, RequestMethod[] methods, Class<?> targetClass, Method targetMethod,
                        String[] roles, Set<Class<? extends Throwable>> throwables) {
        checkNotNull(targetClass, "'targetClass' must not be null");
        checkNotNull(targetMethod, "'targetMethod' must not be null");
        this.path = path;
        this.methods = asSet(methods);
        this.targetClass = targetClass;
        this.targetMethod = targetMethod;
        this.roles = asSet(roles);
        this.throwables = firstNonNull(throwables, emptyThrowableSet());
    }
    
    @Override
    public Set<RequestMethod> getMethods() {
        return Collections.unmodifiableSet(methods);
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
        return !roles.isEmpty();
    }

    @Override
    public Set<String> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    @Override
    public boolean hasExceptionsRoutes() {
        return !throwables.isEmpty();
    }
    
    @Override
    public boolean canHandle(final Throwable throwable) {
        for (Class<? extends Throwable> t : throwables) {
            if (t.isAssignableFrom(throwable.getClass())) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return new StringBuilder("DefaultRoute[")
            .append("path=").append(path)
            .append(", targetClass=").append(targetClass)
            .append(", targetMethod=").append(targetMethod)
            .append(", roles=").append(roles)
            .append(", throwables=").append(throwables)
            .append("]")
            .toString();
    }

    private Set<RequestMethod> asSet(final RequestMethod[] methods) {
        return methods == null ? Collections.<RequestMethod>emptySet() : new HashSet<RequestMethod>(Arrays.asList(methods));
    }

    private Set<String> asSet(final String[] roles) {
        return roles == null ? Collections.<String>emptySet() : new HashSet<String>(Arrays.asList(roles));
    }

    private static Set<Class<? extends Throwable>> emptyThrowableSet() {
        return Collections.<Class<? extends Throwable>>emptySet();
    }
}
