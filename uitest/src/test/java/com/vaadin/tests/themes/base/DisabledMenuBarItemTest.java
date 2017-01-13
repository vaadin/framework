package com.vaadin.tests.themes.base;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DisabledMenuBarItemTest extends MultiBrowserTest {

    @Test
    public void disabledMenuItemShouldHaveOpacity() throws IOException {
        openTestURL();

        WebElement element = driver
                .findElement(By.className("v-menubar-menuitem-disabled"));

        assertThat(element.getCssValue("opacity"), is("0.5"));

        compareScreen("transparent");
    }
}
