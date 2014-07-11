/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.applicationservlet;

import com.vaadin.launcher.ApplicationRunnerServlet;
import com.vaadin.launcher.CustomDeploymentConfiguration;
import com.vaadin.launcher.CustomDeploymentConfiguration.Conf;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;

@CustomDeploymentConfiguration({
        @Conf(name = "customParam", value = "customValue"),
        @Conf(name = "resourceCacheTime", value = "3599") })
public class CustomDeploymentConf extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        DeploymentConfiguration deploymentConfiguration = getSession()
                .getService().getDeploymentConfiguration();
        addComponent(new Label("Resource cache time: "
                + deploymentConfiguration.getResourceCacheTime()));
        addComponent(new Label("Custom config param: "
                + deploymentConfiguration.getApplicationOrSystemProperty(
                        "customParam", null)));
    }

    @Override
    protected String getTestDescription() {
        return "Demonstrates the @"
                + CustomDeploymentConfiguration.class.getSimpleName()
                + " feature that allows customizing the effective deployment configuration for test UIs run through "
                + ApplicationRunnerServlet.class.getSimpleName() + ".";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(14215);
    }

}
