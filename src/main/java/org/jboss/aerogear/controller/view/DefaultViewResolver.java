package org.jboss.aerogear.controller.view;

import org.jboss.aerogear.controller.view.ViewResolver;
import org.jboss.aerogear.controller.router.Route;

/**
 * A {@link ViewResolver} that resolves Java Server Page (JSP) views. 
 * </p>
 * 
 * The pattern for resolving a view is as follows:
 * <pre>
 * /WEB-INF/pages/TargetClassName/TargetMethodName.jsp
 * </pre>
 * 
 * @see View
 */
public class DefaultViewResolver implements ViewResolver {

    private static final String DEFAULT_PREFIX = "/WEB-INF/pages";
    private static final String DEFAULT_TEMPLATE_FORMAT = ".jsp";

    @Override
    public String resolveViewPathFor(Route route) {
        String folder = route.getTargetClass().getSimpleName();
        String name = route.getTargetMethod().getName();

        return String.format("%s/%s/%s%s", DEFAULT_PREFIX, folder, name, DEFAULT_TEMPLATE_FORMAT);
    }
}
