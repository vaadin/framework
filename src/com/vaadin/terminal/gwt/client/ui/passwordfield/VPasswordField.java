/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui.passwordfield;

import com.google.gwt.user.client.DOM;
import com.vaadin.terminal.gwt.client.ui.textfield.VTextField;

/**
 * This class represents a password field.
 * 
 * @author Vaadin Ltd.
 * 
 */
public class VPasswordField extends VTextField {

    public VPasswordField() {
        super(DOM.createInputPassword());
    }

}
