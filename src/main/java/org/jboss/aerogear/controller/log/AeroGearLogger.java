/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.aerogear.controller.log;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.jboss.aerogear.controller.router.RequestMethod;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.Cause;
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
    
    @LogMessage(level = Logger.Level.ERROR)
    @Message(id = 5, value = "Exception Message: '%s'")
    void routeCatchAllException(@Cause Throwable exception, String error) ;
    
    @LogMessage(level = Logger.Level.TRACE)
    @Message(id = 6, value = "Exception when trying to close input stream: '%s'")
    void closeInputStream(IOException exception);
    
    @LogMessage(level = Logger.Level.INFO)
    @Message(id = 7, value = "CORS Bad Request Headers: Received '%s', allowed: '%s'")
    void badCorsRequestHeaders(String actualHeaders, List<String> allowedHeaders);
    
    @LogMessage(level = Logger.Level.INFO)
    @Message(id = 8, value = "CORS Bad Request Method: Received '%s', allowed: '%s'")
    void badCorsRequestMethod(String actualMethod, Set<String> allowedMethods);


}
