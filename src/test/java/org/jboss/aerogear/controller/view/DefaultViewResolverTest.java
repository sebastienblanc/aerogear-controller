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

package org.jboss.aerogear.controller.view;


import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

import org.jboss.aerogear.controller.router.Route;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DefaultViewResolverTest {

	@Mock
	private Route route;
	
	@Test
	public void resolveViewPathFor() throws Exception {
		Class clazz = Plane.class;
		Method method = Plane.class.getMethod("index");
		
		when(route.getTargetMethod()).thenReturn(method);
		when(route.getTargetClass()).thenReturn(clazz);
		
		String path = new DefaultViewResolver().resolveViewPathFor(route);
		assertThat(path).isEqualTo("/WEB-INF/pages/Plane/index.jsp");
	}

	private class Plane {
		public void index() {

		}
	}
}
