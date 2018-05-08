package com.vaadin.tests.resources;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class FrontendInitialResourceUITest extends MultiBrowserTest {

    @Test
    public void correctEs5Es6FileImportedThroughFrontend() {
        openTestURL();
        String es;
        if (BrowserUtil.isIE(getDesiredCapabilities())
                || BrowserUtil.isPhantomJS(getDesiredCapabilities())
                || BrowserUtil.isFirefox(getDesiredCapabilities())) {
            es = "es5";
        } else {
            es = "es6";
        }
        if (BrowserUtil.isIE(getDesiredCapabilities())) {
            // For some reason needed by IE11
            testBench().disableWaitForVaadin();
        }

        assertEquals("/VAADIN/frontend/" + es + "/logFilename.js",
                findElement(By.tagName("body")).getText());
    }

}
