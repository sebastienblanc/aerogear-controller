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

import java.util.Map;

import org.jboss.aerogear.controller.test.Util;
import org.junit.Test;

public class WebLinkingTest {

    @Test
    public void weblinkinWithTotal() {
        final PaginationInfo pinfo = PaginationInfo.offset(0).limit(10).build();
        final RequestPathParser requestPathParser = new RequestPathParser(pinfo, "cars?offset=0&limit=10");
        final Links links = new Links(requestPathParser, new PaginationProperties(pinfo.getOffset(), pinfo.getLimit(), 100));
        final WebLinking weblinking = new WebLinking(links);
        final String linkHeader = weblinking.getLinkHeaders();
        final Map<String, String> linksHeaders = Util.parseWebLinkHeader(linkHeader);
        assertThat(linksHeaders.get(WebLinking.PREVIOUS)).isEqualTo("cars?offset=0&limit=10");
        assertThat(linksHeaders.get(WebLinking.NEXT)).isEqualTo("cars?offset=10&limit=10");
        assertThat(weblinking.getLinkHeaderName()).isEqualTo("Link");
    }
    
    @Test
    public void weblinkingLimit5() {
        final PaginationInfo pinfo = PaginationInfo.offset(0).limit(5).build();
        final RequestPathParser requestPathParser = new RequestPathParser(pinfo, "cars?offset=0&limit=5");
        final Links links = new Links(requestPathParser, new PaginationProperties(pinfo.getOffset(), pinfo.getLimit()));
        final WebLinking weblinking = new WebLinking(links);
        final String linkHeader = weblinking.getLinkHeaders();
        final Map<String, String> linksHeaders = Util.parseWebLinkHeader(linkHeader);
        assertThat(linksHeaders.get(WebLinking.PREVIOUS)).isEqualTo("cars?offset=0&limit=5");
        assertThat(linksHeaders.get(WebLinking.NEXT)).isEqualTo("cars?offset=5&limit=5");
    }
    
    @Test
    public void weblinkingDefaultOffset() {
        final PaginationInfo pinfo = PaginationInfo.offset(0).limit(5).build();
        final RequestPathParser requestPathParser = new RequestPathParser(pinfo, "cars?limit=5");
        final Links links = new Links(requestPathParser, new PaginationProperties(pinfo.getOffset(), pinfo.getLimit()));
        final WebLinking weblinking = new WebLinking(links);
        final String linkHeader = weblinking.getLinkHeaders();
        final Map<String, String> linksHeaders = Util.parseWebLinkHeader(linkHeader);
        assertThat(linksHeaders.get(WebLinking.PREVIOUS)).isEqualTo("cars?limit=5&offset=0");
        assertThat(linksHeaders.get(WebLinking.NEXT)).isEqualTo("cars?limit=5&offset=5");
    }
    
    @Test
    public void weblinkingDefaultLimit() {
        final PaginationInfo pinfo = PaginationInfo.offset(0).limit(10).build();
        final RequestPathParser requestPathParser = new RequestPathParser(pinfo, "cars?offset=10");
        final Links links = new Links(requestPathParser, new PaginationProperties(pinfo.getOffset(), pinfo.getLimit()));
        final WebLinking weblinking = new WebLinking(links);
        final String linkHeader = weblinking.getLinkHeaders();
        final Map<String, String> linksHeaders = Util.parseWebLinkHeader(linkHeader);
        assertThat(linksHeaders.get(WebLinking.PREVIOUS)).isEqualTo("cars?offset=0&limit=10");
        assertThat(linksHeaders.get(WebLinking.NEXT)).isEqualTo("cars?offset=10&limit=10");
    }
    
}
