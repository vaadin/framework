/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.user.client.DOM;

/**
 * This class represents a password field.
 * 
 * @author IT Mill Ltd.
 * 
 */
public class IPasswordField extends ITextField {

    public IPasswordField() {
        super(DOM.createInputPassword());
    }

}
