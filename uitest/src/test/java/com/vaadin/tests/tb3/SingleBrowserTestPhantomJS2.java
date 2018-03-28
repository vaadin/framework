package com.vaadin.tests.tb3;

import java.util.Collections;
import java.util.List;

import org.openqa.selenium.remote.DesiredCapabilities;

public abstract class SingleBrowserTestPhantomJS2
        extends PrivateTB3Configuration {
    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return Collections.singletonList(PHANTOMJS2());
    }
}
