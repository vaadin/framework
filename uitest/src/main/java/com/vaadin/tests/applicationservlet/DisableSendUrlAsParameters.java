package com.vaadin.tests.applicationservlet;

import com.vaadin.launcher.CustomDeploymentConfiguration;
import com.vaadin.launcher.CustomDeploymentConfiguration.Conf;
import com.vaadin.server.Constants;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;

@CustomDeploymentConfiguration({
        @Conf(name = Constants.SERVLET_PARAMETER_SENDURLSASPARAMETERS, value = "false") })
public class DisableSendUrlAsParameters extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        try {
            log("Init location: " + getPage().getLocation());
        } catch (IllegalStateException e) {
            log("Init location exception: " + e.getMessage());
        }
    }

}
