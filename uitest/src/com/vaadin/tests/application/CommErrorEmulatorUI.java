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
import com.vaadin.ui.TextField;

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

        addComponent(createTemporaryResponseCodeSetters("UIDL", uidlResponse));
        addComponent(createTemporaryResponseCodeSetters("Heartbeat",
                heartbeatResponse));
        addComponent(new Button("Do it", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (uidlResponse.code != null && uidlResponse.code != 200) {
                    getServlet().setUIDLResponseCode(uidlResponse.code,
                            uidlResponse.time);
                }
                if (heartbeatResponse.code != null
                        && heartbeatResponse.code != 200) {
                    getServlet().setHeartbeatResponseCode(
                            heartbeatResponse.code, heartbeatResponse.time);
                }
            }
        }));
        addComponent(new Button("Say hello", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                log("Hello");
            }
        }));
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
