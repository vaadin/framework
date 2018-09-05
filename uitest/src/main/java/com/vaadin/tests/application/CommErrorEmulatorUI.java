package com.vaadin.tests.application;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.TextField;

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
            log("Using websocket");
        } else if ("websocket-xhr".equalsIgnoreCase(transport)) {
            log("Using websocket for push only");
        } else if ("long-polling".equalsIgnoreCase(transport)) {
            log("Using long-polling");
        } else {
            log("Using XHR");
        }
        getLayout().setSpacing(true);
        addComponent(createConfigPanel());
        addComponent(createServerConfigPanel());

        addComponent(new Button("Say hello", event -> log("Hello")));
    }

    private Component createServerConfigPanel() {
        Panel p = new Panel("Server config (NOTE: affects all users)");
        VerticalLayout vl = new VerticalLayout();
        vl.setSpacing(true);
        vl.setMargin(true);
        p.setContent(vl);
        vl.addComponent(
                createTemporaryResponseCodeSetters("UIDL", uidlResponse));
        vl.addComponent(createTemporaryResponseCodeSetters("Heartbeat",
                heartbeatResponse));
        vl.addComponent(new Button("Activate", event -> {
            if (uidlResponse.code != null && uidlResponse.code != 200) {
                getServlet().setUIDLResponseCode(CommErrorEmulatorUI.this,
                        uidlResponse.code, uidlResponse.time);
                log("Responding with " + uidlResponse.code
                        + " to UIDL requests for " + uidlResponse.time + "s");
            }
            if (heartbeatResponse.code != null
                    && heartbeatResponse.code != 200) {
                getServlet().setHeartbeatResponseCode(CommErrorEmulatorUI.this,
                        heartbeatResponse.code, heartbeatResponse.time);
                log("Responding with " + heartbeatResponse.code
                        + " to heartbeat requests for " + heartbeatResponse.time
                        + "s");
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
        reconnectDialogMessage
                .setValue(getReconnectDialogConfiguration().getDialogText());
        reconnectDialogMessage.addValueChangeListener(
                event -> getReconnectDialogConfiguration()
                        .setDialogText(reconnectDialogMessage.getValue()));

        final TextField reconnectDialogGaveUpMessage = new TextField(
                "Reconnect gave up message");
        reconnectDialogGaveUpMessage.setWidth("50em");

        reconnectDialogGaveUpMessage.setValue(
                getReconnectDialogConfiguration().getDialogTextGaveUp());
        reconnectDialogGaveUpMessage.addValueChangeListener(
                event -> getReconnectDialogConfiguration().setDialogTextGaveUp(
                        reconnectDialogGaveUpMessage.getValue()));
        final TextField reconnectDialogReconnectAttempts = new TextField(
                "Reconnect attempts");
        reconnectDialogReconnectAttempts.setConverter(Integer.class);
        reconnectDialogReconnectAttempts.setConvertedValue(
                getReconnectDialogConfiguration().getReconnectAttempts());
        reconnectDialogReconnectAttempts.addValueChangeListener(
                event -> getReconnectDialogConfiguration().setReconnectAttempts(
                        (Integer) reconnectDialogReconnectAttempts
                                .getConvertedValue()));
        final TextField reconnectDialogReconnectInterval = new TextField(
                "Reconnect interval (ms)");
        reconnectDialogReconnectInterval.setConverter(Integer.class);
        reconnectDialogReconnectInterval.setConvertedValue(
                getReconnectDialogConfiguration().getReconnectInterval());
        reconnectDialogReconnectInterval.addValueChangeListener(
                event -> getReconnectDialogConfiguration().setReconnectInterval(
                        (Integer) reconnectDialogReconnectInterval
                                .getConvertedValue()));

        final TextField reconnectDialogGracePeriod = new TextField(
                "Reconnect dialog grace period (ms)");
        reconnectDialogGracePeriod.setConverter(Integer.class);
        reconnectDialogGracePeriod.setConvertedValue(
                getReconnectDialogConfiguration().getDialogGracePeriod());
        reconnectDialogGracePeriod.addValueChangeListener(
                event -> getReconnectDialogConfiguration().setDialogGracePeriod(
                        (Integer) reconnectDialogGracePeriod
                                .getConvertedValue()));

        final CheckBox reconnectDialogModal = new CheckBox(
                "Reconnect dialog modality");
        reconnectDialogModal
                .setValue(getReconnectDialogConfiguration().isDialogModal());
        reconnectDialogModal.addValueChangeListener(
                event -> getReconnectDialogConfiguration()
                        .setDialogModal(reconnectDialogModal.getValue()));

        VerticalLayout vl = new VerticalLayout();
        vl.setMargin(true);
        vl.setSpacing(true);
        p.setContent(vl);
        vl.addComponents(reconnectDialogMessage, reconnectDialogGaveUpMessage,
                reconnectDialogGracePeriod, reconnectDialogModal,
                reconnectDialogReconnectAttempts,
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

        responseCode.addValueChangeListener(event -> {
            Integer code = (Integer) responseCode.getConvertedValue();
            response.code = code;
        });

        timeField.addValueChangeListener(event -> {
            Integer time = (Integer) timeField.getConvertedValue();
            response.time = time;
        });

        hl.addComponents(l1, responseCode, l2, timeField, l3);
        return hl;
    }

    protected CommErrorEmulatorServlet getServlet() {
        return (CommErrorEmulatorServlet) VaadinServlet.getCurrent();
    }

}
