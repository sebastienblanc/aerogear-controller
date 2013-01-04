package org.jboss.aerogear.controller.router;

import java.util.LinkedList;
import java.util.List;

import org.jboss.aerogear.controller.router.parameter.Parameter;
import org.jboss.aerogear.controller.router.parameter.Parameters;

/**
 * AbstractRoutingModule simplifies the process of configuring Routes by implementing
 * {@link RoutingModule} and providing helper methods.
 * </p>
 * Example Usage: 
 * <pre>
 * Routes routes = new AbstractRoutingModule() {
 *     &#64;Override
 *     public void configuration() {
 *         route()
 *                .from("/home")
 *                .on(RequestMethod.GET)
 *                .to(SampleController.class).index();
 *         //... more routes
 *     }
 * }.build();
 * </pre>
 * 
 */
public abstract class AbstractRoutingModule implements RoutingModule {
    private final List<RouteBuilder> routes = new LinkedList<RouteBuilder>();
    
    /**
     * "Hook" for the template method {@link #build()}, which subclasses should
     * implement to define the routes of the application. 
     * For an example usage see the javadoc for this class.
     * 
     * @throws Exception enables a route to specify a target method that throws an exception. 
     * This method is only about configuring routes and not invoking them, but the target methods might
     * still declare that they throw exceptions and {@link #configuration()} declares this as well, saving
     * end users from having to have try/catch clauses that clutter up their route configurations
     */
    public abstract void configuration() throws Exception;

    /**
     * Is the starting point to configuring a single route. 
     * 
     * @return {@link RouteBuilder} which provides a fluent API for configuring a {@link Route}.
     */
    public RouteBuilder route() {
        RouteBuilder route = Routes.route();
        routes.add(route);
        return route;
    }

    @Override
    public Routes build() {
        try {
            configuration();
        } catch (final Exception e) {
            throw new AeroGearException(e);
        }
        return Routes.from(routes);
    }

    public <T> T param(Class<T> type) {
        addParameter(Parameters.param(type));
        return null;
    }
    
    public String param(String id) {
        addParameter(Parameters.param(id, String.class));
        return null;
    }
    
    public String param(String id, String defaultValue) {
        addParameter(Parameters.param(id, defaultValue, String.class));
        return null;
    }
    
    private void addParameter(final Parameter<?> parameter) {
        current().addParameter(parameter);
    }
    
    private RouteDescriptor current() {
        RouteDescriptorAccessor rda = (RouteDescriptorAccessor) routes.get(routes.size()-1);
        return rda.getRouteDescriptor();
    }
    
}
