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

package com.vaadin.client.tokka.connectors.fields;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.TextBox;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.tokka.ui.components.fields.TextFieldServerRpc;
import com.vaadin.shared.tokka.ui.components.fields.TextFieldState;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;

@Connect(value = com.vaadin.tokka.ui.components.fields.TextField.class, loadStyle = LoadStyle.EAGER)
public class TextFieldConnector extends AbstractComponentConnector {

    @Override
    protected void init() {
        getWidget().addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                getRpcProxy(TextFieldServerRpc.class)
                        .setText(getWidget().getValue());
            }
        });
    }

    @Override
    public TextFieldState getState() {
        return (TextFieldState) super.getState();
    }

    @Override
    public TextBox getWidget() {
        return (TextBox) super.getWidget();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        // TODO Split into separate @OnStateChanged methods
        super.onStateChanged(stateChangeEvent);

        final TextBox w = getWidget();
        final TextFieldState state = getState();

        w.setReadOnly(state.readOnly);

        if (state.placeholder != null) {
            w.getElement().setAttribute("placeholder", state.placeholder);
        } else {
            w.getElement().removeAttribute("placeholder");
        }
        if (state.maxLength >= 0) {
            w.setMaxLength(state.maxLength);
        } else {
            w.getElement().removeAttribute("maxlength");
        }

        w.setValue(state.text);

        if (state.selectionStart != -1) {
            /*
             * Gecko defers setting the text so we need to defer the selection.
             */
            Scheduler.get().scheduleDeferred(new Command() {
                @Override
                public void execute() {
                    w.setSelectionRange(state.selectionStart,
                            state.selectionLength);
                }
            });
        }
    }
}
