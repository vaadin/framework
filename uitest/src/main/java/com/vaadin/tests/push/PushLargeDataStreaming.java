package com.vaadin.tests.push;

import com.vaadin.annotations.Push;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.shared.ui.ui.UIState.PushConfigurationState;

@Push(transport = Transport.STREAMING)
public class PushLargeDataStreaming extends PushLargeData {

    @Override
    protected void setup(VaadinRequest request) {
        super.setup(request);
        getPushConfiguration().setTransport(Transport.STREAMING);
        getPushConfiguration().setParameter(
                PushConfigurationState.FALLBACK_TRANSPORT_PARAM, "none");
    }
}
