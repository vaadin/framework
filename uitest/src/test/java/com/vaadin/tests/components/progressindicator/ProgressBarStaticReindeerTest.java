package com.vaadin.tests.components.progressindicator;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.ProgressBarElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ProgressBarStaticReindeerTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return Arrays.asList(Browser.CHROME.getDesiredCapabilities(),
                Browser.FIREFOX.getDesiredCapabilities());
    }
    @Test
    public void compareScreenshot() throws Exception {
        openTestURL();
        compareScreen($(ProgressBarElement.class).first(), "screen");
    }
}
