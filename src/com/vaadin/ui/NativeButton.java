/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.ui;

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

}
