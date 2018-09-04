package com.vaadin.tests.tb3;

import java.util.Collections;
import java.util.List;

import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.parallel.Browser;

public abstract class SingleBrowserTest extends PrivateTB3Configuration {
    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        if (isRunLocally()) {
            return Collections.singletonList(getRunLocallyCapabilities());
        }
        return Collections
                .singletonList(Browser.CHROME.getDesiredCapabilities());
    }
}
