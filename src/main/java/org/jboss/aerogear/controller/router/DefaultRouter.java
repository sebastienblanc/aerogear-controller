package org.jboss.aerogear.controller.router;

import br.com.caelum.iogi.Iogi;
import br.com.caelum.iogi.parameters.Parameter;
import br.com.caelum.iogi.reflection.Target;
import br.com.caelum.iogi.util.DefaultLocaleProvider;
import br.com.caelum.iogi.util.NullDependencyProvider;
import org.jboss.aerogear.controller.RequestMethod;
import org.jboss.aerogear.controller.log.AeroGearLogger;
import org.jboss.aerogear.controller.util.StringUtils;
import org.jboss.aerogear.controller.view.View;
import org.jboss.aerogear.controller.view.ViewResolver;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.Map;

public class DefaultRouter implements Router {

    private Routes routes;
    private final BeanManager beanManager;
    private ViewResolver viewResolver;
    private Iogi iogi = new Iogi(new NullDependencyProvider(), new DefaultLocaleProvider());
    private ControllerFactory controllerFactory;

    @Inject
    public DefaultRouter(RoutingModule routes, BeanManager beanManager, ViewResolver viewResolver, ControllerFactory controllerFactory) {
        this.routes = routes.build();
        this.beanManager = beanManager;
        this.viewResolver = viewResolver;
        this.controllerFactory = controllerFactory;
    }

    @Override
    public boolean hasRouteFor(HttpServletRequest httpServletRequest) {
        return routes.hasRouteFor(extractMethod(httpServletRequest), extractPath(httpServletRequest));
    }

    private String extractPath(HttpServletRequest httpServletRequest) {
        ServletContext servletContext = httpServletRequest.getServletContext();
        String contextPath = servletContext.getContextPath();

        return httpServletRequest.getRequestURI().substring(contextPath.length());
    }

    private RequestMethod extractMethod(HttpServletRequest httpServletRequest) {
        return RequestMethod.valueOf(httpServletRequest.getMethod());
    }

    @Override
    public void dispatch(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException {
        try {
            final String requestPath = extractPath(request);
            Route route = routes.routeFor(extractMethod(request), requestPath);
            Object[] params;
            if (route.isParameterized()) {
                params = extractPathParameters(requestPath, route);
            } else {
                params = extractParameters(request, route);
            }
            Object result = route.getTargetMethod().invoke(getController(route), params);
            String viewPath = viewResolver.resolveViewPathFor(route);
            View view = new View(viewPath, result);
            if (view.hasModelData()) {
                request.setAttribute(view.getModelName(), view.getModel());
            }
            request.getRequestDispatcher(view.getViewPath()).forward(request, response);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private Object[] extractPathParameters(String requestPath, Route route) {
        // TODO: extract this from resteasy
        final int paramOffset = route.getPath().indexOf('{');
        final CharSequence param = requestPath.subSequence(paramOffset, requestPath.length());
        return new Object[]{param.toString()};
    }

    private Object[] extractParameters(HttpServletRequest request, Route route) {
        LinkedList<Parameter> parameters = new LinkedList<Parameter>();
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String[] value = entry.getValue();
            if (value.length == 1) {
                parameters.add(new Parameter(entry.getKey(), value[0]));
            } else {
                AeroGearLogger.LOGGER.multivaluedParamsUnsupported();
                continue;
            }
        }
        Class<?>[] parameterTypes = route.getTargetMethod().getParameterTypes();
        if (parameterTypes.length == 1) {
            Class<?> parameterType = parameterTypes[0];
            Target<?> target = Target.create(parameterType, StringUtils.downCaseFirst(parameterType.getSimpleName()));
            Object instantiate = iogi.instantiate(target, parameters.toArray(new Parameter[]{}));
            return new Object[]{instantiate};
        }

        return new Object[0];  //To change body of created methods use File | Settings | File Templates.
    }

    private Object getController(Route route) {
        return controllerFactory.createController(route.getTargetClass(), beanManager);
    }

}
