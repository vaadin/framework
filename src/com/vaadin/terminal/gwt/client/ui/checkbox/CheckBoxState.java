/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.checkbox;

import com.vaadin.terminal.gwt.client.AbstractFieldState;

public class CheckBoxState extends AbstractFieldState {
    private boolean checked = false;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

}