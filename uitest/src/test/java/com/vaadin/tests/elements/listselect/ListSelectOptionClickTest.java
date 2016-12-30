package com.vaadin.tests.elements.listselect;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.ListSelectElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ListSelectOptionClickTest extends MultiBrowserTest {
    ListSelectElement select;
    LabelElement counterLbl;

    @Before
    public void init() {
        openTestURL();
        select = $(ListSelectElement.class).first();
        counterLbl = $(LabelElement.class).id("multiCounterLbl");
    }

    @Test
    @Ignore("depends on framework8-issues/issues/464 fix")
    public void testOptionClick() {
        List<WebElement> options = select.findElements(By.tagName("option"));
        WebElement option = options.get(1);
        option.click();
        checkValueChanged();
    }

    @Test
    @Ignore("depends on framework8-issues/issues/464 fix")
    public void testSelectByText() {
        select.selectByText("item2");
        checkValueChanged();
    }

    @Test
    public void testMultiSelectDeselectByText() {
        select.selectByText("item2");
        Assert.assertEquals("1: [item1, item2]", counterLbl.getText());
        select.selectByText("item3");
        Assert.assertEquals("2: [item1, item2, item3]", counterLbl.getText());
        select.deselectByText("item2");
        Assert.assertEquals("3: [item1, item3]", counterLbl.getText());
    }

    @Test
    public void testDeselectSelectByText() {
        select.deselectByText("item1");
        Assert.assertEquals("1: []", counterLbl.getText());
        select.selectByText("item1");
        Assert.assertEquals("2: [item1]", counterLbl.getText());
        select.selectByText("item3");
        Assert.assertEquals("3: [item1, item3]", counterLbl.getText());
        select.deselectByText("item1");
        Assert.assertEquals("4: [item3]", counterLbl.getText());
    }

    /*
     * Checks that value has changed. Checks that the change event was fired
     * once.
     */
    private void checkValueChanged() {
        String actual = select.getValue();
        String actualCounter = counterLbl.getText();
        Assert.assertEquals("The value of the ListSelect has not changed",
                "item2", actual);
        Assert.assertEquals(
                "The number of list select valueChange events is not one.",
                "1: item2", actualCounter);
    }
}
