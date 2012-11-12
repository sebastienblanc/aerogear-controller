/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc., and individual contributors
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

package org.jboss.aerogear.controller.view;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;

import org.jboss.aerogear.controller.RequestMethod;
import org.jboss.aerogear.controller.router.DefaultRoute;
import org.jboss.aerogear.controller.router.Route;
import org.jboss.aerogear.controller.router.error.ErrorRoute;
import org.junit.Test;

public class ErrorViewResolverTest {

    @Test (expected = NullPointerException.class)
    public void constructWithNullDelegate() {
        new ErrorViewResolver(null);
    }
    
    @Test
    public void resolveGlobalErrorPath() {
        final ErrorViewResolver evs = new ErrorViewResolver(new DefaultViewResolver());
        final String resolvedPath = evs.resolveViewPathFor(ErrorRoute.DEFAULT.getRoute());
        assertThat(resolvedPath).isEqualTo("/ErrorFilter");
    }
    
    @Test
    public void resolveCustomErrorPath() throws Exception {
        final ErrorViewResolver evs = new ErrorViewResolver(new DefaultViewResolver());
        final Route customErrorRoute = customErrorRoute(IllegalArgumentException.class);
        assertThat(evs.resolveViewPathFor(customErrorRoute)).isEqualTo("/WEB-INF/pages/ErrorTarget/errorPage.jsp");
    }
    
    @SuppressWarnings("unchecked")
    private Route customErrorRoute(final Class<? extends Throwable> t) throws Exception {
        return new DefaultRoute("/ErrorFilter", new RequestMethod[]{RequestMethod.GET}, 
                ErrorTarget.class, 
                ErrorTarget.class.getDeclaredMethod("errorPage"), 
                new HashSet<Class<? extends Throwable>>(Arrays.asList(t)));
    }
       
    static class ErrorTarget {
        public void errorPage() {
        } 
    }
}