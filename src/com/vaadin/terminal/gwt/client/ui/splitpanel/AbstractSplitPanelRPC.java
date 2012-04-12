/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.splitpanel;

import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.communication.ServerRpc;

public interface AbstractSplitPanelRPC extends ServerRpc {

    /**
     * Called when the position has been updated by the user.
     * 
     * @param position
     *            The new position in % if the current unit is %, in px
     *            otherwise
     */
    public void setSplitterPosition(float position);

    /**
     * Called when a click event has occurred on the splitter.
     * 
     * @param mouseDetails
     *            Details about the mouse when the event took place
     */
    public void splitterClick(MouseEventDetails mouseDetails);

}