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

public class VaadinPortletTest {

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
