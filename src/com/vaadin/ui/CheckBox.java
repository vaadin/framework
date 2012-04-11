/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import com.vaadin.data.Property;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusAndBlurServerRpcImpl;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.ui.checkbox.CheckBoxServerRpc;
import com.vaadin.terminal.gwt.client.ui.checkbox.CheckBoxState;

public class CheckBox extends AbstractField<Boolean> {

    private CheckBoxServerRpc rpc = new CheckBoxServerRpc() {

        public void setChecked(boolean checked,
                MouseEventDetails mouseEventDetails) {
            if (isReadOnly()) {
                return;
            }

            final Boolean oldValue = getValue();
            final Boolean newValue = checked;

            if (!newValue.equals(oldValue)) {
                // The event is only sent if the switch state is changed
                setValue(newValue);
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

    /**
     * Creates a new checkbox that is connected to a boolean property.
     * 
     * @param state
     *            the Initial state of the switch-button.
     * @param dataSource
     */
    public CheckBox(String caption, Property<?> dataSource) {
        this(caption);
        setPropertyDataSource(dataSource);
    }

    @Override
    public Class<Boolean> getType() {
        return Boolean.class;
    }

    @Override
    public CheckBoxState getState() {
        return (CheckBoxState) super.getState();
    }

    @Override
    protected void setInternalValue(Boolean newValue) {
        super.setInternalValue(newValue);
        if (newValue == null) {
            newValue = false;
        }
        getState().setChecked(newValue);
    }

    public void addListener(BlurListener listener) {
        addListener(BlurEvent.EVENT_ID, BlurEvent.class, listener,
                BlurListener.blurMethod);
    }

    public void removeListener(BlurListener listener) {
        removeListener(BlurEvent.EVENT_ID, BlurEvent.class, listener);
    }

    public void addListener(FocusListener listener) {
        addListener(FocusEvent.EVENT_ID, FocusEvent.class, listener,
                FocusListener.focusMethod);
    }

    public void removeListener(FocusListener listener) {
        removeListener(FocusEvent.EVENT_ID, FocusEvent.class, listener);
    }

    /**
     * Get the boolean value of the button state.
     * 
     * @return True iff the button is pressed down or checked.
     * 
     * @deprecated Use {@link #getValue()} instead and, if needed, handle null
     *             values.
     */
    @Deprecated
    public boolean booleanValue() {
        Boolean value = getValue();
        return (null == value) ? false : value.booleanValue();
    }
}
