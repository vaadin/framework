/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.minitutorials.v7a3;

import com.vaadin.terminal.gwt.client.communication.ClientRpc;

public interface FlotHighlightRpc extends ClientRpc {
    public void highlight(int seriesIndex, int dataIndex);
}
