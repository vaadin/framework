package com.vaadin.tests.components.checkbox;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class CheckboxContextClickTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersSupportingContextMenu();
    }

    @Test
    public void contextClickCheckboxAndText() {
        openTestURL();
        CheckBoxElement checkbox = $(CheckBoxElement.class).first();
        Assert.assertEquals("checked", checkbox.getValue());
        WebElement input = checkbox.findElement(By.xpath("input"));
        WebElement label = checkbox.findElement(By.xpath("label"));

        new Actions(getDriver()).contextClick(input).perform();
        Assert.assertEquals("1. checkbox context clicked", getLogRow(0));
        Assert.assertEquals("checked", checkbox.getValue());

        new Actions(getDriver()).contextClick(label).perform();
        Assert.assertEquals("2. checkbox context clicked", getLogRow(0));
        Assert.assertEquals("checked", checkbox.getValue());
    }

}
