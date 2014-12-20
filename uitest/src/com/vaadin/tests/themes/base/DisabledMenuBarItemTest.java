package com.vaadin.tests.themes.base;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DisabledMenuBarItemTest extends MultiBrowserTest {

    @Test
    public void disabledMenuItemShouldHaveOpacity() {
        openTestURL();

        WebElement element = driver.findElement(By
                .className("v-menubar-menuitem-disabled"));

        assertThat(element.getCssValue("opacity"), is("0.5"));

        if (browserIsIE8or9()) {
            assertThat(element.getCssValue("filter"), is("alpha(opacity=50)"));

        }
    }

    private boolean browserIsIE8or9() {
        return Browser.IE8.getDesiredCapabilities().equals(
                getDesiredCapabilities())
                || Browser.IE9.getDesiredCapabilities().equals(
                        getDesiredCapabilities());
    }
}