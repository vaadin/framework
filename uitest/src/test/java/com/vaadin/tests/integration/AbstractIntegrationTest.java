package com.vaadin.tests.integration;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.UIElement;
import com.vaadin.testbench.parallel.TestNameSuffix;
import com.vaadin.tests.tb3.PrivateTB3Configuration;

/**
 * Base class for integration tests. Integration tests use the
 * {@literal deployment.url} parameter to determine the base deployment url
 * (http://hostname:123)
 *
 * @author Vaadin Ltd
 */
@TestNameSuffix(property = "server-name")
public abstract class AbstractIntegrationTest extends PrivateTB3Configuration {
    @Override
    protected String getBaseURL() {
        String deploymentUrl = System.getProperty("deployment.url");
        if (deploymentUrl == null || deploymentUrl.equals("")) {
            throw new RuntimeException(
                    "Deployment url must be given as deployment.url");
        }
        return deploymentUrl;
    }

    @Override
    protected void openTestURL() {
        super.openTestURL();

        waitForApplication();
    }

    protected void waitForApplication() {
        if (!isElementPresent(UIElement.class)) {
            // Wait for UI element.
            waitForElementPresent(By.vaadin("//com.vaadin.ui.UI"));
        }
    }
}
