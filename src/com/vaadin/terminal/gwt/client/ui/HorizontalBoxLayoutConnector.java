/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.vaadin.terminal.gwt.client.ui.Connect.LoadStyle;
import com.vaadin.ui.HorizontalLayout;

@Connect(value = HorizontalLayout.class, loadStyle = LoadStyle.EAGER)
public class HorizontalBoxLayoutConnector extends AbstractBoxLayoutConnector {

    @Override
    public void init() {
        super.init();
        getWidget().setVertical(false);
    }

}
