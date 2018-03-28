package com.vaadin.tests.components.table;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.commands.TestBenchCommandExecutor;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.screenshot.ImageComparison;
import com.vaadin.testbench.screenshot.ReferenceNameGenerator;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TableScrollAfterAddRowTest extends MultiBrowserTest {

    @Before
    public void init() {
        openTestURL();
    }

    @Test
    public void testJumpToFirstRow() throws InterruptedException {
        jumpToFifteenthRow();
        sleep(300);
        jumpToFirstRow();
        assertEquals("0", getCurrentPageFirstItemIndex());
    }

    @Test
    public void testAddRowAfterJumpToLastRow() throws InterruptedException {
        jumpToLastRow();
        addRow();
        sleep(200);
        assertEquals("85", getCurrentPageFirstItemIndex());
    }

    @Test
    public void testAddRowAfterJumpingToLastRowAndScrollingUp()
            throws InterruptedException {
        jumpToLastRow();
        scrollUp();
        addRow();
        sleep(200);
        Assert.assertNotEquals("86", getCurrentPageFirstItemIndex());
    }

    private void scrollUp() {
        WebElement actualElement = getDriver()
                .findElement(By.className("v-table-body-wrapper"));
        JavascriptExecutor js = new TestBenchCommandExecutor(getDriver(),
                new ImageComparison(), new ReferenceNameGenerator());
        js.executeScript("arguments[0].scrollTop = " + 30, actualElement);
    }

    private String getCurrentPageFirstItemIndex() throws InterruptedException {
        ButtonElement updateLabelButton = $(ButtonElement.class).get(4);
        LabelElement label = $(LabelElement.class).get(1);
        updateLabelButton.click();
        sleep(200);
        return label.getText();
    }

    private void addRow() {
        ButtonElement button = $(ButtonElement.class).get(0);
        button.click();
    }

    private void jumpToFirstRow() {
        ButtonElement button = $(ButtonElement.class).get(3);
        button.click();
    }

    private void jumpToFifteenthRow() {
        ButtonElement button = $(ButtonElement.class).get(2);
        button.click();
    }

    private void jumpToLastRow() {
        ButtonElement button = $(ButtonElement.class).get(1);
        button.click();
    }
}
