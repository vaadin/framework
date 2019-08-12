package com.vaadin.tests.components.twincolselect;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import com.vaadin.testbench.elements.TwinColSelectElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

import static org.junit.Assert.assertEquals;

public class TwinColSelectCtrlATest extends MultiBrowserTest {

    @Before
    public void setUp() {
        openTestURL();
    }

    @Test
    public void TestSelectionWithCtrlA() {
        getTwinColElement().findElement(By.tagName("select"))
                .sendKeys(Keys.chord(Keys.CONTROL, "a"));
        getTwinColElement().findElements(By.className("v-button")).get(0)
                .click();
        assertEquals(getTwinColElement().getValues().size(), 6);
    }

    private TwinColSelectElement getTwinColElement() {
        return $(TwinColSelectElement.class).first();
    }
}
