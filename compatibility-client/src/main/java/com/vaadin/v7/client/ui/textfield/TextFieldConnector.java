/*
 * Copyright 2000-2022 Vaadin Ltd.
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

package com.vaadin.v7.client.ui.textfield;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.Paintable;
import com.vaadin.client.UIDL;
import com.vaadin.client.Util;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;
import com.vaadin.v7.client.ui.AbstractFieldConnector;
import com.vaadin.v7.client.ui.VTextField;
import com.vaadin.v7.shared.ui.textfield.AbstractTextFieldState;
import com.vaadin.v7.shared.ui.textfield.TextFieldConstants;
import com.vaadin.v7.ui.TextField;

@Deprecated
@Connect(value = TextField.class, loadStyle = LoadStyle.EAGER)
public class TextFieldConnector extends AbstractFieldConnector
        implements Paintable {

    @Override
    public AbstractTextFieldState getState() {
        return (AbstractTextFieldState) super.getState();
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        VTextField textField = getWidget();
        // Save details
        textField.client = client;
        textField.paintableId = uidl.getId();

        if (!isRealUpdate(uidl)) {
            return;
        }

        textField.setReadOnly(isReadOnly());

        textField.setInputPrompt(getState().inputPrompt);
        textField.setMaxLength(getState().maxLength);
        textField.setImmediate(getState().immediate);

        textField.listenTextChangeEvents = hasEventListener("ie");
        if (textField.listenTextChangeEvents) {
            textField.textChangeEventMode = uidl.getStringAttribute(
                    TextFieldConstants.ATTR_TEXTCHANGE_EVENTMODE);
            if (textField.textChangeEventMode
                    .equals(TextFieldConstants.TEXTCHANGE_MODE_EAGER)) {
                textField.textChangeEventTimeout = 1;
            } else {
                textField.textChangeEventTimeout = uidl.getIntAttribute(
                        TextFieldConstants.ATTR_TEXTCHANGE_TIMEOUT);
                if (textField.textChangeEventTimeout < 1) {
                    // Sanitize and allow lazy/timeout with timeout set to 0 to
                    // work as eager
                    textField.textChangeEventTimeout = 1;
                }
            }
            textField.sinkEvents(VTextField.TEXTCHANGE_EVENTS);
            textField.attachCutEventListener(textField.getElement());
        }
        textField.setColumns(getState().columns);

        String text = getState().text;
        if (text == null) {
            text = "";
        }
        /*
         * We skip the text content update if field has been repainted, but text
         * has not been changed (#6588). Additional sanity check verifies there
         * is no change in the queue (in which case we count more on the server
         * side value). <input> is updated only when it looses focus, so we
         * force updating if not focused. Lost focus issue appeared in (#15144)
         */
        if (Util.getFocusedElement() != textField.getElement()
                || !uidl.getBooleanAttribute(
                        TextFieldConstants.ATTR_NO_VALUE_CHANGE_BETWEEN_PAINTS)
                || textField.valueBeforeEdit == null
                || !text.equals(textField.valueBeforeEdit)) {
            textField.updateFieldContent(text);
        }

        if (uidl.hasAttribute("selpos")) {
            final int pos = uidl.getIntAttribute("selpos");
            final int length = uidl.getIntAttribute("sellen");
            /*
             * Gecko defers setting the text so we need to defer the selection.
             */
            Scheduler.get().scheduleDeferred(new Command() {
                @Override
                public void execute() {
                    textField.setSelectionRange(pos, length);
                }
            });
        }
    }

    @Override
    public VTextField getWidget() {
        return (VTextField) super.getWidget();
    }

    @Override
    public void flush() {
        getWidget().valueChange(false);
    }

}
