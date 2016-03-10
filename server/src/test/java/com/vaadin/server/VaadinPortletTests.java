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
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.portlet.PortalContext;
import javax.portlet.PortletRequest;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.VaadinPortlet.VaadinGateInRequest;
import com.vaadin.server.VaadinPortlet.VaadinLiferayRequest;
import com.vaadin.server.VaadinPortlet.VaadinWebSpherePortalRequest;

public class VaadinPortletTests {

    private VaadinPortlet sut;
    private PortletRequest portletRequest;
    private PortalContext portalContext;

    @Before
    public void setup() {
        sut = new VaadinPortlet();

        portletRequest = mock(PortletRequest.class);
        portalContext = mock(PortalContext.class);

        when(portletRequest.getPortalContext()).thenReturn(portalContext);
    }

    private void mockPortalInfo(String name) {
        when(portalContext.getPortalInfo()).thenReturn(name);
    }

    private VaadinPortletRequest createRequest() {
        VaadinPortletRequest request = sut.createVaadinRequest(portletRequest);
        return request;
    }

    @Test
    public void gateInRequestIsCreated() {
        mockPortalInfo("gatein");

        VaadinPortletRequest request = createRequest();

        assertThat(request, instanceOf(VaadinGateInRequest.class));
    }

    @Test
    public void liferayRequestIsCreated() {
        mockPortalInfo("liferay");

        VaadinPortletRequest request = createRequest();

        assertThat(request, instanceOf(VaadinLiferayRequest.class));
    }

    @Test
    public void webspherePortalRequestIsCreated() {
        mockPortalInfo("websphere portal");

        VaadinPortletRequest request = createRequest();

        assertThat(request, instanceOf(VaadinWebSpherePortalRequest.class));
    }

    @Test
    public void defaultPortletRequestIsCreated() {
        mockPortalInfo("foobar");

        VaadinPortletRequest request = createRequest();

        assertThat(request, instanceOf(VaadinPortletRequest.class));
    }

}
