package org.jboss.aerogear.controller.view;

import org.jboss.aerogear.controller.router.MediaType;


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
public class JspViewResolver extends AbstractViewResolver {

    public JspViewResolver() {
        super(".jsp");
    }

}
