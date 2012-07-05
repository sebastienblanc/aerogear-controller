package org.jboss.aerogear.controller.router;

import org.jboss.aerogear.controller.RequestMethod;

import java.lang.reflect.Method;
import java.util.Set;

public interface Route {

    public Set<RequestMethod> getMethods();

    public String getPath();

    Method getTargetMethod();

    Class<?> getTargetClass();
}
