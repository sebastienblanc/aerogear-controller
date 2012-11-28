package org.jboss.aerogear.controller.router;


/**
 * RouteBuilder builds a {@link Route} using a fluent API.
 * </p>
 * Sample Usage:<pre>{@code 
 *     RouteBuilder routeBuilder = ...;
 *     routeBuilder.from("/home")
 *                 .on(RequestMethod.GET)
 *                 .to(SampleController.class).index();
 *     Route route = routeBuilder.build();
 * }</pre>
 */
public interface RouteBuilder {

    /**
     * Specifies that this route should be able to handle any of the types of exceptions passed-in.
     * The method is used to define an error route.
     * 
     * @param exception a single class of type, or subtype, {@link Throwable}.
     * @param exceptions zero or more classes of type, or subtypes, of {@link Throwable}.
     * @return {@link TargetEndpoint} to enable further configuration of the route, such as
     * * specifying the target class and method that will be called when the exception(s) 
     * are thrown.
     */
    TargetEndpoint on(Class<? extends Throwable> exception, Class<?>... exceptions);
    
    /**
     * Specifies the request path that the {@link Route} will handle request from.
     * 
     * @param path the request path that the {@link Route} will handle request from.
     * @return {@link OnMethods} which enables further specialization of the types of requests that
     * can be handled by the {@link Route}.
     */
    OnMethods from(String path);

    /**
     * A fluent API for further specializing the {@link Route}'s destination/endpoint.
     */
    public static interface OnMethods {
        
        /**
         * Specifies which {@link RequestMethod}s should be supported by the {@link Route}.
         * 
         * @param methods the {@link RequestMethod}s that should be supported.
         * @return {@link TargetEndpoint} which is a builder for the destination for this {@link Route}.
         */
        TargetEndpoint on(RequestMethod... methods);
        
        /**
         * Specifies the roles that are allowed to invoke the target endpoint
         * 
         * @param roles the roles.
         * @return {@link OnMethods} to support method chaining.
         */
        OnMethods roles(String... roles);
    }

    /**
     * Describes the target destination for the {@link Route}.
     */
    public static interface TargetEndpoint {
        
        /**
         * Specifies the media types that this endpoint produces. 
         * </p>
         * The distinguishes an MVC call from a REST request and will cause a response
         * to be returned to the caller, as opposed to the request being forwarded to a view.
         * 
         * @param mediaTypes the media types that this endpoint method can produce.
         * @return {@link TargetEndpoint} to support method chaining.
         */
        TargetEndpoint produces(String... mediaTypes);
        
        /**
         * Specifies the target Class for the {@link Route}.
         * 
         * @param clazz The class that will be the used as the target endpoint by the {@link Route}.
         * @return T the type of the class.
         */
        <T> T to(Class<T> clazz);
    }

    /**
     * Builds a {@link Route} using the information gathered from this builder.
     * 
     * @return {@link Route} a Route instance configured by the this builder.
     */
    Route build();
}
