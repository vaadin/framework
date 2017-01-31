/*
 * Copyright 2000-2016 Vaadin Ltd.
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

package com.vaadin.ui;

import java.util.Collection;
import java.util.Objects;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusAndBlurServerRpcDecorator;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.Registration;
import com.vaadin.shared.ui.checkbox.CheckBoxServerRpc;
import com.vaadin.shared.ui.checkbox.CheckBoxState;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;

public class CheckBox extends AbstractField<Boolean>
        implements FieldEvents.BlurNotifier, FieldEvents.FocusNotifier {

    private CheckBoxServerRpc rpc = (boolean checked,
            MouseEventDetails mouseEventDetails) -> {
        if (isReadOnly()) {
            return;
        }

        /*
         * Client side updates the state before sending the event so we need to
         * make sure the cached state is updated to match the client. If we do
         * not do this, a reverting setValue() call in a listener will not cause
         * the new state to be sent to the client.
         *
         * See #11028, #10030.
         */
        getUI().getConnectorTracker().getDiffState(CheckBox.this).put("checked",
                checked);

        final Boolean oldValue = getValue();
        final Boolean newValue = checked;

        if (!newValue.equals(oldValue)) {
            // The event is only sent if the switch state is changed
            setValue(newValue, true);
        }
    };

    /**
     * Creates a new checkbox.
     */
    public CheckBox() {
        registerRpc(rpc);
        registerRpc(new FocusAndBlurServerRpcDecorator(this, this::fireEvent));
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
    public Boolean getValue() {
        return getState(false).checked;
    }

    /**
     * Sets the value of this CheckBox. If the new value is not equal to
     * {@code getValue()}, fires a {@link ValueChangeEvent}. Throws
     * {@code NullPointerException} if the value is null.
     *
     * @param value
     *            the new value, not {@code null}
     * @throws NullPointerException
     *             if {@code value} is {@code null}
     */
    @Override
    public void setValue(Boolean value) {
        Objects.requireNonNull(value, "CheckBox value must not be null");
        super.setValue(value);
    }

    /**
     * Sets the value of this CheckBox. If the new value is not equal to
     * {@code getValue()}, fires a {@link ValueChangeEvent}. Throws
     * {@code NullPointerException} if the value is null.
     *
     * @param value
     *            the new value, not {@code null}
     * @param userOriginated
     *            {@code true} if this event originates from the client,
     *            {@code false} otherwise.
     * @throws NullPointerException
     *             if {@code value} is {@code null}
     */
    @Override
    protected boolean setValue(Boolean value, boolean userOriginated) {
        Objects.requireNonNull(value, "CheckBox value must not be null");
        return super.setValue(value, userOriginated);
    }

    @Override
    public Boolean getEmptyValue() {
        return false;
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
    public Registration addBlurListener(BlurListener listener) {
        return addListener(BlurEvent.EVENT_ID, BlurEvent.class, listener,
                BlurListener.blurMethod);
    }

    @Override
    public Registration addFocusListener(FocusListener listener) {
        return addListener(FocusEvent.EVENT_ID, FocusEvent.class, listener,
                FocusListener.focusMethod);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.AbstractField#readDesign(org.jsoup.nodes.Element,
     * com.vaadin.ui.declarative.DesignContext)
     */
    @Override
    public void readDesign(Element design, DesignContext designContext) {
        super.readDesign(design, designContext);
        if (design.hasAttr("checked")) {
            this.setValue(DesignAttributeHandler.readAttribute("checked",
                    design.attributes(), Boolean.class), false);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.AbstractField#getCustomAttributes()
     */
    @Override
    protected Collection<String> getCustomAttributes() {
        Collection<String> attributes = super.getCustomAttributes();
        attributes.add("checked");
        return attributes;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.ui.AbstractField#writeDesign(org.jsoup.nodes.Element,
     * com.vaadin.ui.declarative.DesignContext)
     */
    @Override
    public void writeDesign(Element design, DesignContext designContext) {
        super.writeDesign(design, designContext);
        CheckBox def = designContext.getDefaultInstance(this);
        Attributes attr = design.attributes();
        DesignAttributeHandler.writeAttribute("checked", attr, getValue(),
                def.getValue(), Boolean.class, designContext);
    }

}
