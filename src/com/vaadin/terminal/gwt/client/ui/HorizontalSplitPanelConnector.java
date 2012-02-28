/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;

public class HorizontalSplitPanelConnector extends AbstractSplitPanelConnector {

    @Override
    protected VAbstractSplitPanel createWidget() {
        return GWT.create(VSplitPanelHorizontal.class);
    }

}
