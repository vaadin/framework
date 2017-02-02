/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.tb3;

import java.util.logging.Logger;

import org.openqa.selenium.Platform;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.DefaultBrowserFactory;
import com.vaadin.testbench.parallel.TestBenchBrowserFactory;

public class VaadinBrowserFactory extends DefaultBrowserFactory {

    TestBenchBrowserFactory delegate = null;

    @Override
    public DesiredCapabilities create(Browser browser) {
        String browserFactoryClass = System.getProperty("browser.factory");
        if (browserFactoryClass != null
                && !browserFactoryClass.trim().isEmpty()) {
            if (delegate == null) {
                getLogger()
                        .info("Using browser factory " + browserFactoryClass);
                try {
                    delegate = (TestBenchBrowserFactory) getClass()
                            .getClassLoader().loadClass(browserFactoryClass)
                            .newInstance();
                } catch (Exception e) {
                    getLogger().warning("Failed to instantiate browser factory "
                            + browserFactoryClass);
                    throw new RuntimeException(e);
                }
            }
            return delegate.create(browser);
        }

        return doCreate(browser);
    }

    public DesiredCapabilities doCreate(Browser browser) {
        switch (browser) {
        case IE11:
            return createIE(browser, "11");
        case PHANTOMJS:
            return create(browser, "1", Platform.LINUX);
        case CHROME:
            return create(browser, "40", Platform.VISTA);
        case FIREFOX:
        default:
            DesiredCapabilities dc = create(Browser.FIREFOX, "45",
                    Platform.WINDOWS);
            dc.setCapability("marionette", "false");
            return dc;
        }
    }

    private DesiredCapabilities createIE(Browser browser, String version) {
        DesiredCapabilities capabilities = create(browser, version,
                Platform.WINDOWS);
        capabilities.setCapability(
                InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
        return capabilities;
    }

    @Override
    public DesiredCapabilities create(Browser browser, String version) {
        DesiredCapabilities capabilities = create(browser);
        capabilities.setVersion(version);
        return capabilities;
    }

    private static final Logger getLogger() {
        return Logger.getLogger(VaadinBrowserFactory.class.getName());
    }
}
