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
