package com.vaadin.tests.components.combobox;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ComboBoxSelectingNewItemValueChangeTest extends MultiBrowserTest {

    private enum SelectionType {
        ENTER, TAB, CLICK_OUT;
    }

    private ComboBoxElement comboBoxElement;
    private LabelElement valueLabelElement;
    private LabelElement changeLabelElement;
    private String[] defaultInputs = new String[] { "foo", "bar", "baz",
            "fie" };
    private String[] shortInputs = new String[] { "a", "b", "c", "d" };

    @Override
    public void setup() throws Exception {
        super.setup();

        openTestURL();
        waitForElementPresent(By.className("v-filterselect"));
        comboBoxElement = $(ComboBoxElement.class).first();
        valueLabelElement = $(LabelElement.class).id("value");
        changeLabelElement = $(LabelElement.class).id("change");
    }

    @Test
    public void newItemHandlingWithEnter() {
        itemHandling(SelectionType.ENTER, defaultInputs);
    }

    @Test
    public void newItemHandlingWithTab() {
        itemHandling(SelectionType.TAB, defaultInputs);
    }

    @Test
    public void newItemHandlingWithClickingOut() {
        itemHandling(SelectionType.CLICK_OUT, defaultInputs);
    }

    @Test
    public void slowNewItemHandlingWithEnter() {
        delay(true);
        itemHandling(SelectionType.ENTER, defaultInputs);
    }

    @Test
    public void slowNewItemHandlingWithTab() {
        delay(true);
        itemHandling(SelectionType.TAB, defaultInputs);
    }

    @Test
    public void slowNewItemHandlingWithClickingOut() {
        delay(true);
        itemHandling(SelectionType.CLICK_OUT, defaultInputs);
    }

    @Test
    public void shortNewItemHandlingWithEnter() {
        itemHandling(SelectionType.ENTER, shortInputs);
    }

    @Test
    public void shortNewItemHandlingWithTab() {
        itemHandling(SelectionType.TAB, shortInputs);
    }

    @Test
    public void shortNewItemHandlingWithClickingOut() {
        itemHandling(SelectionType.CLICK_OUT, shortInputs);
    }

    public void itemHandling(SelectionType selectionType, String[] inputs) {
        assertThatSelectedValueIs("");

        // new item, no existing selection
        typeInputAndSelect(inputs[0], selectionType);
        assertThatSelectedValueIs(inputs[0]);
        assertValueChange(1);

        // new item, existing selection
        typeInputAndSelect(inputs[1], selectionType);
        assertThatSelectedValueIs(inputs[1]);
        assertValueChange(2);

        reject(true);

        // item adding blocked, existing selection
        typeInputAndSelect(inputs[2], selectionType);
        assertThatSelectedValueIs(inputs[1]);
        assertRejected(inputs[2]);

        reset();

        // item adding blocked, no existing selection
        typeInputAndSelect(inputs[2], selectionType);
        assertThatSelectedValueIs("");
        assertRejected(inputs[2]);

        reject(false);
        blockSelection(true);

        // item adding allowed, selection blocked, no existing selection
        typeInputAndSelect(inputs[2], selectionType);
        assertThatSelectedValueIs("");
        assertItemCount(2601);

        // second attempt selects
        typeInputAndSelect(inputs[2], selectionType);
        assertThatSelectedValueIs(inputs[2]);
        assertValueChange(1);

        // item adding allowed, selection blocked, existing selection
        typeInputAndSelect(inputs[3], selectionType);
        assertThatSelectedValueIs(inputs[2]);
        assertItemCount(2602);
    }

    private void typeInputAndSelect(String input, SelectionType selectionType) {
        comboBoxElement.clear();
        sendKeysToInput(input);
        switch (selectionType) {
        case ENTER:
            sendKeysToInput(getReturn());
            break;
        case TAB:
            sendKeysToInput(Keys.TAB);
            sleep(100);
            break;
        case CLICK_OUT:
            new Actions(getDriver()).moveToElement(comboBoxElement, 10, 10)
                    .moveByOffset(comboBoxElement.getSize().getWidth(), 0)
                    .click().perform();
            break;
        }
    }

    private void sendKeysToInput(CharSequence... keys) {
        // ensure mouse is located over the ComboBox to avoid hover issues
        new Actions(getDriver()).moveToElement(comboBoxElement).perform();
        comboBoxElement.sendKeys(keys);
    }

    private Keys getReturn() {
        if (BrowserUtil.isPhantomJS(getDesiredCapabilities())) {
            return Keys.ENTER;
        } else {
            return Keys.RETURN;
        }
    }

    private void assertThatSelectedValueIs(final String value) {
        waitUntil(new ExpectedCondition<Boolean>() {
            private String actualComboBoxValue;
            private String actualLabelValue;

            @Override
            public Boolean apply(WebDriver input) {
                actualLabelValue = valueLabelElement.getText();
                actualComboBoxValue = comboBoxElement.getText();
                return actualComboBoxValue.equals(value)
                        && actualLabelValue.equals(value);
            }

            @Override
            public String toString() {
                // Timed out after 10 seconds waiting for ...
                return String.format(
                        "combobox and label value to match '%s' (was: '%s' and '%s')",
                        value, actualComboBoxValue, actualLabelValue);
            }
        });
    }

    private void assertValueChange(int count) {
        sleep(100);
        assertEquals(String.format(
                "Value change count: %s Selection change count: %s user originated: true",
                count, count), changeLabelElement.getText());
    }

    private void assertRejected(String value) {
        assertEquals(String.format("item %s discarded", value),
                changeLabelElement.getText());
    }

    private void assertItemCount(int count) {
        sleep(100);
        assertEquals(String.format("adding new item... count: %s", count),
                changeLabelElement.getText());
    }

    private void reject(boolean reject) {
        CheckBoxElement checkBox = $(CheckBoxElement.class).id("reject");
        if (reject != checkBox.isChecked()) {
            checkBox.click();
        }
    }

    private void delay(boolean delay) {
        CheckBoxElement checkBox = $(CheckBoxElement.class).id("delay");
        if (delay != checkBox.isChecked()) {
            checkBox.click();
        }
    }

    private void blockSelection(boolean noSelection) {
        CheckBoxElement checkBox = $(CheckBoxElement.class).id("noSelection");
        if (noSelection != checkBox.isChecked()) {
            checkBox.click();
        }
    }

    private void reset() {
        $(ButtonElement.class).id("reset").click();
    }

}
