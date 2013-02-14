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

import org.jboss.aerogear.controller.router.rest.pagination.Links;
import org.junit.Test;

public class LinksTest {

    @Test
    public void links() {
        final PaginationInfo paginationInfo = PaginationInfo.offset(0).limit(10).build();
        final RequestPathParser requestPathParser = new RequestPathParser(paginationInfo, "cars?offset=0&limit=10");
        final Links links = new Links(requestPathParser, new PaginationProperties(0, 10, 100));
        assertThat(links.getFirst()).isEqualTo("cars?offset=0&limit=10");
        assertThat(links.getPrevious()).isEqualTo("cars?offset=0&limit=10");
        assertThat(links.getNext()).isEqualTo("cars?offset=10&limit=10");
        assertThat(links.getLast().get()).isEqualTo("cars?offset=90&limit=10");
    }
    
    @Test
    public void linksWithCustomParamName() {
        final PaginationInfo paginationInfo = PaginationInfo.offset(0).limit(10).build();
        final RequestPathParser requestPathParser = new RequestPathParser(paginationInfo, "cars?myoffset=0&mylimit=10");
        final Links links = new Links(requestPathParser, new PaginationProperties(0, 10, 100));
        assertThat(links.getFirst()).isEqualTo("cars?myoffset=0&mylimit=10");
        assertThat(links.getPrevious()).isEqualTo("cars?myoffset=0&mylimit=10");
        assertThat(links.getNext()).isEqualTo("cars?myoffset=10&mylimit=10");
        assertThat(links.getLast().get()).isEqualTo("cars?myoffset=90&mylimit=10");
    }
    
    @Test
    public void linksWithCustomParamNameAndExtraQueryParams() {
        final PaginationInfo paginationInfo = PaginationInfo.offset(0).limit(10).build();
        final RequestPathParser requestPathParser = new RequestPathParser(paginationInfo, "cars?color=red&myoffset=0&brand=Audi&mylimit=10&year=2013");
        final Links links = new Links(requestPathParser, new PaginationProperties(0, 10, 100));
        assertThat(links.getFirst()).isEqualTo("cars?color=red&myoffset=0&brand=Audi&mylimit=10&year=2013");
        assertThat(links.getPrevious()).isEqualTo("cars?color=red&myoffset=0&brand=Audi&mylimit=10&year=2013");
        assertThat(links.getNext()).isEqualTo("cars?color=red&myoffset=10&brand=Audi&mylimit=10&year=2013");
        assertThat(links.getLast().get()).isEqualTo("cars?color=red&myoffset=90&brand=Audi&mylimit=10&year=2013");
    }

}
