/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.orderedlayout;

import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;
import com.vaadin.ui.VerticalLayout;

@Connect(value = VerticalLayout.class, loadStyle = LoadStyle.EAGER)
public class VerticalBoxLayoutConnector extends AbstractBoxLayoutConnector {

    @Override
    public void init() {
        super.init();
        getWidget().setVertical(true);
    }

}
