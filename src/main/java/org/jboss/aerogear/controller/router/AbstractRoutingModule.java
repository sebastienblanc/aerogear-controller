package org.jboss.aerogear.controller.router;

import java.util.LinkedList;
import java.util.List;

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
    private List<RouteBuilder> routes = new LinkedList<RouteBuilder>();
    
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

    /**
     * Param is used when a target method takes a argument.
     * </p>
     * For example, lets say you have a target method named save, which takes a single argument of type Car.class,
     * the following would enable you to configure the save method:
     * <pre>
     *     .to(SampleController.class).save(param(Car.class));
     * </pre>
     * 
     * @param clazz the type of the parameter that the target method accepts.
     * @return T reference of type T but will always be null.
     */
    public static <T> T param(Class<T> clazz) {
        return null;
    }

    /**
     * Param is used when a target method takes a argument.
     * </p>
     * Example usage:<pre>{@code
     *     .to(SampleController.class).save(AbstractRoutingModule.<Car>param());
     * }</pre>
     * 
     * @return T reference of type T but will always be null.
     */
    public static <T> T param() {
        return null;
    }

    /**
     * Used to specify that a parameter of a method is expected to be in the request path.
     * </p>
     * For example:<pre>{@code 
     *     .from("/car/{id}")
     *     .on(RequestMethod.GET)
     *     .to(SampleController.class).find(pathParam("id"));
     * }</pre>
     * 
     * @param id the id/name of the parameter
     * @return {@code String} the same String that was passed in.
     */
    public static String pathParam(String id) {
        return id;
    }
    
}
