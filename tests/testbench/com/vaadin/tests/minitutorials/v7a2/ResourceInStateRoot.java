/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.minitutorials.v7a2;

import com.vaadin.annotations.Widgetset;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.Root;

/**
 * Mini tutorial code for
 * https://vaadin.com/wiki/-/wiki/Main/Using%20Resources%20
 * in%20the%20shared%20state
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
@Widgetset("com.vaadin.tests.widgetset.TestingWidgetSet")
public class ResourceInStateRoot extends Root {

    @Override
    protected void init(WrappedRequest request) {
        ResourceInStateComponent component = new ResourceInStateComponent();
        component.setIcon(new ThemeResource("../runo/icons/32/calendar.png"));

        addComponent(component);
    }

}
