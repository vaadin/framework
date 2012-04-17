/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.splitpanel;

import com.google.gwt.core.client.GWT;
import com.vaadin.terminal.gwt.client.ui.Connect;
import com.vaadin.terminal.gwt.client.ui.Connect.LoadStyle;
import com.vaadin.ui.HorizontalSplitPanel;

@Connect(value = HorizontalSplitPanel.class, loadStyle = LoadStyle.EAGER)
public class HorizontalSplitPanelConnector extends AbstractSplitPanelConnector {

    @Override
    protected VAbstractSplitPanel createWidget() {
        return GWT.create(VSplitPanelHorizontal.class);
    }

}
