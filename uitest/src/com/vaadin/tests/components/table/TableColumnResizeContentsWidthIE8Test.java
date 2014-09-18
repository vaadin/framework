package com.vaadin.tests.components.table;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TableColumnResizeContentsWidthIE8Test extends MultiBrowserTest {
    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        List<DesiredCapabilities> browsersToTest = new ArrayList<DesiredCapabilities>();

        browsersToTest.add(Browser.IE8.getDesiredCapabilities());

        return browsersToTest;
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
