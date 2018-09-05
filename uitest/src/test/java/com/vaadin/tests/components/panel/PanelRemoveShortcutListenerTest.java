package com.vaadin.tests.components.panel;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.google.common.base.Objects;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.PanelElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for removing a shortcut listener from Panel.
 *
 * @author Vaadin Ltd
 */
public class PanelRemoveShortcutListenerTest extends MultiBrowserTest {

    private PanelElement panel;

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
        waitForElementPresent(By.className("v-panel"));
        panel = $(PanelElement.class).first();
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        List<DesiredCapabilities> list = super.getBrowsersToTest();
        // For some reason the shortcut isn't working for these browsers when
        // tested through TestBench:
        list.remove(Browser.FIREFOX.getDesiredCapabilities());
        list.remove(Browser.CHROME.getDesiredCapabilities());
        return list;
    }

    @Test
    public void testToggleWithShortcut() {
        assertThat(
                panel.findElement(By.className("v-panel-caption"))
                        .findElement(By.tagName("span")).getText(),
                is("No shortcut effects (press 'A')"));

        attemptShortcut("A on");
        attemptShortcut("A off");
    }

    @Test
    public void testShortcutGetsRemoved() {
        attemptShortcut("A on");

        $(ButtonElement.class).first().click();
        waitForElementPresent(By.className("v-label"));

        attemptShortcut("A on");

        // add a bit more delay to make sure the caption doesn't change later
        sleep(2000);

        assertThat(panel.findElement(By.className("v-panel-caption"))
                .findElement(By.tagName("span")).getText(), is("A on"));
    }

    private void attemptShortcut(final String expectedCaption) {
        $(TextFieldElement.class).first().sendKeys("A");
        waitUntil(new ExpectedCondition<Boolean>() {
            private String actualCaption;

            @Override
            public Boolean apply(WebDriver input) {
                actualCaption = panel
                        .findElement(By.className("v-panel-caption"))
                        .findElement(By.tagName("span")).getText();
                return Objects.equal(actualCaption, expectedCaption);
            }

            @Override
            public String toString() {
                // Timed out after 10 seconds waiting for ...
                return "panel's caption to become " + expectedCaption
                        + " (was: " + actualCaption + ")";
            }
        });
    }
}
