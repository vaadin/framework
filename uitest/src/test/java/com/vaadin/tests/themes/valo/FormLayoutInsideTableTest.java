package com.vaadin.tests.themes.valo;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class FormLayoutInsideTableTest extends MultiBrowserTest {

    @Test
    public void nestedItemHasBorderTop() {
        openTestURL();

        List<WebElement> formLayoutRows = findElements(
                By.cssSelector("tr.v-formlayout-row"));
        WebElement secondNestedRow = formLayoutRows.get(1);

        WebElement td = secondNestedRow.findElement(By.tagName("td"));

        assertEquals("1px", td.getCssValue("border-top-width"));
    }
}
