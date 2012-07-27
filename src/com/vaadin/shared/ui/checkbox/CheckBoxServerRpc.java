/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.shared.ui.checkbox;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.communication.ServerRpc;

public interface CheckBoxServerRpc extends ServerRpc {
    public void setChecked(boolean checked, MouseEventDetails mouseEventDetails);
}