package com.vaadin.server;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import com.vaadin.server.VaadinPortlet.VaadinLiferayRequest;

public class VaadinLiferayRequestTest
        extends VaadinHttpAndPortletRequestTestBase<VaadinLiferayRequest> {

    @Override
    protected VaadinLiferayRequest createSut() {

        VaadinLiferayRequest request = new VaadinLiferayRequest(portletRequest,
                vaadinPortletService);

        // Although partial mocking can be considered a code smell,
        // here it's actually quite useful to mock reflection calls.
        VaadinLiferayRequest spy = spy(request);
        doReturn(servletRequest).when(spy).getServletRequest(portletRequest);

        return spy;
    }
}
