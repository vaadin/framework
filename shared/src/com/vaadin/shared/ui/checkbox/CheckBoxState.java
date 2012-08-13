/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.shared.ui.checkbox;

import com.vaadin.shared.AbstractFieldState;

public class CheckBoxState extends AbstractFieldState {
    private boolean checked = false;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

}