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
package com.vaadin.tests.application;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * 
 * @since
 * @author Vaadin Ltd
 */
@Theme("valo")
public class CommErrorEmulatorUI extends AbstractTestUIWithLog {

    private static class Response {
        private Integer code;
        private Integer time;

        /**
         * @param code
         * @param time
         */
        public Response(Integer code, Integer time) {
            super();
            this.code = code;
            this.time = time;
        }

    }

    private Response uidlResponse = new Response(503, 10);
    private Response heartbeatResponse = new Response(200, 10);

    // Server exceptions will occur in this test as we are writing the response
    // here and not letting the servlet write it
    @Override
    protected void setup(VaadinRequest request) {
        String transport = request.getParameter("transport");

        if ("websocket".equalsIgnoreCase(transport)) {
            getPushConfiguration().setPushMode(PushMode.AUTOMATIC);
            getPushConfiguration().setTransport(Transport.WEBSOCKET);
            log("Using websocket");
        } else if ("long-polling".equalsIgnoreCase(transport)) {
            getPushConfiguration().setPushMode(PushMode.AUTOMATIC);
            getPushConfiguration().setTransport(Transport.LONG_POLLING);
            log("Using long-polling");
        } else {
            log("Using XHR");
        }
        getLayout().setSpacing(true);
        addComponent(createConfigPanel());
        addComponent(createServerConfigPanel());

        addComponent(new Button("Say hello", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                log("Hello");
            }
        }));
    }

    /**
     * @since
     * @return
     */
    private Component createServerConfigPanel() {
        Panel p = new Panel("Server config (NOTE: affects all users)");
        VerticalLayout vl = new VerticalLayout();
        vl.setSpacing(true);
        vl.setMargin(true);
        p.setContent(vl);
        vl.addComponent(createTemporaryResponseCodeSetters("UIDL", uidlResponse));
        vl.addComponent(createTemporaryResponseCodeSetters("Heartbeat",
                heartbeatResponse));
        vl.addComponent(new Button("Activate", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (uidlResponse.code != null && uidlResponse.code != 200) {
                    getServlet().setUIDLResponseCode(CommErrorEmulatorUI.this,
                            uidlResponse.code, uidlResponse.time);
                    log("Responding with " + uidlResponse.code
                            + " to UIDL requests for " + uidlResponse.time
                            + "s");
                }
                if (heartbeatResponse.code != null
                        && heartbeatResponse.code != 200) {
                    getServlet().setHeartbeatResponseCode(
                            CommErrorEmulatorUI.this, heartbeatResponse.code,
                            heartbeatResponse.time);
                    log("Responding with " + heartbeatResponse.code
                            + " to heartbeat requests for "
                            + heartbeatResponse.time + "s");
                }
            }
        }));

        return p;
    }

    private Component createConfigPanel() {
        Panel p = new Panel("Reconnect dialog configuration");
        p.setSizeUndefined();
        final TextField reconnectDialogMessage = new TextField(
                "Reconnect message");
        reconnectDialogMessage.setWidth("50em");
        reconnectDialogMessage.setValue(getState().reconnectDialog.dialogText);
        reconnectDialogMessage
                .addValueChangeListener(new ValueChangeListener() {
                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        getState().reconnectDialog.dialogText = reconnectDialogMessage
                                .getValue();
                    }
                });

        final TextField reconnectDialogGaveUpMessage = new TextField(
                "Reconnect gave up message");
        reconnectDialogGaveUpMessage.setWidth("50em");

        reconnectDialogGaveUpMessage
                .setValue(getState().reconnectDialog.dialogTextGaveUp);
        reconnectDialogGaveUpMessage
                .addValueChangeListener(new ValueChangeListener() {
                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        getState().reconnectDialog.dialogTextGaveUp = reconnectDialogGaveUpMessage
                                .getValue();
                    }
                });
        final TextField reconnectDialogReconnectAttempts = new TextField(
                "Reconnect attempts");
        reconnectDialogReconnectAttempts.setConverter(Integer.class);
        reconnectDialogReconnectAttempts
                .setConvertedValue(getState().reconnectDialog.reconnectAttempts);
        reconnectDialogReconnectAttempts
                .addValueChangeListener(new ValueChangeListener() {
                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        getState().reconnectDialog.reconnectAttempts = (Integer) reconnectDialogReconnectAttempts
                                .getConvertedValue();
                    }
                });
        final TextField reconnectDialogReconnectInterval = new TextField(
                "Reconnect interval (ms)");
        reconnectDialogReconnectInterval.setConverter(Integer.class);
        reconnectDialogReconnectInterval
                .setConvertedValue(getState().reconnectDialog.reconnectInterval);
        reconnectDialogReconnectInterval
                .addValueChangeListener(new ValueChangeListener() {
                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        getState().reconnectDialog.reconnectInterval = (Integer) reconnectDialogReconnectInterval
                                .getConvertedValue();
                    }
                });

        final TextField reconnectDialogGracePeriod = new TextField(
                "Reconnect dialog grace period (ms)");
        reconnectDialogGracePeriod.setConverter(Integer.class);
        reconnectDialogGracePeriod
                .setConvertedValue(getState().reconnectDialog.dialogGracePeriod);
        reconnectDialogGracePeriod
                .addValueChangeListener(new ValueChangeListener() {
                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        getState().reconnectDialog.dialogGracePeriod = (Integer) reconnectDialogGracePeriod
                                .getConvertedValue();
                    }
                });

        VerticalLayout vl = new VerticalLayout();
        vl.setMargin(true);
        vl.setSpacing(true);
        p.setContent(vl);
        vl.addComponents(reconnectDialogMessage, reconnectDialogGaveUpMessage,
                reconnectDialogGracePeriod, reconnectDialogReconnectAttempts,
                reconnectDialogReconnectInterval);
        return p;
    }

    private Component createTemporaryResponseCodeSetters(String type,
            final Response response) {

        HorizontalLayout hl = new HorizontalLayout();
        hl.setSpacing(true);
        hl.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        Label l1 = new Label("Respond to " + type + " requests with code");
        final TextField responseCode = new TextField(null, "" + response.code);
        responseCode.setConverter(Integer.class);
        responseCode.setWidth("5em");
        Label l2 = new Label("for the following");
        final TextField timeField = new TextField(null, "" + response.time);
        timeField.setConverter(Integer.class);
        timeField.setWidth("5em");
        Label l3 = new Label("seconds");

        responseCode.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                Integer code = (Integer) responseCode.getConvertedValue();
                response.code = code;
            }
        });

        timeField.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                Integer time = (Integer) timeField.getConvertedValue();
                response.time = time;
            }
        });

        hl.addComponents(l1, responseCode, l2, timeField, l3);
        return hl;
    }

    protected CommErrorEmulatorServlet getServlet() {
        return (CommErrorEmulatorServlet) VaadinServlet.getCurrent();
    }

}
