package org.jboss.aerogear.controller.router;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Router is the component that knows how to route/dispatch requests to target endpoints.
 */
public interface Router {
    
    /**
     * Determines if this {@link Router} has a {@link Route} for the passed-in {@link HttpServletRequest}.
     * 
     * @param httpServletRequest the {@link HttpServletRequest}.
     * @return {@code true} if this Router has a {@link Route} for the {@link HttpServletRequest}, otherwise {@code false}.
     */
    boolean hasRouteFor(HttpServletRequest httpServletRequest);

    /**
     * Dispatches to an appropriate {@link Route}.
     * 
     * @param request the {@link HttpServletRequest}.
     * @param response the {@link HttpServletResponse}
     * @param chain the {@link FilterChain}.
     * @throws ServletException if en error occurs while dispatching.
     */
    void dispatch(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException;
}
