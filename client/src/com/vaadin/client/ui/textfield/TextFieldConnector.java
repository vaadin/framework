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

package com.vaadin.client.ui.textfield;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.Paintable;
import com.vaadin.client.UIDL;
import com.vaadin.client.ui.AbstractFieldConnector;
import com.vaadin.client.ui.ShortcutActionHandler.BeforeShortcutActionListener;
import com.vaadin.client.ui.VTextField;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;
import com.vaadin.shared.ui.textfield.AbstractTextFieldState;
import com.vaadin.shared.ui.textfield.TextFieldConstants;
import com.vaadin.ui.TextField;

@Connect(value = TextField.class, loadStyle = LoadStyle.EAGER)
public class TextFieldConnector extends AbstractFieldConnector implements
        Paintable, BeforeShortcutActionListener {

    @Override
    public AbstractTextFieldState getState() {
        return (AbstractTextFieldState) super.getState();
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // Save details
        getWidget().client = client;
        getWidget().paintableId = uidl.getId();

        if (!isRealUpdate(uidl)) {
            return;
        }

        getWidget().setReadOnly(isReadOnly());

        getWidget().setInputPrompt(getState().inputPrompt);
        getWidget().setMaxLength(getState().maxLength);
        getWidget().setImmediate(getState().immediate);

        getWidget().listenTextChangeEvents = hasEventListener("ie");
        if (getWidget().listenTextChangeEvents) {
            getWidget().textChangeEventMode = uidl
                    .getStringAttribute(TextFieldConstants.ATTR_TEXTCHANGE_EVENTMODE);
            if (getWidget().textChangeEventMode
                    .equals(TextFieldConstants.TEXTCHANGE_MODE_EAGER)) {
                getWidget().textChangeEventTimeout = 1;
            } else {
                getWidget().textChangeEventTimeout = uidl
                        .getIntAttribute(TextFieldConstants.ATTR_TEXTCHANGE_TIMEOUT);
                if (getWidget().textChangeEventTimeout < 1) {
                    // Sanitize and allow lazy/timeout with timeout set to 0 to
                    // work as eager
                    getWidget().textChangeEventTimeout = 1;
                }
            }
            getWidget().sinkEvents(VTextField.TEXTCHANGE_EVENTS);
            getWidget().attachCutEventListener(getWidget().getElement());
        }
        getWidget().setColumns(getState().columns);

        String text = getState().text;
        if (text == null) {
            text = "";
        }
        /*
         * We skip the text content update if field has been repainted, but text
         * has not been changed. Additional sanity check verifies there is no
         * change in the que (in which case we count more on the server side
         * value).
         */
        if (!(uidl
                .getBooleanAttribute(TextFieldConstants.ATTR_NO_VALUE_CHANGE_BETWEEN_PAINTS)
                && getWidget().valueBeforeEdit != null && text
                    .equals(getWidget().valueBeforeEdit))) {
            getWidget().updateFieldContent(text);
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
                    getWidget().setSelectionRange(pos, length);
                }
            });
        }
    }

    @Override
    public VTextField getWidget() {
        return (VTextField) super.getWidget();
    }

    @Override
    public void onBeforeShortcutAction(Event e) {
        flush();
    }

    @Override
    public void flush() {
        getWidget().valueChange(false);
    }

}
