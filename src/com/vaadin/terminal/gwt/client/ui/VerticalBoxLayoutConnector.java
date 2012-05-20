/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.vaadin.terminal.gwt.client.ui.Connect.LoadStyle;
import com.vaadin.ui.VerticalLayout;

@Connect(value = VerticalLayout.class, loadStyle = LoadStyle.EAGER)
public class VerticalBoxLayoutConnector extends AbstractBoxLayoutConnector {

    @Override
    public void init() {
        super.init();
        getWidget().setVertical(true);
    }

}
