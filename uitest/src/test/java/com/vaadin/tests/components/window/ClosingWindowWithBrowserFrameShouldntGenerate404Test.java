package com.vaadin.tests.components.window;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.logging.LogEntry;

import java.util.List;

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
        waitUntilLoadingIndicatorVisible();

        $(LabelElement.class).exists();

        List<LogEntry> logs = getDriver().manage().logs().get("browser")
                .getAll();

        Assert.assertTrue(theresNoLogWith404In(logs));
    }

    private boolean theresNoLogWith404In(List<LogEntry> logs) {
        return !logs.stream().anyMatch(
                ClosingWindowWithBrowserFrameShouldntGenerate404Test::contains404);
    }
}
