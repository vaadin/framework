package com.vaadin.tests.components.window;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.commands.TestBenchElementCommands;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ScrollingBodyElementWithModalOpenedTest extends MultiBrowserTest {

    @Test
    public void testWindowScrollbars() throws Exception {
        openTestURL();

        WebElement bodyElement = driver
                .findElement(By.className("v-modal-window-open"));

        TestBenchElementCommands scrollable = testBenchElement(bodyElement);
        scrollable.scroll(1000);

        Thread.sleep(1000);

        compareScreen(getScreenshotBaseName());
    }

}
