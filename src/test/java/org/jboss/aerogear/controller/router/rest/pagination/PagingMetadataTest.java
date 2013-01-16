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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

public class PagingMetadataTest {
    
    private final Pattern offsetLimitValue = Pattern.compile(".*\\?offset=([0-9]*)&limit=([0-9]*).*");
    private RequestPathParser requestPathParser;
    
    @Before
    public void setup() {
        final PaginationInfo paginationInfo = new PaginationInfo("offset", "0", "limit", "10");
        requestPathParser = new RequestPathParser(paginationInfo, "cars?offset=0&limit=10");
    }

    @Test
    public void links() {
        final PagingMetadata metadata = new PagingMetadata(new PagingProperties(0, 10), requestPathParser);
        final Links links = metadata.getLinks();
        assertThat(links.getFirst()).isEqualTo("cars?offset=0&limit=10");
        assertThat(links.getPrevious()).isEqualTo("cars?offset=0&limit=10");
    }
    
    @Test
    public void navigateForward() {
        PagingMetadata metadata = new PagingMetadata(new PagingProperties(0, 5), requestPathParser);
        Links links = metadata.getLinks();
        assertThat(links.getNext()).isEqualTo("cars?offset=5&limit=5");
        
        metadata = new PagingMetadata(new PagingProperties(parseOffset(links.getNext()), 5), requestPathParser);
        links = metadata.getLinks();
        assertThat(links.getNext()).isEqualTo("cars?offset=10&limit=5");
        assertThat(links.getPrevious()).isEqualTo("cars?offset=0&limit=5");
        
        metadata = new PagingMetadata(new PagingProperties(parseOffset(links.getNext()), 5), requestPathParser);
        links = metadata.getLinks();
        assertThat(links.getNext()).isEqualTo("cars?offset=15&limit=5");
    }
    
    @Test
    public void navigateBackwards() {
        PagingMetadata metadata = new PagingMetadata(new PagingProperties(15, 5), requestPathParser);
        Links links = metadata.getLinks();
        assertThat(links.getPrevious()).isEqualTo("cars?offset=10&limit=5");
        
        metadata = new PagingMetadata(new PagingProperties(parseOffset(links.getPrevious()), 5), requestPathParser);
        links = metadata.getLinks();
        assertThat(links.getPrevious()).isEqualTo("cars?offset=5&limit=5");
        
        metadata = new PagingMetadata(new PagingProperties(parseOffset(links.getPrevious()), 5), requestPathParser);
        links = metadata.getLinks();
        assertThat(links.getPrevious()).isEqualTo("cars?offset=0&limit=5");
    }
    
    private int parseOffset(final String header) {
        final Matcher matcher = offsetLimitValue.matcher(header);
        matcher.find();
        final String offset = matcher.group(1);
        return Integer.valueOf(offset);
    }
    
}
