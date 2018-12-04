package com.vaadin.tests.minitutorials.v7a2;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

/**
 * Mini tutorial code for
 * https://vaadin.com/wiki/-/wiki/Main/Using%20Resources%20
 * in%20the%20shared%20state
 *
 * @author Vaadin Ltd
 * @since 7.0.0
 */
@Widgetset("com.vaadin.tests.widgetset.TestingWidgetSet")
public class ResourceInStateUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        ResourceInStateComponent component = new ResourceInStateComponent();
        component.setMyIcon(new ThemeResource("../runo/icons/32/calendar.png"));

        setContent(component);
    }

}
