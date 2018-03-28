package com.vaadin.tests.components.abstractfield;

import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.SingleBrowserTest;

public abstract class ConfigurableAbstractFieldTest extends SingleBrowserTest {

    private static final org.openqa.selenium.By REQUIRED_BY = By
            .className("v-required");
    private static final org.openqa.selenium.By ERROR_INDICATOR_BY = By
            .className("v-errorindicator");

    @Test
    public void requiredIndicator() {
        openTestURL();

        assertNoRequiredIndicator();
        selectMenuPath("Component", "State", "Required");
        assertRequiredIndicator();
        selectMenuPath("Component", "State", "Required");
        assertNoRequiredIndicator();
    }

    @Test
    public void errorIndicator() {
        openTestURL();

        assertNoErrorIndicator();
        selectMenuPath("Component", "State", "Error indicator");
        assertErrorIndicator();
        selectMenuPath("Component", "State", "Error indicator");
        assertNoErrorIndicator();
    }

    private void assertRequiredIndicator() {
        assertElementPresent(REQUIRED_BY);
    }

    private void assertNoRequiredIndicator() {
        assertElementNotPresent(REQUIRED_BY);
    }

    private void assertErrorIndicator() {
        assertElementPresent(ERROR_INDICATOR_BY);
    }

    private void assertNoErrorIndicator() {
        assertElementNotPresent(ERROR_INDICATOR_BY);
    }
}
