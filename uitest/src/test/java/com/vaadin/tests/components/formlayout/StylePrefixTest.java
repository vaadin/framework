package com.vaadin.tests.components.formlayout;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for FormLayout style prefix: custom additional styles should be prefixed
 * with "v-formlayout-", not "v-layout-".
 *
 * @author Vaadin Ltd
 */
public class StylePrefixTest extends MultiBrowserTest {

    @Test
    public void testStylePrefix() {
        openTestURL();

        assertTrue("Custom style has unexpected prefix",
                isElementPresent(By.className("v-formlayout-mystyle")));
    }

}
