/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.ui;

import java.util.Map;

import com.vaadin.data.Property;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.client.ui.VCheckBox;

@ClientWidget(com.vaadin.terminal.gwt.client.ui.VCheckBox.class)
public class CheckBox extends AbstractField {
    /**
     * Creates a new checkbox.
     */
    public CheckBox() {
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
    public CheckBox(String caption, Property dataSource) {
        this(caption);
        setPropertyDataSource(dataSource);
    }

    @Override
    public Class<?> getType() {
        return Boolean.class;
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);

        target.addVariable(this, VCheckBox.VARIABLE_STATE, booleanValue());
    }

    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        super.changeVariables(source, variables);

        if (!isReadOnly() && variables.containsKey(VCheckBox.VARIABLE_STATE)) {
            // Gets the new and old states
            final Boolean newValue = (Boolean) variables
                    .get(VCheckBox.VARIABLE_STATE);
            final Boolean oldValue = (Boolean) getValue();

            // The event is only sent if the switch state is changed
            if (newValue != null && !newValue.equals(oldValue)) {
                setValue(newValue);
            }
        }

        if (variables.containsKey(FocusEvent.EVENT_ID)) {
            fireEvent(new FocusEvent(this));
        }
        if (variables.containsKey(BlurEvent.EVENT_ID)) {
            fireEvent(new BlurEvent(this));
        }
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
     * Get the boolean value of the checkbox state.
     * 
     * @return True iff the checkbox is checked.
     * @deprecated in Vaadin 7.0.0. Retained to ease migration from Vaadin 6
     */
    @Deprecated
    public boolean booleanValue() {
        // FIXME: How should null really be handled? A default converter that
        // converts it to false? The only UI values supported are true and
        // false.
        Boolean value = (Boolean) getValue();
        return (null == value) ? false : value.booleanValue();
    }

}
