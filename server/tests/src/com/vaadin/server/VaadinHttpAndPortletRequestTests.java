/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.server;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Enumeration;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.vaadin.server.VaadinPortlet.VaadinHttpAndPortletRequest;
import com.vaadin.server.VaadinPortletService;

//Have to ignore this class - otherwise JUnit tries to instantiate it...
@Ignore
public abstract class VaadinHttpAndPortletRequestTests<T extends VaadinHttpAndPortletRequest> {

    protected VaadinHttpAndPortletRequest sut;
    protected HttpServletRequest servletRequest;
    protected PortletRequest portletRequest;
    protected VaadinPortletService vaadinPortletService;

    protected abstract T createSut();

    @Before
    public void setup() {
        portletRequest = mock(PortletRequest.class);
        vaadinPortletService = mock(VaadinPortletService.class);
        servletRequest = mock(HttpServletRequest.class);

        sut = createSut();
    }

    @Test
    public void parameterIsFetchedFromServletRequest() {
        when(servletRequest.getParameter("foo")).thenReturn("bar");

        String parameter = sut.getParameter("foo");

        assertThat(parameter, is("bar"));
    }

    @Test
    public void originalParameterIsOverridden() {
        when(servletRequest.getParameter("foo")).thenReturn("braa");
        when(portletRequest.getParameter("foo")).thenReturn("bar");

        String parameter = sut.getParameter("foo");

        assertThat(parameter, is("bar"));
    }

    @Test
    public void remoteAddressIsFetchedFromServletRequest() {
        when(servletRequest.getRemoteAddr()).thenReturn("foo");

        String remoteAddr = sut.getRemoteAddr();

        assertThat(remoteAddr, is("foo"));
    }

    @Test
    public void remoteHostIsFetchedFromServletRequest() {
        when(servletRequest.getRemoteHost()).thenReturn("foo");

        String remoteHost = sut.getRemoteHost();

        assertThat(remoteHost, is("foo"));
    }

    @Test
    public void remotePortIsFetchedFromServletRequest() {
        when(servletRequest.getRemotePort()).thenReturn(12345);

        int remotePort = sut.getRemotePort();

        assertThat(remotePort, is(12345));
    }

    @Test
    public void headerIsFetchedFromServletRequest() {
        when(servletRequest.getHeader("foo")).thenReturn("bar");

        String header = sut.getHeader("foo");

        assertThat(header, is("bar"));
    }

    @Test
    public void headerNamesAreFetchedFromServletRequest() {
        Enumeration expectedHeaderNames = mock(Enumeration.class);
        when(servletRequest.getHeaderNames()).thenReturn(expectedHeaderNames);

        Enumeration<String> actualHeaderNames = sut.getHeaderNames();

        assertThat(actualHeaderNames, is(expectedHeaderNames));
    }

    @Test
    public void headersAreFetchedFromServletRequest() {
        Enumeration expectedHeaders = mock(Enumeration.class);
        when(servletRequest.getHeaders("foo")).thenReturn(expectedHeaders);

        Enumeration<String> actualHeaders = sut.getHeaders("foo");

        assertThat(actualHeaders, is(expectedHeaders));
    }

    @Test
    public void parameterMapIsFetchedFromServletRequest() {
        Map expectedParameterMap = mock(Map.class);
        when(servletRequest.getParameterMap()).thenReturn(expectedParameterMap);

        Map<String, String[]> actualParameterMap = sut.getParameterMap();

        assertThat(actualParameterMap, is(expectedParameterMap));
    }
}
