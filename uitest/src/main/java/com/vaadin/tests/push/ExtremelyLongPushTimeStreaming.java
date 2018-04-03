package com.vaadin.tests.push;

import com.vaadin.annotations.Push;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.shared.ui.ui.UIState.PushConfigurationState;

@Push(transport = Transport.STREAMING)
public class ExtremelyLongPushTimeStreaming extends ExtremelyLongPushTime {

    @Override
    public void init(VaadinRequest request) {
        super.init(request);
        // Don't use fallback so we can easier detect failures
        getPushConfiguration().setParameter(
                PushConfigurationState.FALLBACK_TRANSPORT_PARAM, "none");
    }
}
