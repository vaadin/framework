package com.vaadin.ui;

import com.vaadin.data.Property;

@SuppressWarnings("serial")
public class NativeButton extends Button {

    public NativeButton() {
        super();
    }

    public NativeButton(String caption) {
        super(caption);
    }

    public NativeButton(String caption, ClickListener listener) {
        super(caption, listener);
    }

    public NativeButton(String caption, Object target, String methodName) {
        super(caption, target, methodName);
    }

    /**
     * Creates a new switch button with initial value.
     * 
     * @param state
     *            the Initial state of the switch-button.
     * @param initialState
     */
    public NativeButton(String caption, boolean initialState) {
        super(caption, initialState);
    }

    /**
     * Creates a new switch button that is connected to a boolean property.
     * 
     * @param state
     *            the Initial state of the switch-button.
     * @param dataSource
     */
    public NativeButton(String caption, Property dataSource) {
        super(caption, dataSource);
    }

    @Override
    public String getTag() {
        return "nativebutton";
    }

}