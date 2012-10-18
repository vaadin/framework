package com.vaadin.tests.application;

import java.util.Properties;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

public class DeploymentConfigurationTest extends UI {

    @Override
    protected void init(VaadinRequest request) {
        Properties params = getSession().getConfiguration().getInitParameters();

        for (Object key : params.keySet()) {
            addComponent(new Label(key + ": "
                    + params.getProperty((String) key)));
        }
    }
}
