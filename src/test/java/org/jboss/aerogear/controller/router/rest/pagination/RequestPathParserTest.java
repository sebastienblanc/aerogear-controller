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

import org.junit.Test;

public class RequestPathParserTest {

    @Test
    public void replaceMultipleParams() {
        final String requestPath = "http://localhost/app/cars?color=red&limit=10&offset=0&brand=BMW";
        final PaginationInfo paginationInfo = PaginationInfo.offset(0).limit(10).build();
        final RequestPathParser parser = new RequestPathParser(paginationInfo, requestPath);
        final String replace = parser.replace(10, 20);
        assertThat(replace).isEqualTo("http://localhost/app/cars?color=red&limit=20&offset=10&brand=BMW");
    }
    
    @Test
    public void replaceLimitOffset() {
        final String requestPath = "https://localhost:8080/app/cars?limit=10&offset=0";
        final PaginationInfo paginationInfo = PaginationInfo.offset(0).limit(10).build();
        final RequestPathParser parser = new RequestPathParser(paginationInfo, requestPath);
        final String replace = parser.replace(10, 20);
        assertThat(replace).isEqualTo("https://localhost:8080/app/cars?limit=20&offset=10");
    }
    
    @Test
    public void replaceOffsetLimiit() {
        final String requestPath = "cars?offset=0&limit=10";
        final PaginationInfo paginationInfo = PaginationInfo.offset(0).limit(10).build();
        final RequestPathParser parser = new RequestPathParser(paginationInfo, requestPath);
        final String replace = parser.replace(10, 20);
        assertThat(replace).isEqualTo("cars?offset=10&limit=20");
    }
    
    @Test
    public void replaceOffsetLimiitRandomOrder() {
        final String requestPath = "cars?brand=BMW&offset=0&color=red&limit=10&year=2013";
        final PaginationInfo paginationInfo = PaginationInfo.offset(0).limit(10).build();
        final RequestPathParser parser = new RequestPathParser(paginationInfo, requestPath);
        final String replace = parser.replace(10, 20);
        assertThat(replace).isEqualTo("cars?brand=BMW&offset=10&color=red&limit=20&year=2013");
    }
    
    @Test 
    public void missspelledOffsetShouldAddToEndOfResponse() {
        final String invalidRequestPath = "cars?brand=BMW&ofets=0&color=red&limit=10&year=2013";
        final PaginationInfo paginationInfo = PaginationInfo.offset(0).limit(10).build();
        final RequestPathParser parser = new RequestPathParser(paginationInfo, invalidRequestPath);
        final String replace = parser.replace(10, 10);
        assertThat(replace).isEqualTo("cars?brand=BMW&ofets=0&color=red&limit=10&year=2013&offset=10");
    }
    
    @Test 
    public void defaultOffsetIfOffsetIsMissingFromRequest() {
        final String invalidRequestPath = "cars?brand=BMW&color=red&limit=10&year=2013";
        final PaginationInfo paginationInfo = PaginationInfo.offset(0).limit(10).build();
        final RequestPathParser parser = new RequestPathParser(paginationInfo, invalidRequestPath);
        final String path = parser.replace(10, 20);
        assertThat(path).isEqualTo("cars?brand=BMW&color=red&limit=20&year=2013&offset=10");
    }
    
    @Test 
    public void defaultOffsetLimitIfBothAreMissingFromRequest() {
        final String invalidRequestPath = "cars?brand=BMW&color=red&year=2013";
        final PaginationInfo paginationInfo = PaginationInfo.offset(0).limit(10).build();
        final RequestPathParser parser = new RequestPathParser(paginationInfo, invalidRequestPath);
        final String path = parser.replace(10, 20);
        assertThat(path).isEqualTo("cars?brand=BMW&color=red&year=2013&offset=10&limit=20");
    }


}
