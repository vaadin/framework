/*
 * Copyright 2000-2013 Vaadin Ltd.
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
package com.vaadin.server.communication;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Locale;

import org.easymock.EasyMock;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.server.ClientConnector;
import com.vaadin.server.LegacyCommunicationManager;
import com.vaadin.server.SystemMessages;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.UI;

/**
 * 
 * @since
 * @author Vaadin Ltd
 */
public class UidlWriterTest {

    private UI ui;
    private UidlWriter uidlWriter;

    @Before
    public void setUp() {
        SystemMessages messages = EasyMock.createNiceMock(SystemMessages.class);
        EasyMock.expect(messages.isSessionExpiredNotificationEnabled())
                .andReturn(true).anyTimes();
        EasyMock.replay(messages);

        VaadinService service = EasyMock.createNiceMock(VaadinService.class);
        EasyMock.expect(
                service.getSystemMessages(EasyMock.anyObject(Locale.class),
                        EasyMock.anyObject(VaadinRequest.class)))
                .andReturn(messages).anyTimes();
        EasyMock.replay(service);

        LegacyCommunicationManager manager = EasyMock
                .createNiceMock(LegacyCommunicationManager.class);
        EasyMock.replay(manager);

        WrappedSession wrappedSession = EasyMock
                .createNiceMock(WrappedSession.class);
        EasyMock.expect(wrappedSession.getMaxInactiveInterval()).andReturn(100)
                .times(3).andReturn(200);

        EasyMock.replay(wrappedSession);

        VaadinSession session = EasyMock.createNiceMock(VaadinSession.class);
        EasyMock.expect(session.getService()).andReturn(service).anyTimes();
        EasyMock.expect(session.getCommunicationManager()).andReturn(manager)
                .anyTimes();
        EasyMock.expect(session.getSession()).andReturn(wrappedSession)
                .anyTimes();
        EasyMock.replay(session);

        ConnectorTracker tracker = EasyMock
                .createNiceMock(ConnectorTracker.class);
        EasyMock.expect(tracker.getDirtyVisibleConnectors())
                .andReturn(new ArrayList<ClientConnector>()).anyTimes();
        EasyMock.replay(tracker);

        ui = EasyMock.createNiceMock(UI.class);
        EasyMock.expect(ui.getSession()).andReturn(session).anyTimes();
        EasyMock.expect(ui.getConnectorTracker()).andReturn(tracker).anyTimes();
        EasyMock.replay(ui);

        uidlWriter = new UidlWriter();
    }

    @Test
    public void testMetadataWriterState() throws IOException, JSONException {

        Assert.assertEquals(
                "Metadata should contain redirect interval on first write",
                115, getRedirect(uidl(false, false)).optInt("interval"));
        Assert.assertNull(
                "Metadata should not contain redirect interval on second write",
                getRedirect(uidl(false, false)));
        Assert.assertEquals(
                "Metadata should contain redirect interval on repaintAll", 115,
                getRedirect(uidl(true, false)).optInt("interval"));
        Assert.assertEquals(
                "Metadata should contain redirect interval when changed in session",
                215, getRedirect(uidl(false, false)).optInt("interval"));
    }

    private JSONObject uidl(boolean repaintAll, boolean async)
            throws IOException, JSONException {
        StringWriter writer = new StringWriter();
        uidlWriter.write(ui, writer, repaintAll, async);
        return new JSONObject("{" + writer.toString() + "}");
    }

    private JSONObject getRedirect(JSONObject json) throws JSONException {
        return json.getJSONObject("meta").optJSONObject("timedRedirect");

    }
}
