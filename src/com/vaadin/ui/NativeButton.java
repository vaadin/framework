/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.ui;

import com.vaadin.data.Property;
import com.vaadin.terminal.gwt.client.ui.VNativeButton;

@SuppressWarnings("serial")
@ClientWidget(VNativeButton.class)
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
     * @deprecated use the {@link CheckBox} component instead
     */
    @Deprecated
    public NativeButton(String caption, boolean initialState) {
        super(caption, initialState);
    }

    /**
     * Creates a new switch button that is connected to a boolean property.
     * 
     * @param state
     *            the Initial state of the switch-button.
     * @param dataSource
     * @deprecated use the {@link CheckBox} component instead
     */
    @Deprecated
    public NativeButton(String caption, Property dataSource) {
        super(caption, dataSource);
    }

}