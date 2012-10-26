package com.vaadin.tests.server;

import org.easymock.EasyMock;
import org.junit.Test;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServiceSession;
import com.vaadin.ui.UI;
import com.vaadin.ui.UI.CleanupEvent;
import com.vaadin.ui.UI.CleanupListener;

public class TestUICleanup {

    @Test
    public void uiDetach() {
        VaadinService service = EasyMock.createMock(VaadinService.class);

        UI ui = EasyMock.createMock(UI.class);
        ui.setSession(null);
        ui.detach();
        ui.fireCleanupEvent();
        EasyMock.expect(ui.getUIId()).andReturn(1);

        EasyMock.replay(service, ui);

        VaadinServiceSession session = new VaadinServiceSession(service);

        session.cleanupUI(ui);
    }

    @Test
    public void uiCleanupListeners() {
        CleanupListener listener = EasyMock.createMock(CleanupListener.class);
        listener.cleanup(EasyMock.anyObject(CleanupEvent.class));

        EasyMock.replay(listener);

        UI ui = new UI() {
            @Override
            protected void init(VaadinRequest request) {
            }
        };

        ui.addCleanupListener(listener);
        ui.fireCleanupEvent();
    }
}
