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
package com.vaadin.tests.widgetset.client.gwtrpc;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.widgetset.server.gwtrpc.GwtRpcButton;

/**
 * Dummy connector to test our Vaadin/GWT RPC bug. In a Vaadin environment with
 * DevMode enabled, a pure GWT RPC call would throw an exception. See #11709.
 *
 * @author Vaadin Ltd
 */
@SuppressWarnings("serial")
@Connect(GwtRpcButton.class)
public class GwtRpcButtonConnector extends AbstractComponentConnector {

    static Logger logger = Logger.getLogger(GwtRpcButtonConnector.class
            .getName());
    static {
        logger.setLevel(Level.ALL);
    }

    @Override
    public Button getWidget() {
        return (Button) super.getWidget();
    }

    @Override
    protected Button createWidget() {
        return GWT.create(Button.class);
    }

    private void log(String message) {
        logger.log(Level.INFO, message);
    }

    @Override
    public void init() {
        super.init();

        log("GwtRpcButtonTestConnector init");

        getWidget().setText("Click me");
        getWidget().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                doRPC();
            }

        });
    }

    /**
     * The ID of the label in case the test is successful.
     */
    public static final String SUCCESS_LABEL_ID = "yes";

    /**
     * The ID of the label in case the test failed.
     */
    public static final String FAIL_LABEL_ID = "no";

    /*
     * Make an RPC to test our bug.
     */
    private void doRPC() {
        log("GwtRpcButtonTestConnector onClick");

        GwtRpcServiceTestAsync service = GWT.create(GwtRpcServiceTest.class);

        service.giveMeThat("honey", "sugar", new AsyncCallback<String>() {

            @Override
            public void onSuccess(String result) {
                showResult(result, SUCCESS_LABEL_ID);
            }

            @Override
            public void onFailure(Throwable caught) {
                showResult(caught.getMessage(), FAIL_LABEL_ID);
            }

            /*
             * Show the result box.
             */
            private void showResult(String result, String labelID) {
                DialogBox box = new DialogBox(true);
                Label label = new Label(result);
                label.getElement().setId(labelID);
                box.add(label);
                box.center();
                box.show();
            }

        });
    }
}
