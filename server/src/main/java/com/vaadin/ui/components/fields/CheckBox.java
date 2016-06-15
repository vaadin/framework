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
package com.vaadin.ui.components.fields;

import java.util.LinkedHashSet;
import java.util.Set;

import com.vaadin.event.FieldEvents.FocusAndBlurServerRpcImpl;
import com.vaadin.event.handler.Handler;
import com.vaadin.event.handler.Registration;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.checkbox.CheckBoxServerRpc;
import com.vaadin.shared.ui.checkbox.CheckBoxState;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.components.HasValue;

public class CheckBox extends AbstractComponent implements HasValue<Boolean> {

    private final LinkedHashSet<Handler<Boolean>> handlers = new LinkedHashSet<>();

    private CheckBoxServerRpc rpc = new CheckBoxServerRpc() {

        @Override
        public void setChecked(boolean checked,
                MouseEventDetails mouseEventDetails) {

            if (isReadOnly()) {
                return;
            }

            /*
             * Client side updates the state before sending the event so we need
             * to make sure the cached state is updated to match the client. If
             * we do not do this, a reverting setValue() call in a listener will
             * not cause the new state to be sent to the client.
             * 
             * See #11028, #10030.
             */
            getUI().getConnectorTracker().getDiffState(CheckBox.this)
                    .put("checked", checked);

            final Boolean oldValue = getValue();
            final Boolean newValue = checked;

            if (!newValue.equals(oldValue)) {
                // The event is only sent if the switch state is changed
                setValue(newValue, true);
            }

        }
    };

    FocusAndBlurServerRpcImpl focusBlurRpc = new FocusAndBlurServerRpcImpl(this) {
        @Override
        protected void fireEvent(Event event) {
            CheckBox.this.fireEvent(event);
        }
    };

    /**
     * Creates a new checkbox.
     */
    public CheckBox() {
        registerRpc(rpc);
        registerRpc(focusBlurRpc);
        setValue(Boolean.FALSE);
    }

    /**
     * Creates a new checkbox with a set caption.
     * 
     * @param caption
     *            the Checkbox caption.
     */
    public CheckBox(String caption) {
        this();
        setCaption(caption);
    }

    /**
     * Creates a new checkbox with a caption and a set initial state.
     * 
     * @param caption
     *            the caption of the checkbox
     * @param initialState
     *            the initial state of the checkbox
     */
    public CheckBox(String caption, boolean initialState) {
        this(caption);
        setValue(initialState);
    }

    @Override
    public void setValue(Boolean value) {
        setValue(value, false);
    }

    protected void setValue(Boolean value, Boolean userOriginated) {
        getState().checked = value;

        if (!userOriginated)
            markAsDirty();

        if (!handlers.isEmpty()) {
            // name conflict
            com.vaadin.event.handler.Event<Boolean> event = new com.vaadin.event.handler.Event<Boolean>(
                    this, new Boolean(value), userOriginated);
            Set<Handler<Boolean>> copy = new LinkedHashSet<>(handlers);
            for (Handler<Boolean> handler : copy) {
                handler.handleEvent(event);
            }
        }
    }

    @Override
    public Registration onChange(Handler<Boolean> handler) {
        handlers.add(handler);
        return () -> handlers.remove(handler);
    }

    @Override
    public Boolean getValue() {
        return getState().checked;
    }

    @Override
    protected CheckBoxState getState() {
        return (CheckBoxState) super.getState();
    }

}
