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

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.aerogear.controller.router.RouteContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DefaultPagingStrategyTest {

    @Mock
    private RouteContext routeContext;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpServletRequest request;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(routeContext.getResponse()).thenReturn(response);
        when(routeContext.getRequest()).thenReturn(request);
        when(request.getRequestURI()).thenReturn("/app/cars");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/app/cars"));
        when(routeContext.getRequestPath()).thenReturn("cars");
    }

    @Test
    public void processFirst() {
        when(routeContext.getRequestPath()).thenReturn("cars");
        when(request.getQueryString()).thenReturn("myoffset=0&mylimit=5&color=red");
        final PaginationInfo pagingInfo = PaginationInfo.offset("myoffset", 0).customHeadersPrefix("Test-").limit("mylimit", 5)
                .build();
        final Collection<Integer> results = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5));
        PaginationHandler.defaultPagingStrategy().postInvocation(results, routeContext, pagingInfo);
        verify(response, never()).setHeader(eq("Test-Links-Previous"), anyString());
        verify(response).setHeader("Test-Links-Next", "http://localhost:8080/app/cars?myoffset=5&mylimit=5&color=red");
    }

    @Test
    public void processMiddle() {
        when(routeContext.getRequestPath()).thenReturn("cars");
        when(request.getQueryString()).thenReturn("color=red&myoffset=5&mylimit=5");
        final PaginationInfo pagingInfo = PaginationInfo.offset("myoffset", 5).customHeadersPrefix("Test-").limit("mylimit", 5)
                .build();
        final Collection<Integer> results = new ArrayList<Integer>(Arrays.asList(6, 7, 8, 9, 10));
        PaginationHandler.defaultPagingStrategy().postInvocation(results, routeContext, pagingInfo);
        verify(response).setHeader("Test-Links-Previous", "http://localhost:8080/app/cars?color=red&myoffset=0&mylimit=5");
        verify(response).setHeader("Test-Links-Next", "http://localhost:8080/app/cars?color=red&myoffset=10&mylimit=5");
    }

    @Test
    public void processLast() {
        when(routeContext.getRequestPath()).thenReturn("cars");
        when(request.getQueryString()).thenReturn("offset=5&color=red&limit=5");
        final PaginationInfo pagingInfo = PaginationInfo.offset(5).limit(5).customHeadersPrefix("Test-").build();
        final Collection<Integer> results = new ArrayList<Integer>(Arrays.asList(6, 7));
        PaginationHandler.defaultPagingStrategy().postInvocation(results, routeContext, pagingInfo);
        verify(response).setHeader("Test-Links-Previous", "http://localhost:8080/app/cars?offset=0&color=red&limit=5");
    }

    @Test
    public void missingPagingRequestParamButHasDefault() {
        when(routeContext.getRequestPath()).thenReturn("cars");
        when(request.getQueryString()).thenReturn("color=red&limit=5");
        final PaginationInfo pagingInfo = PaginationInfo.offset(0).limit(5).customHeaders().build();
        final Collection<Integer> results = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5));
        PaginationHandler.defaultPagingStrategy().postInvocation(results, routeContext, pagingInfo);
        verify(response).setHeader("AG-Links-Next", "http://localhost:8080/app/cars?color=red&limit=5&offset=5");
    }

    @Test
    public void weblinkingHeader() {
        when(routeContext.getRequestPath()).thenReturn("cars");
        when(request.getQueryString()).thenReturn("myoffset=0&mylimit=5&color=red");
        final PaginationInfo pagingInfo = PaginationInfo.offset("myoffset", 5).limit("mylimit", 5).webLinking(true).build();
        final Collection<Integer> results = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5));
        PaginationHandler.defaultPagingStrategy().postInvocation(results, routeContext, pagingInfo);
        verify(response, never()).setHeader(eq("Test-Links-Previous"), anyString());
        verify(response, never()).setHeader(eq("Test-Links-Next"), anyString());
        verify(response).setHeader(eq("Link"), anyString());
    }

}
