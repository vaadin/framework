package com.vaadin.tests.components.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class UIInitBrowserDetailsTest extends MultiBrowserTest {

    @Test
    public void testBrowserDetails() {
        openTestURL();
        /* location */
        compareRequestAndBrowserValue("v-loc", "location", "null");
        /* browser window width */
        compareRequestAndBrowserValue("v-cw", "browser window width", "-1");
        /* browser window height */
        compareRequestAndBrowserValue("v-ch", "browser window height", "-1");
        /* screen width */
        compareRequestAndBrowserValue("v-sw", "screen width", "-1");
        /* screen height */
        compareRequestAndBrowserValue("v-sh", "screen height", "-1");
        /* mpr ui id */
        compareRequestAndBrowserValue("v-mui", "mpr ui id",
                "any-non-empty-value");
        /* timezone offset */
        assertTextNotNull("timezone offset");
        /* raw timezone offset */
        assertTextNotNull("raw timezone offset");
        /* dst saving */
        assertTextNotNull("dst saving");
        /* dst in effect */
        assertTextNotNull("dst in effect");
        /* current date */
        assertTextNotNull("v-curdate");
        assertTextNotNull("current date");
    }

    private void compareRequestAndBrowserValue(String paramName,
            String browserName, String errorValue) {
        assertTextNotEquals(browserName, errorValue);
        assertEquals(
                String.format("Browser and request values differ in '%s',",
                        browserName),
                getLabelText(paramName), getLabelText(browserName));
    }

    private String getLabelText(String id) {
        return $(LabelElement.class).id(id).getText();
    }

    private void assertTextNotNull(String id) {
        assertTextNotEquals(id, "null");
    }

    private void assertTextNotEquals(String id, String notExpected) {
        String actual = getLabelText(id);
        assertNotEquals(String.format("Unexpected value for '%s'", id),
                notExpected, actual);
    }
}
