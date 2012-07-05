package org.jboss.aerogear.controller.log;

import org.jboss.aerogear.controller.RequestMethod;
import org.jboss.logging.LogMessage;
import org.jboss.logging.Logger;
import org.jboss.logging.Message;
import org.jboss.logging.MessageBundle;
import org.jboss.logging.Messages;

import javax.servlet.ServletException;

@MessageBundle(projectCode = "AG_CONTROLLER")
public interface LoggerMessages {
    LoggerMessages MESSAGES = Messages.getBundle(LoggerMessages.class);

    @LogMessage(level = Logger.Level.FATAL)
    @Message(id = 1, value = "must be run inside a Servlet container")
    ServletException mustRunInsideAContainer();

    @LogMessage(level = Logger.Level.ERROR)
    @Message(id = 4, value = "method: '%s', requested URI: '%s'")
    RuntimeException routeNotFound(RequestMethod method, String requestURI);
}
