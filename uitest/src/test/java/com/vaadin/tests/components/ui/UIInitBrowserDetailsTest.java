package com.vaadin.tests.components.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

import static com.vaadin.tests.components.ui.UIInitBrowserDetails.ACTUAL_MPR_UI_ID_LABEL_ID;
import static com.vaadin.tests.components.ui.UIInitBrowserDetails.EXPECTED_MPR_UI_ID_LABEL_ID;
import static com.vaadin.tests.components.ui.UIInitBrowserDetails.POPULATE_MPR_UI_BUTTON_ID;
import static com.vaadin.tests.components.ui.UIInitBrowserDetails.TRIGGER_MPR_UI_BUTTON_ID;

public class UIInitBrowserDetailsTest extends MultiBrowserTest {

    @Test
    public void testBrowserDetails() throws Exception {
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
        compareRequestAndBrowserValue("v-mui", "mpr ui id", "foo");
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

    @Test
    public void testMprUiIdRequestParameter() {
        openTestURL();
        waitForElementPresent(By.id(POPULATE_MPR_UI_BUTTON_ID));
        $(ButtonElement.class).id(POPULATE_MPR_UI_BUTTON_ID).click();
        waitUntil(driver -> getCommandExecutor().executeScript(
                "return !!window.vaadin.mprUiId;"));
        waitForElementPresent(By.id(EXPECTED_MPR_UI_ID_LABEL_ID));

        $(ButtonElement.class).id(TRIGGER_MPR_UI_BUTTON_ID).click();
        waitForElementPresent(By.id(ACTUAL_MPR_UI_ID_LABEL_ID));

        String expectedMprUiId = $(LabelElement.class).id(
                EXPECTED_MPR_UI_ID_LABEL_ID).getText();
        String actualMprUiId =
                $(LabelElement.class).id(ACTUAL_MPR_UI_ID_LABEL_ID).getText();
        Assert.assertEquals("Unexpected mpr UI id request parameter",
                expectedMprUiId, actualMprUiId);
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
