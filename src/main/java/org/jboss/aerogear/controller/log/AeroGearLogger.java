package org.jboss.aerogear.controller.log;

import org.jboss.aerogear.controller.RequestMethod;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.LogMessage;
import org.jboss.logging.Logger;
import org.jboss.logging.Message;
import org.jboss.logging.MessageLogger;

import javax.servlet.ServletException;

@MessageLogger(projectCode = "AG_CONTROLLER")
public interface AeroGearLogger extends BasicLogger {
    AeroGearLogger LOGGER = Logger.getMessageLogger(AeroGearLogger.class, AeroGearLogger.class.getPackage().getName());



    @LogMessage(level = Logger.Level.ERROR)
    @Message(id = 2, value = "oops, multivalued params not supported yet")
    void multivaluedParamsUnsupported();

    @LogMessage(level = Logger.Level.TRACE)
    @Message(id = 3, value = "method: '%s', requested URI: '%s'")
    void requestedRoute(RequestMethod method, String requestURI);


}
