package com.vaadin.tests.components.embedded;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.EmbeddedElement;
import com.vaadin.testbench.elements.ImageElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.SingleBrowserTest;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Image;

/**
 * Tests that {@link Embedded} uses correct theme when the theme is set with
 * {@link #setTheme(String)}, and also updates correctly if theme is changed
 * later. {@link Image} is used as the baseline for correct behavior.
 *
 * @author Vaadin Ltd
 */
public class EmbeddedThemeResourceTest extends SingleBrowserTest {

    @Before
    @Override
    public void setup() throws Exception {
        Assume.assumeFalse("PhantomJS does not send onload events for styles",
                BrowserUtil.isPhantomJS(getDesiredCapabilities()));

        super.setup();
        openTestURL();
        waitForElementPresent(By.className("v-embedded"));
    }

    @Test
    public void testInitialTheme() {
        EmbeddedElement embedded = $(EmbeddedElement.class).first();
        ImageElement image = $(ImageElement.class).first();
        final String initial = image.getAttribute("src");

        assertFalse(
                "ThemeResource image source uses default theme instead of set theme.",
                initial.contains("/reindeer/"));
        assertThat(
                "Embedded and Image aren't using the same source for the image despite sharing the ThemeResource.",
                embedded.findElement(By.tagName("img")).getAttribute("src"),
                is(initial));
    }

    @Test
    public void testUpdatedTheme() {
        final String initial = $(ImageElement.class).first()
                .getAttribute("src");

        // update theme
        $(ButtonElement.class).first().click();
        waitForThemeToChange("reindeer");

        EmbeddedElement embedded = $(EmbeddedElement.class).first();
        // Re fetch as theme change creates new elements
        final ImageElement image = $(ImageElement.class).first();

        waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                return !initial.equals(image.getAttribute("src"));
            }

            @Override
            public String toString() {
                // Timed out after 10 seconds waiting for ...
                return "image source to be updated (was: " + initial + ")";
            }
        });

        assertTrue("ThemeResource image source didn't update correctly.",
                image.getAttribute("src").contains("/reindeer/"));
        assertThat(
                "Embedded and Image aren't using the same source for the image despite sharing the ThemeResource.",
                embedded.findElement(By.tagName("img")).getAttribute("src"),
                is(image.getAttribute("src")));
    }

}
