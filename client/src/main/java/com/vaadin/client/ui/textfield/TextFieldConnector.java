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
package com.vaadin.client.ui.textfield;

import com.vaadin.client.BrowserInfo;
import com.vaadin.client.event.InputEvent;
import com.vaadin.client.ui.VTextField;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;
import com.vaadin.shared.ui.textfield.TextFieldState;
import com.vaadin.ui.TextField;

/**
 * Connector class for TextField.
 */
@Connect(value = TextField.class, loadStyle = LoadStyle.EAGER)
public class TextFieldConnector extends AbstractTextFieldConnector {

    @Override
    protected void init() {
        super.init();
        getWidget().addChangeHandler(event -> sendValueChange());
        getWidget().addDomHandler(
                event -> getValueChangeHandler().scheduleValueChange(),
                InputEvent.getType());
    }

    @Override
    public TextFieldState getState() {
        return (TextFieldState) super.getState();
    }

    @Override
    public VTextField getWidget() {
        VTextField vTextField = (VTextField) super.getWidget();

        /*-
         * Stop the browser from showing its own suggestion popup.
         *
         * Using an invalid value instead of "off" as suggested by
         * https://developer.mozilla.org/en-US/docs/Web/Security/Securing_your_site/Turning_off_form_autocompletion
         *
         * Leaving the non-standard Safari options autocapitalize and
         * autocorrect untouched since those do not interfere in the same
         * way.
         */
        if (BrowserInfo.get().isChrome()) {
            // Chrome supports "off" and random number does not work with Chrome
            vTextField.getElement().setAttribute("autocomplete", "off");
        } else {
            vTextField.getElement().setAttribute("autocomplete",
                    Math.random() + "");
        }
        return vTextField;
    }
}
