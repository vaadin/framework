package com.vaadin.tests.application;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.launcher.CustomDeploymentConfiguration;
import com.vaadin.launcher.CustomDeploymentConfiguration.Conf;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Label;

@PreserveOnRefresh
@CustomDeploymentConfiguration({
        @Conf(name = "heartbeatInterval", value = "5") })
public class PreserveWithExpiredHeartbeat extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Label label = new Label("UI with id " + getUIId() + " in session "
                + getSession().getSession().getId());
        label.setId("idLabel");
        addComponent(label);
    }
}
