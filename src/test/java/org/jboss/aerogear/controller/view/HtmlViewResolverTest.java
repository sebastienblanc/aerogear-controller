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
import static org.mockito.Mockito.doReturn;

import org.jboss.aerogear.controller.SampleController;
import org.jboss.aerogear.controller.router.Route;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class HtmlViewResolverTest {
    
    @Mock
    private Route route;
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void resolveViewForPath() throws Exception {
        final ViewResolver resolver = new HtmlViewResolver();
        doReturn(SampleController.class).when(route).getTargetClass();
        doReturn(SampleController.class.getMethod("index")).when(route).getTargetMethod();
        final String resolved = resolver.resolveViewPathFor(route);
        assertThat(resolved).isEqualTo("/WEB-INF/pages/SampleController/index.html");
    }

}
