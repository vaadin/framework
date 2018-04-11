package com.vaadin.tests.themes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.junit.Assume;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class ThemeChangeFaviconTest extends SingleBrowserTest {

    @Test
    public void changeFavicon() throws InterruptedException {
        Assume.assumeFalse("PhantomJS does not send onload events for styles",
                BrowserUtil.isPhantomJS(getDesiredCapabilities()));

        setDebug(true);
        openTestURL();
        assertFavicon("reindeer");

        changeTheme("valo");
        assertFavicon("valo");

        changeTheme("reindeer");
        assertFavicon("reindeer");
    }

    private void changeTheme(final String theme) {
        $(ButtonElement.class).caption(theme).first().click();
        waitForThemeToChange(theme);
    }

    private void assertFavicon(String theme) {
        String faviconUrl = "/VAADIN/themes/" + theme + "/favicon.ico";

        List<WebElement> elements = findElements(
                By.cssSelector("link[rel~=\"icon\"]"));

        assertEquals(2, elements.size());

        for (WebElement element : elements) {
            assertTrue(
                    element.getAttribute("href") + " does not end with "
                            + faviconUrl,
                    element.getAttribute("href").endsWith(faviconUrl));
        }
    }

}
