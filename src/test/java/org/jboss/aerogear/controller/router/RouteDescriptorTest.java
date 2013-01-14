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

package org.jboss.aerogear.controller.router;

import static org.fest.assertions.Assertions.assertThat;

import org.jboss.aerogear.controller.SampleController;
import org.junit.Test;

public class RouteDescriptorTest {

    @Test
    public void filterFinalize() throws InterruptedException {
        final RouteDescriptor routeDescriptor = new RouteDescriptor();
        routeDescriptor.to(SampleController.class).index();
        assertThat(routeDescriptor.getTargetMethod().getName()).isEqualTo("index");
        System.gc(); // not guaranteed but issuing the call just the same.
        Thread.sleep(1000);
        assertThat(routeDescriptor.getTargetMethod().getName()).isEqualTo("index");
    }

}
