package com.vaadin.tests.components.window;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Automatic test for fix for #12943.
 *
 * While testing without the fix, the test failed on both Chrome and PhantomJS.
 *
 * @since
 * @author Vaadin Ltd
 */
public class BottomComponentScrollsUpTest extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();

        openTestURL();
    }

    @Test
    public void windowScrollTest() throws IOException, InterruptedException {
        TestBenchElement panelScrollable = (TestBenchElement) getDriver()
                .findElement(By.className("v-panel-content"));
        Dimension panelScrollableSize = panelScrollable.getSize();

        WebElement verticalLayout = panelScrollable
                .findElement(By.className("v-verticallayout"));
        Dimension verticalLayoutSize = verticalLayout.getSize();

        panelScrollable.scroll(verticalLayoutSize.height);

        WebElement button = verticalLayout
                .findElement(By.className("v-button"));

        button.click();

        // Loose the focus from the button.
        new Actions(getDriver())
                .moveToElement(panelScrollable, panelScrollableSize.width / 2,
                        panelScrollableSize.height / 2)
                .click().build().perform();

        compareScreen("window");
    }
}
