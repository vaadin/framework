/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.checkbox;

import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.communication.ServerRpc;

public interface CheckBoxServerRpc extends ServerRpc {
    public void setChecked(boolean checked, MouseEventDetails mouseEventDetails);
}