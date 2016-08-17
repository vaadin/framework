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

package com.vaadin.client.legacy.ui.textfield;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.Paintable;
import com.vaadin.client.UIDL;
import com.vaadin.client.Util;
import com.vaadin.client.legacy.ui.VLegacyTextField;
import com.vaadin.client.ui.AbstractFieldConnector;
import com.vaadin.legacy.ui.LegacyTextField;
import com.vaadin.shared.legacy.ui.textfield.LegacyAbstractTextFieldState;
import com.vaadin.shared.legacy.ui.textfield.LegacyTextFieldConstants;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

@Deprecated
@Connect(value = LegacyTextField.class, loadStyle = LoadStyle.EAGER)
public class LegacyTextFieldConnector extends AbstractFieldConnector
        implements Paintable {

    @Override
    public LegacyAbstractTextFieldState getState() {
        return (LegacyAbstractTextFieldState) super.getState();
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
            getWidget().textChangeEventMode = uidl.getStringAttribute(
                    LegacyTextFieldConstants.ATTR_TEXTCHANGE_EVENTMODE);
            if (getWidget().textChangeEventMode
                    .equals(LegacyTextFieldConstants.TEXTCHANGE_MODE_EAGER)) {
                getWidget().textChangeEventTimeout = 1;
            } else {
                getWidget().textChangeEventTimeout = uidl.getIntAttribute(
                        LegacyTextFieldConstants.ATTR_TEXTCHANGE_TIMEOUT);
                if (getWidget().textChangeEventTimeout < 1) {
                    // Sanitize and allow lazy/timeout with timeout set to 0 to
                    // work as eager
                    getWidget().textChangeEventTimeout = 1;
                }
            }
            getWidget().sinkEvents(VLegacyTextField.TEXTCHANGE_EVENTS);
            getWidget().attachCutEventListener(getWidget().getElement());
        }
        getWidget().setColumns(getState().columns);

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
        if (!(Util.getFocusedElement() == getWidget().getElement())
                || !uidl.getBooleanAttribute(
                        LegacyTextFieldConstants.ATTR_NO_VALUE_CHANGE_BETWEEN_PAINTS)
                || getWidget().valueBeforeEdit == null
                || !text.equals(getWidget().valueBeforeEdit)) {
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
    public VLegacyTextField getWidget() {
        return (VLegacyTextField) super.getWidget();
    }

    @Override
    public void flush() {
        getWidget().valueChange(false);
    }

}
