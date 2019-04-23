package com.vaadin.tests.components.combobox;

import java.util.logging.Logger;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

import static org.junit.Assert.assertEquals;

public class ComboBoxAddNewItemAndResetProviderAtSameRoundTest
        extends SingleBrowserTest {

    protected enum SelectionType {
        ENTER, TAB, CLICK_OUT;
    }

    private ComboBoxElement comboBoxElement;
    private LabelElement valueLabelElement;
    private String inputValue = "000";

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
        waitForElementPresent(By.className("v-filterselect"));
        waitForElementPresent(By.id("reset-label"));
        waitForElementPresent(By.id("value-label"));
        comboBoxElement = $(ComboBoxElement.class).first();
    }

    /**
     * Scenario: add new item and reset the data provider in the same round,
     * then add the same value again with ENTER
     */
    @Test
    public void addNewItemAndReset_reAddWithEnter() {
        itemHandling(SelectionType.ENTER, inputValue);
    }

    /**
     * Scenario: add new item and reset the data provider in the same round,
     * then add the same value again with TAB
     */
    @Test
    public void addNewItemAndReset_reAddWithTab() {
        itemHandling(SelectionType.TAB, inputValue);
    }

    /**
     * Scenario: add new item and reset the data provider in the same round,
     * then add the same value again with clicking out side of the CB
     */
    @Test
    public void addNewItemAndReset_reAddWithClickOut() {
        itemHandling(SelectionType.CLICK_OUT, inputValue);
    }

    /**
     * Scenario: add new item and reset the data provider in the same round with
     * 2 seconds delay, then add the same value again with ENTER
     */
    @Test
    public void slowAddNewItemAndReset_reAddWithEnter() {
        delay(true);
        itemHandling(SelectionType.ENTER, inputValue);
    }

    /**
     * Scenario: add new item and reset the data provider in the same round with
     * 2 seconds delay, then add the same value again with TAB
     */
    @Test
    public void slowAddNewItemAndReset_reAddWithTab() {
        delay(true);
        itemHandling(SelectionType.TAB, inputValue);
    }

    /**
     * Scenario: add new item and reset the data provider in the same round with
     * 2 seconds delay, then add the same value again with clicking out side
     */
    @Test
    public void slowAddNewItemAndReset_reAddWithClickOut() {
        delay(true);
        itemHandling(SelectionType.CLICK_OUT, inputValue);
    }

    private void itemHandling(SelectionType selectionType, String input) {
        assertValueLabelText("Value Label");
        sendKeysToInput(input);

        // reset the dataProvider
        reset();
        sleep(1000);

        // re-add the same value and select
        sendKeysToInput(input);
        sleep(1000);
        performSelect(selectionType);

        assertLogMessage();
    }

    private void assertLogMessage() {
        Logger.getLogger(
                ComboBoxAddingSameItemTwoTimesWithItemHandlerResetTest.class
                        .getName()).info("!!!!!!!!!!!!!!!!!!!!!!!!" +
                getLogRow(0));
        assertEquals("6. ComboBox value : 000", getLogRow(0));
        assertEquals("5. New item has been added", getLogRow(1));
        assertEquals("4. DataProvider has been reset", getLogRow(2));
        assertEquals("3. ComboBox value : null", getLogRow(3));
        assertEquals("2. ComboBox value : 000", getLogRow(4));
    }

    private void sendKeysToInput(CharSequence... keys) {
        new Actions(getDriver()).moveToElement(comboBoxElement).perform();
        comboBoxElement.sendKeys(keys);
    }

    private void performSelect(SelectionType selectionType) {
        switch (selectionType) {
        case ENTER:
            sendKeysToInput(Keys.ENTER);
            break;
        case TAB:
            sendKeysToInput(Keys.TAB);
            break;
        case CLICK_OUT:
            $(ButtonElement.class).id("button-for-click").click();
            break;
        }
    }

    private void assertValueLabelText(String value) {
        valueLabelElement = $(LabelElement.class).id("value-label");
        waitUntil(driver -> value.equals(valueLabelElement.getText()));
    }

    private void delay(boolean delay) {
        CheckBoxElement checkBox = $(CheckBoxElement.class).id("delay");
        if (delay != checkBox.isChecked()) {
            checkBox.click();
        }
    }

    private void reset() {
        $(ButtonElement.class).id("reset").click();
    }
}
