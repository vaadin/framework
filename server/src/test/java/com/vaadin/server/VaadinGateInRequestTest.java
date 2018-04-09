package com.vaadin.server;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import com.vaadin.server.VaadinPortlet.VaadinGateInRequest;

public class VaadinGateInRequestTest
        extends VaadinHttpAndPortletRequestTestBase<VaadinGateInRequest> {

    @Override
    protected VaadinGateInRequest createSut() {

        VaadinGateInRequest request = new VaadinGateInRequest(portletRequest,
                vaadinPortletService);

        // Although partial mocking can be considered a code smell,
        // here it's actually quite useful to mock reflection calls.
        VaadinGateInRequest spy = spy(request);
        doReturn(servletRequest).when(spy).getServletRequest(portletRequest);

        return spy;
    }
}
