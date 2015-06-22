package com.vaadin.tests.themes.base;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DisabledMenuBarItemTest extends MultiBrowserTest {

    @Test
    public void disabledMenuItemShouldHaveOpacity() throws IOException {
        openTestURL();

        WebElement element = driver.findElement(By
                .className("v-menubar-menuitem-disabled"));

        if (Browser.IE9.getDesiredCapabilities().equals(
                getDesiredCapabilities())) {
            assertThat(element.getCssValue("filter"), is("alpha(opacity=50)"));
        } else if (Browser.IE8.getDesiredCapabilities().equals(
                getDesiredCapabilities())) {
            WebElement icon = element.findElement(By.tagName("img"));
            assertThat(icon.getCssValue("filter"), is("alpha(opacity=50)"));
        } else {
            assertThat(element.getCssValue("opacity"), is("0.5"));
        }

        compareScreen("transparent");
    }
}