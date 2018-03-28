package com.vaadin.v7.tests.components.grid.basicfeatures;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.tests.widgetset.client.v7.grid.GridBasicClientFeaturesWidget;
import com.vaadin.tests.widgetset.server.TestWidgetComponent;
import com.vaadin.ui.UI;

/**
 * Initializer shell for GridClientBasicFeatures test application
 *
 * @since
 * @author Vaadin Ltd
 */
@Widgetset(TestingWidgetSet.NAME)
public class GridBasicClientFeatures extends UI {

    @Override
    protected void init(VaadinRequest request) {
        setContent(
                new TestWidgetComponent(GridBasicClientFeaturesWidget.class));
    }

}
