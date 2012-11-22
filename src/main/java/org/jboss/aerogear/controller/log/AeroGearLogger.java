package org.jboss.aerogear.controller.log;

import java.io.IOException;

import org.jboss.aerogear.controller.router.RequestMethod;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.LogMessage;
import org.jboss.logging.Logger;
import org.jboss.logging.Message;
import org.jboss.logging.MessageLogger;

/**
 * A JBoss-Logging typed logger for AeroGear.
 */
@MessageLogger(projectCode = "AG_CONTROLLER")
public interface AeroGearLogger extends BasicLogger {
    AeroGearLogger LOGGER = Logger.getMessageLogger(AeroGearLogger.class, AeroGearLogger.class.getPackage().getName());

    @LogMessage(level = Logger.Level.ERROR)
    @Message(id = 2, value = "oops, multivalued params not supported yet")
    void multivaluedParamsUnsupported();

    @LogMessage(level = Logger.Level.TRACE)
    @Message(id = 3, value = "method: '%s', requested URI: '%s'")
    void requestedRoute(RequestMethod method, String requestURI);
    
    @LogMessage(level = Logger.Level.TRACE)
    @Message(id = 5, value = "Exception Message: '%s'")
    void routeCatchAllException(Throwable exception);
    
    @LogMessage(level = Logger.Level.TRACE)
    @Message(id = 6, value = "Exception when trying to close input stream: '%s'")
    void closeInputStream(IOException exception);

}
