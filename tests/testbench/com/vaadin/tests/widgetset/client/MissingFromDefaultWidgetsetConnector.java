/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.tests.widgetset.client;

import com.vaadin.shared.ui.Connect;
import com.vaadin.terminal.gwt.client.ui.AbstractComponentConnector;
import com.vaadin.terminal.gwt.client.ui.label.VLabel;
import com.vaadin.tests.widgetset.server.MissingFromDefaultWidgetsetComponent;

@Connect(MissingFromDefaultWidgetsetComponent.class)
public class MissingFromDefaultWidgetsetConnector extends
        AbstractComponentConnector {
    @Override
    public VLabel getWidget() {
        return (VLabel) super.getWidget();
    }

    @Override
    protected void init() {
        getWidget()
                .setText(
                        "This component is available in TestingWidgetset, but not in DefaultWidgetset");
        super.init();
    }
}
