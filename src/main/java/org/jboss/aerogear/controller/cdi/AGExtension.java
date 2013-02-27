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
package org.jboss.aerogear.controller.cdi;

import java.lang.reflect.Modifier;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.jboss.aerogear.controller.log.LoggerMessages;
import org.jboss.aerogear.controller.router.Responder;

public class AGExtension implements Extension {

    <T> void processAnnotatedType(final @Observes ProcessAnnotatedType<T> pat) throws SecurityException {
        if (Responder.class.isAssignableFrom(pat.getAnnotatedType().getJavaClass())) {
            final Class<T> javaClass = pat.getAnnotatedType().getJavaClass();
            if (!Modifier.isAbstract(javaClass.getModifiers()) || !javaClass.isInterface()) {
                try {
                    javaClass.getConstructor(new Class[] {});
                } catch (final NoSuchMethodException e) {
                    throw LoggerMessages.MESSAGES.responderDoesNotHaveNoArgsCtor(javaClass);
                }
            }
        }
    }

}
