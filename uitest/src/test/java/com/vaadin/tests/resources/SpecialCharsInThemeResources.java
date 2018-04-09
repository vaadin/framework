package com.vaadin.tests.resources;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.tests.tb3.SingleBrowserTest;

public class SpecialCharsInThemeResources extends SingleBrowserTest {

    @Test
    public void loadThemeResource() {
        loadResource("/VAADIN/themes/tests-tickets/ordinary.txt");
        checkSource();
    }

    @Test
    public void loadThemeResourceWithPercentage() {
        loadResource("/VAADIN/themes/tests-tickets/percentagein%2520name.txt");
        checkSource();
    }

    @Test
    public void loadThemeResourceWithSpecialChars() {
        loadResource(
                "/VAADIN/themes/tests-tickets/folder%20with%20space/resource%20with%20special%20$chars@.txt");
        checkSource();
    }

    private void loadResource(String path) {
        getDriver().get(getBaseURL() + path);
    }

    private void checkSource() {
        String source = getDriver().getPageSource();
        assertTrue("Incorrect contents (was: " + source + ")",
                source.contains("Just ordinary contents here"));
    }
}
