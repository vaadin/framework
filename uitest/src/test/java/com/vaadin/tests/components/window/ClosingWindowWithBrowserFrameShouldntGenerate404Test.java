package com.vaadin.tests.components.window;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ClosingWindowWithBrowserFrameShouldntGenerate404Test
        extends MultiBrowserTest {

    private static boolean contains404(LogEntry logEntry) {
        return logEntry.getMessage().contains("404");
    }

    @Test
    public void openWindowWithFrame_closeWindow_no404() {
        openTestURL();
        $(ButtonElement.class).first().click();

        $(WindowElement.class).first().close();

        $(LabelElement.class).exists();

        List<LogEntry> logs = getDriver().manage().logs().get("browser")
                .getAll();

        Assert.assertTrue(theresNoLogWith404In(logs));
    }

    private boolean theresNoLogWith404In(List<LogEntry> logs) {
        return !logs.stream().anyMatch(
                ClosingWindowWithBrowserFrameShouldntGenerate404Test::contains404);
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // IE and Firefox drivers do not support logging API, see
        // https://github.com/SeleniumHQ/selenium/issues/6414
        // https://github.com/mozilla/geckodriver/issues/284
        return Arrays.asList(Browser.CHROME.getDesiredCapabilities());
    }
}
