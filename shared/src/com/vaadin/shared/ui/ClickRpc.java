/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.shared.ui;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.communication.ServerRpc;

public interface ClickRpc extends ServerRpc {
    /**
     * Called when a click event has occurred and there are server side
     * listeners for the event.
     * 
     * @param mouseDetails
     *            Details about the mouse when the event took place
     */
    public void click(MouseEventDetails mouseDetails);
}