/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.splitpanel;

import com.google.gwt.core.client.GWT;
import com.vaadin.terminal.gwt.client.ui.Connect;
import com.vaadin.terminal.gwt.client.ui.Connect.LoadStyle;
import com.vaadin.ui.VerticalSplitPanel;

@Connect(value = VerticalSplitPanel.class, loadStyle = LoadStyle.EAGER)
public class VerticalSplitPanelConnector extends AbstractSplitPanelConnector {

    @Override
    public VSplitPanelVertical getWidget() {
        return (VSplitPanelVertical) super.getWidget();
    }

}
