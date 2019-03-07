package com.vaadin.tests.components.combobox;

import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

@Ignore("Tests are not stable at all, locally pass")
public class ComboBoxAddNewItemAndResetProviderAtSameRoundTest
        extends SingleBrowserTest {

    protected enum SelectionType {
        ENTER, TAB, CLICK_OUT;
    }

    private ComboBoxElement comboBoxElement;
    private LabelElement changeLabelElement;
    private String inputValue = "000";

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
        waitForElementPresent(By.className("v-filterselect"));
        comboBoxElement = $(ComboBoxElement.class).first();
        changeLabelElement = $(LabelElement.class).id("change");
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
        assertThatSelectedValueIs("");
        sendKeysToInput(input);

        // reset the dataProvider
        reset();
        assertResetLabelText();
        assertThatSelectedValueIs("");

        // re-add the same value and select
        sendKeysToInput(input);
        performSelect(selectionType);

        assertThatSelectedValueIs(input);
    }

    private void assertResetLabelText() {
        waitUntil(value -> "Reset".equals(changeLabelElement.getText()), 2);
    }

    private void sendKeysToInput(CharSequence... keys) {
        new Actions(getDriver()).moveToElement(comboBoxElement).perform();
        comboBoxElement.sendKeys(keys);
    }

    private void performSelect(SelectionType selectionType) {
        switch (selectionType) {
            case ENTER:
                sendKeysToInput(Keys.RETURN);
                break;
            case TAB:
                sendKeysToInput(Keys.TAB);
                break;
            case CLICK_OUT:
                new Actions(getDriver()).moveToElement(comboBoxElement, 10, 10)
                        .moveByOffset(comboBoxElement.getSize().getWidth(), 0)
                        .click().perform();
                break;
        }
    }

    private void assertThatSelectedValueIs(final String value) {
        waitUntil(input -> value.equals(comboBoxElement.getText()), 2);
    }

    private void delay(boolean delay) {
        CheckBoxElement checkBox = $(CheckBoxElement.class).id("delay");
        if (delay != checkBox.isChecked()) {
            checkBox.click();
        }
    }

    private void reset() {
        $(ButtonElement.class).id("reset").click();
        sleep(200);
    }
}
