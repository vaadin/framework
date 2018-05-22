package com.vaadin.tests.components.table;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Checks that Table that has required flag set to true is also indicated as
 * such on the client side.
 *
 * @author Vaadin Ltd
 */
public class TableRequiredIndicatorTest extends MultiBrowserTest {

    @Test
    public void testRequiredIndicatorIsVisible() {
        openTestURL();
        Assert.assertTrue(
                isElementPresent(By.className("v-required-field-indicator")));
    }

}
