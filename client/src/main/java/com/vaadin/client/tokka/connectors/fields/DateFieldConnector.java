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

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.TextBox;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;
import com.vaadin.shared.ui.components.field.DateFieldServerRpc;
import com.vaadin.shared.ui.components.field.DateFieldState;

@Connect(value = com.vaadin.tokka.ui.components.fields.DateField.class, loadStyle = LoadStyle.EAGER)
public class DateFieldConnector extends AbstractComponentConnector {

    @Override
    protected void init() {
        getWidget().addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                String value = getWidget().getValue();
                value = (value == null) ? "" : value.trim();
                // TODO validation

                getRpcProxy(DateFieldServerRpc.class).setDate(value);
            }
        });
    }

    @Override
    public DateFieldState getState() {
        return (DateFieldState) super.getState();
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
        final DateFieldState state = getState();

        w.setReadOnly(state.readOnly);

        w.getElement().setAttribute("placeholder", "DD-MM-YYYY");
        w.getElement().setAttribute("maxlength", "10");
        w.setValue(state.value);

    }
}
