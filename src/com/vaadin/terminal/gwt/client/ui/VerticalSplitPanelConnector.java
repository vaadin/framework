/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.vaadin.terminal.gwt.client.ui.Component.LoadStyle;
import com.vaadin.ui.VerticalSplitPanel;

@Component(value = VerticalSplitPanel.class, loadStyle = LoadStyle.EAGER)
public class VerticalSplitPanelConnector extends AbstractSplitPanelConnector {

    @Override
    protected VAbstractSplitPanel createWidget() {
        return GWT.create(VSplitPanelVertical.class);
    }

}
