package org.jboss.aerogear.controller.spi;

import org.jboss.aerogear.controller.router.Route;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.doThrow;

public class SecurityProviderTest {

    @Mock
    private SecurityProvider securityProvider;

    @Mock
    private SecurityServletException securityServletException;

    @Mock
    private Route route;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRouteAllowed() throws Exception {
        securityProvider.isRouteAllowed(route);
    }

    @Test(expected = SecurityServletException.class)
    public void testRouteForbbiden() throws Exception {
        doThrow(securityServletException).when(securityProvider).isRouteAllowed(route);
        securityProvider.isRouteAllowed(route);
    }
}
