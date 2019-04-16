package com.vaadin.tests.components.combobox;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

import static org.junit.Assert.assertTrue;

public class ComboBoxAddNewItemAndResetProviderAtSameRoundTest
        extends SingleBrowserTest {

    protected enum SelectionType {
        ENTER, TAB, CLICK_OUT;
    }

    private ComboBoxElement comboBoxElement;
    private LabelElement resetLabelElement;
    private LabelElement valueLabelElement;
    private String inputValue = "000";

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
        waitForElementPresent(By.className("v-filterselect"));
        comboBoxElement = $(ComboBoxElement.class).first();
        resetLabelElement = $(LabelElement.class).id("reset-label");
        valueLabelElement = $(LabelElement.class).id("value-label");
    }

    @Test
    public void addNewItemAndReset_reAddWithEnter() {
        itemHandling(SelectionType.ENTER, inputValue);
    }

    @Test
    public void addNewItemAndReset_reAddWithTab() {
        itemHandling(SelectionType.TAB, inputValue);
    }

    @Test
    public void addNewItemAndReset_reAddWithClickOut() {
        itemHandling(SelectionType.CLICK_OUT, inputValue);
    }

    @Test
    public void slowAddNewItemAndReset_reAddWithEnter() {
        delay(true);
        itemHandling(SelectionType.ENTER, inputValue);
    }

    @Test
    public void slowAddNewItemAndReset_reAddWithTab() {
        delay(true);
        itemHandling(SelectionType.TAB, inputValue);
    }

    @Test
    public void slowAddNewItemAndReset_reAddWithClickOut() {
        delay(true);
        itemHandling(SelectionType.CLICK_OUT, inputValue);
    }

    private void itemHandling(SelectionType selectionType, String input) {
        assertThatSelectedValueIs("Value Label");
        sendKeysToInput(input);
        assertResetLabelText("Reset Label");

        // reset the dataProvider
        reset();
        assertResetLabelText("Reset");
        assertThatSelectedValueIs("Value is reset");

        // re-add the same value and select
        sendKeysToInput(input);
        performSelect(selectionType);

        assertThatSelectedValueIs(input);
    }

    private void assertResetLabelText(String text) {
        sleep(1000);
        resetLabelElement = $(LabelElement.class).id("reset-label");
        String resetLabel = resetLabelElement.getText();
        assertTrue("Data Provider should have been reset.",
                text.equals(resetLabel));
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

    private void assertThatSelectedValueIs(String value) {
        sleep(1000);
        valueLabelElement = $(LabelElement.class).id("value-label");
        String valueLabel = valueLabelElement.getText();
        assertTrue("Selected combobox item should be " + value + ".",
                value.equals(valueLabel));
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
