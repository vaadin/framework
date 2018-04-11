/*
 * Copyright 2000-2018 Vaadin Ltd.
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

package com.vaadin.client.ui.loginform;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FocusWidget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractSingleComponentContainerConnector;
import com.vaadin.client.ui.VTextField;
import com.vaadin.client.ui.button.ButtonConnector;
import com.vaadin.client.ui.nativebutton.NativeButtonConnector;
import com.vaadin.client.ui.textfield.TextFieldConnector;
import com.vaadin.shared.Connector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.loginform.LoginFormConstants;
import com.vaadin.shared.ui.loginform.LoginFormRpc;
import com.vaadin.shared.ui.loginform.LoginFormState;

@Connect(com.vaadin.ui.LoginForm.class)
public class LoginFormConnector
        extends AbstractSingleComponentContainerConnector {

    private LoginFormRpc loginFormRpc;

    @Override
    public void updateCaption(ComponentConnector connector) {

    }

    @Override
    public VLoginForm getWidget() {
        return (VLoginForm) super.getWidget();
    }

    @Override
    protected void init() {
        super.init();

        loginFormRpc = getRpcProxy(LoginFormRpc.class);
        getWidget().addSubmitCompleteHandler(event -> {
            valuesChanged();
            loginFormRpc.submitCompleted();
        });
    }

    @Override
    public LoginFormState getState() {
        return (LoginFormState) super.getState();
    }

    @Override
    public void onConnectorHierarchyChange(
            ConnectorHierarchyChangeEvent event) {
        ComponentConnector content = getContent();
        if (content != null) {
            getWidget().setWidget(getContentWidget());
        }
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        LoginFormState state = getState();
        configureTextField(getUsernameFieldConnector(), "username");
        configureTextField(getPasswordFieldConnector(), "password");
        addSubmitButtonClickHandler(state.loginButtonConnector);
        getWidget().setAction(
                getResourceUrl(LoginFormConstants.LOGIN_RESOURCE_NAME));
    }

    private TextFieldConnector getUsernameFieldConnector() {
        return (TextFieldConnector) getState().userNameFieldConnector;
    }

    private TextFieldConnector getPasswordFieldConnector() {
        return (TextFieldConnector) getState().passwordFieldConnector;
    }

    private VTextField configureTextField(TextFieldConnector connector,
            String id) {
        if (connector != null) {
            VTextField textField = connector.getWidget();

            textField.addKeyDownHandler(new SubmitKeyHandler());

            Element element = textField.getElement();
            String externalId = element.getId();
            if (externalId == null || externalId.isEmpty()
                    || externalId.startsWith("gwt-")) {
                element.setId(id);
            }
            DOM.setElementAttribute(element, "name", id);
            DOM.setElementAttribute(element, "autocomplete", "on");

            return textField;
        } else {
            return null;
        }
    }

    private void loginLater() {
        Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
            @Override
            public boolean execute() {
                login();
                return false;
            }
        }, 100);
    }

    private void login() {
        getWidget().submit();
    }

    private void addSubmitButtonClickHandler(Connector buttonConnector) {
        if (buttonConnector instanceof ButtonConnector) {
            addSubmitButtonClickHandler(
                    ((ButtonConnector) buttonConnector).getWidget());
        } else if (buttonConnector instanceof NativeButtonConnector) {
            addSubmitButtonClickHandler(
                    ((NativeButtonConnector) buttonConnector).getWidget());
        }
    }

    private void addSubmitButtonClickHandler(FocusWidget button) {
        button.addClickHandler(event -> login());
    }

    private void valuesChanged() {
        TextFieldConnector connector = getUsernameFieldConnector();
        if (connector != null) {
            connector.flush();
        }
        connector = getPasswordFieldConnector();
        if (connector != null) {
            connector.flush();
        }
    }

    private class SubmitKeyHandler implements KeyDownHandler {

        private int previousKeyCode;

        @Override
        public void onKeyDown(KeyDownEvent event) {
            int keyCode = event.getNativeKeyCode();
            if (keyCode == KeyCodes.KEY_ENTER) {
                if (isInAutoComplete()) {
                    previousKeyCode = keyCode;
                } else {
                    loginLater();
                }
            } else {
                previousKeyCode = keyCode;
            }
        }

        private boolean isInAutoComplete() {
            switch (previousKeyCode) {
            case KeyCodes.KEY_PAGEUP:
            case KeyCodes.KEY_PAGEDOWN:
            case KeyCodes.KEY_UP:
            case KeyCodes.KEY_DOWN:
                return true;
            default:
                return false;
            }
        }
    }
}
