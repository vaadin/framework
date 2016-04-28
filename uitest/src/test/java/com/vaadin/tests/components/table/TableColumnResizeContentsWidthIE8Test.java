package com.vaadin.tests.components.table;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TableColumnResizeContentsWidthIE8Test extends MultiBrowserTest {
    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowserCapabilities(Browser.IE8);
    }

    @Override
    protected Class<?> getUIClass() {
        return TableColumnResizeContentsWidth.class;
    }

    @Test
    public void testResizing() throws InterruptedException {
        openTestURL();

        TableElement table = $(TableElement.class).first();
        List<ButtonElement> buttons = $(ButtonElement.class).all();

        WebElement textField = table.findElement(By.className("v-textfield"));

        // click the button for decreasing size
        buttons.get(1).click();

        assertEquals(60, textField.getSize().width);

        // click the button for increasing size
        buttons.get(0).click();

        assertEquals(80, textField.getSize().width);
    }
}
