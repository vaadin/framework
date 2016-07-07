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
package com.vaadin.tokka.ui.components.fields;

import java.util.Collection;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

import com.vaadin.event.FieldEvents.FocusAndBlurServerRpcImpl;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.checkbox.CheckBoxServerRpc;
import com.vaadin.shared.ui.checkbox.CheckBoxState;
import com.vaadin.tokka.event.EventListener;
import com.vaadin.tokka.event.Registration;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;

public class CheckBox extends AbstractField<Boolean> {

    public class StateChange extends ValueChange<Boolean> {
        public StateChange(boolean userOriginated) {
            super(CheckBox.this, userOriginated);
        }
    }

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

            setValue(checked, true);
        }
    };

    FocusAndBlurServerRpcImpl focusBlurRpc = new FocusAndBlurServerRpcImpl(
            this) {
        @Override
        protected void fireEvent(LegacyEvent event) {
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
     * Creates a new {@code CheckBox} with the given caption.
     * 
     * @param caption
     *            the check box caption
     */
    public CheckBox(String caption) {
        this();
        setCaption(caption);
    }

    /**
     * Creates a new {@code CheckBox} with the given caption and initial state.
     * 
     * @param caption
     *            the caption of the check box
     * @param initialState
     *            the initial state of the check box
     */
    public CheckBox(String caption, boolean initialState) {
        this(caption);
        setValue(initialState);
    }

    @Override
    public Registration addValueChangeListener(
            EventListener<ValueChange<Boolean>> listener) {
        return addListener(StateChange.class, listener);
    }

    @Override
    public Boolean getValue() {
        return getState(false).checked;
    }

    @Override
    public void readDesign(Element design, DesignContext designContext) {
        super.readDesign(design, designContext);
        if (design.hasAttr("checked")) {
            this.setValue(DesignAttributeHandler.readAttribute("checked",
                    design.attributes(), Boolean.class), false);
        }
    }

    @Override
    public void writeDesign(Element design, DesignContext designContext) {
        super.writeDesign(design, designContext);
        CheckBox def = (CheckBox) designContext.getDefaultInstance(this);
        Attributes attr = design.attributes();
        DesignAttributeHandler.writeAttribute("checked", attr, getValue(),
                def.getValue(), Boolean.class);
    }

    @Override
    protected Collection<String> getCustomAttributes() {
        Collection<String> attributes = super.getCustomAttributes();
        attributes.add("checked");
        return attributes;
    }

    @Override
    protected CheckBoxState getState() {
        return (CheckBoxState) super.getState();
    }

    @Override
    protected CheckBoxState getState(boolean markAsDirty) {
        return (CheckBoxState) super.getState(markAsDirty);
    }

    @Override
    protected void doSetValue(Boolean value) {
        getState().checked = value;
    }

    @Override
    protected StateChange createValueChange(boolean userOriginated) {
        return new StateChange(userOriginated);
    }
}
