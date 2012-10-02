package org.jboss.aerogear.controller.view;

import org.jboss.aerogear.controller.util.TypeNameExtractor;

/**
 * A view in AeroGear consists of a path to a resource and optionally a model.
 * </p>
 * The path could be to a jsp page, or any other type of template language file.
 * </p> 
 * 
 * @see ViewResolver
 */
public class View {
    private final String viewPath;
    private final Object model;
    private final TypeNameExtractor nameExtractor = new TypeNameExtractor();

    public View(String viewPath) {
        this(viewPath, null);
    }

    public View(String viewPath, Object model) {
        this.viewPath = viewPath;
        this.model = model;
    }

    public String getViewPath() {
        return viewPath;
    }

    public String getModelName() {
        if (hasModelData()) {
            return nameExtractor.nameFor(this.model.getClass());
        }
        return null;
    }

    public Object getModel() {
        return model;
    }

    public boolean hasModelData() {
        return this.model != null;
    }
}
