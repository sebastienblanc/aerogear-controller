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
package org.jboss.aerogear.controller.router.rest.pagination;

import static org.fest.assertions.Assertions.assertThat;

import org.jboss.aerogear.controller.router.rest.pagination.PagingProperties;
import org.junit.Test;

public class PagingPropertiesTest {

    @Test
    public void offset() {
        final PagingProperties params = new PagingProperties(10, 25, 100);
        assertThat(params.offset()).isEqualTo(10);
        assertThat(params.limit()).isEqualTo(25);
        assertThat(params.total().get()).isEqualTo(100);
    }
    
    @Test
    public void nextOffset() {
        final PagingProperties params = new PagingProperties(14, 10, 30);
        assertThat(params.nextOffset()).isEqualTo(24);
        assertThat(params.offset()).isEqualTo(14);
        assertThat(params.limit()).isEqualTo(10);
        assertThat(params.total().get()).isEqualTo(30);
    }
    
    @Test
    public void nextOffsetLastPage() {
        final PagingProperties params = new PagingProperties(90, 10, 100);
        assertThat(params.nextOffset()).isEqualTo(100);
        assertThat(params.offset()).isEqualTo(90);
        assertThat(params.limit()).isEqualTo(10);
        assertThat(params.total().get()).isEqualTo(100);
        assertThat(params.isLastOffset()).isTrue();
    }
    
    @Test
    public void previousOffset() {
        assertThat(new PagingProperties(90, 10, 100).previousOffset()).isEqualTo(80);
        assertThat(new PagingProperties(10, 10, 100).previousOffset()).isEqualTo(0);
        assertThat(new PagingProperties(0, 10, 100).previousOffset()).isEqualTo(0);
    }
    
    @Test
    public void isFirstOffset() {
        assertThat(new PagingProperties(0, 10, 100).isFirstOffset()).isTrue();
        assertThat(new PagingProperties(9, 10, 100).isFirstOffset()).isTrue();
        assertThat(new PagingProperties(10, 10, 100).isFirstOffset()).isFalse();
        assertThat(new PagingProperties(11, 10, 100).isFirstOffset()).isFalse();
    }
    
    @Test
    public void isLastOffset() {
        assertThat(new PagingProperties(90, 10, 100).isLastOffset()).isTrue();
        assertThat(new PagingProperties(99, 10, 100).isLastOffset()).isTrue();
        assertThat(new PagingProperties(89, 10, 100).isLastOffset()).isFalse();
    }
    
    @Test
    public void isOffsetGreaterThanTotal() {
        assertThat(new PagingProperties(200, 10, 100).isOffsetGreaterThanTotal()).isTrue();
    }
    
    @Test
    public void offsetLargerThanTotalPrevious() {
        assertThat(new PagingProperties(200, 10, 100).previousOffset()).isEqualTo(90);
        
    }
    
    @Test (expected = RuntimeException.class)
    public void shouldThrowIfOffsetIsNegative() {
       new PagingProperties(-1, 10, 100);
    }
    
    @Test (expected = RuntimeException.class)
    public void shouldThrowIfLimitIsNegative() {
       new PagingProperties(0, -10, 100);
    }
    
    @Test (expected = RuntimeException.class)
    public void shouldThrowIfLimitIsZero() {
       new PagingProperties(0, 0, 100);
    }
    
}
