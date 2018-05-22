package com.vaadin.tests.components.combobox;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.commands.TestBenchElementCommands;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ComboBoxNoTextInputTest extends MultiBrowserTest {

    @Test
    public void testComboBoxNoTextInputPopupOpensOnClick() throws Exception {
        openTestURL();

        // deactivate text input
        click($(CheckBoxElement.class).id("textInput"));

        // click and check that popup appears
        ComboBoxElement cb = $(ComboBoxElement.class).first();
        click(cb);
        // popup is opened lazily
        waitForElementPresent(By.vaadin("//com.vaadin.ui.ComboBox[0]#popup"));
    }

    @Test
    public void testComboBoxWithTextInputNoPopupOpensOnClick()
            throws Exception {
        openTestURL();

        // click and check that no popup appears
        ComboBoxElement cb = $(ComboBoxElement.class).first();
        click(cb);
        // popup is opened lazily
        sleep(1000);
        Assert.assertFalse(cb.isElementPresent(By.vaadin("#popup")));
    }

    private void click(ComboBoxElement cb) throws Exception {
        WebElement element = cb.findElement(By.vaadin("#textbox"));
        ((TestBenchElementCommands) element).click(8, 7);
    }

}
