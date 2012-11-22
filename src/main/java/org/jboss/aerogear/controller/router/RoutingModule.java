package org.jboss.aerogear.controller.router;


/**
 * A RoutingModule is used by the AeroGear runtime to retrieve the routes defined by the current application. 
 * </p>
 * 
 * End users may implement this interface directly or use {@link AbstractRoutingModule} which has convenience methods.
 * @see AbstractRoutingModule
 */
public interface RoutingModule {
    
    /**
     * Returns a {@link Routes} instance containing all the {@link Route}s configured in
     * the application.
     * 
     * @return {@link Routes} populated with all the configured {@link Route}s.
     */
    Routes build();
}
