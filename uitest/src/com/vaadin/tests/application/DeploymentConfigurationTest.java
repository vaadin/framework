package com.vaadin.tests.application;

import java.util.Properties;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class DeploymentConfigurationTest extends UI {

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        Properties params = getSession().getConfiguration().getInitParameters();

        for (Object key : params.keySet()) {
            layout.addComponent(new Label(key + ": "
                    + params.getProperty((String) key)));
        }
    }
}
