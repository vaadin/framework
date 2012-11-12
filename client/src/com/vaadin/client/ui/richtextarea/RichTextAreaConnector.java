/*
 * Copyright 2011 Vaadin Ltd.
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
package com.vaadin.client.ui.richtextarea;

import com.google.gwt.user.client.Event;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.Paintable;
import com.vaadin.client.UIDL;
import com.vaadin.client.ui.AbstractFieldConnector;
import com.vaadin.client.ui.VRichTextArea;
import com.vaadin.client.ui.ShortcutActionHandler.BeforeShortcutActionListener;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;
import com.vaadin.ui.RichTextArea;

@Connect(value = RichTextArea.class, loadStyle = LoadStyle.LAZY)
public class RichTextAreaConnector extends AbstractFieldConnector implements
        Paintable, BeforeShortcutActionListener {

    @Override
    public void updateFromUIDL(final UIDL uidl, ApplicationConnection client) {
        getWidget().client = client;
        getWidget().id = uidl.getId();

        if (uidl.hasVariable("text")) {
            getWidget().currentValue = uidl.getStringVariable("text");
            if (getWidget().rta.isAttached()) {
                getWidget().rta.setHTML(getWidget().currentValue);
            } else {
                getWidget().html.setHTML(getWidget().currentValue);
            }
        }
        if (isRealUpdate(uidl)) {
            getWidget().setEnabled(isEnabled());
        }

        if (!isRealUpdate(uidl)) {
            return;
        }

        getWidget().setReadOnly(isReadOnly());
        getWidget().immediate = getState().immediate;
        int newMaxLength = uidl.hasAttribute("maxLength") ? uidl
                .getIntAttribute("maxLength") : -1;
        if (newMaxLength >= 0) {
            if (getWidget().maxLength == -1) {
                getWidget().keyPressHandler = getWidget().rta
                        .addKeyPressHandler(getWidget());
            }
            getWidget().maxLength = newMaxLength;
        } else if (getWidget().maxLength != -1) {
            getWidget().getElement().setAttribute("maxlength", "");
            getWidget().maxLength = -1;
            getWidget().keyPressHandler.removeHandler();
        }

        if (uidl.hasAttribute("selectAll")) {
            getWidget().selectAll();
        }

    }

    @Override
    public void onBeforeShortcutAction(Event e) {
        getWidget().synchronizeContentToServer();
    }

    @Override
    public VRichTextArea getWidget() {
        return (VRichTextArea) super.getWidget();
    };

}
