package com.vaadin.tests.components.combobox;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ComboBoxSelectingWithNewItemsAllowedTest extends MultiBrowserTest {
    private ComboBoxElement comboBoxElement;
    private LabelElement labelElement;

    @Override
    public void setup() throws Exception {
        super.setup();

        openTestURL();
        waitForElementPresent(By.className("v-filterselect"));
        comboBoxElement = $(ComboBoxElement.class).first();
        labelElement = $(LabelElement.class).id("count");
    }

    @Test
    public void checkDefaults() {
        assertInitialItemCount();
    }

    @Test
    public void itemIsAddedWithEnter() {
        typeInputAndHitEnter("a");

        assertOneMoreThanInitial();
        assertThatSelectedValueIs("a");
    }

    @Test
    public void itemIsAddedWithTab() {
        typeInputAndHitTab("a");

        assertOneMoreThanInitial();
        assertThatSelectedValueIs("a");
    }

    @Test
    public void itemIsAddedWithClickOut() {
        typeInputAndClickOut("a");

        assertOneMoreThanInitial();
        assertThatSelectedValueIs("a");
    }

    @Test
    public void matchingSuggestionIsSelectedWithEnter() {
        typeInputAndHitEnter("a0");

        assertInitialItemCount();
        assertThatSelectedValueIs("a0");
    }

    @Test
    public void matchingSuggestionIsSelectedWithTab() {
        typeInputAndHitTab("a0");

        assertInitialItemCount();
        assertThatSelectedValueIs("a0");
    }

    @Test
    public void nullIsSelected() {
        typeInputAndHitEnter("a");
        assertOneMoreThanInitial();
        assertThatSelectedValueIs("a");

        clearInputAndHitEnter();

        assertOneMoreThanInitial();
        assertThatSelectedValueIs("", "null");
    }

    @Test
    public void itemFromSecondPageIsSelected() {
        typeInputAndHitEnter("a20");

        assertInitialItemCount();
        assertThatSelectedValueIs("a20");
    }

    @Test
    public void selectingNullFromSecondPage() {
        typeInputAndHitEnter("a20");
        assertInitialItemCount();
        assertThatSelectedValueIs("a20");

        clearInputAndHitEnter();
        assertInitialItemCount();
        assertThatSelectedValueIs("", "null");
    }

    @Test
    public void selectionRemainsAfterOpeningPopup() {
        typeInputAndHitEnter("a20");
        assertInitialItemCount();
        assertThatSelectedValueIs("a20");

        openPopup();
        assertThatSelectedValueIs("a20");
    }

    @Test
    public void selectionOnMouseOut() {
        typeInputAndHitEnter("a20");
        comboBoxElement.sendKeys(Keys.ARROW_DOWN, Keys.ARROW_DOWN);

        findElement(By.className("v-app")).click();

        assertInitialItemCount();
        assertThatSelectedValueIs("", "null");
    }

    @Test
    public void cancelResetsSelection() {
        sendKeysToInput("a20");
        cancelSelection();

        assertInitialItemCount();
        assertThatSelectedValueIs("");
    }

    @Test
    public void inputFieldResetsToSelectedText() {
        typeInputAndHitEnter("z5");

        sendKeysToInput(Keys.BACK_SPACE, Keys.BACK_SPACE);
        cancelSelection();

        assertInitialItemCount();
        assertThatSelectedValueIs("z5");
    }

    @Test
    public void emptyValueIsSelectedWithTab() {
        typeInputAndHitEnter("z5");

        assertInitialItemCount();
        assertThatSelectedValueIs("z5");
        // longer delay for this one because otherwise it keeps failing when run
        // on local machine
        comboBoxElement.sendKeys(200, Keys.BACK_SPACE, Keys.BACK_SPACE,
                Keys.TAB);
        assertInitialItemCount();
        assertThatSelectedValueIs("", "null");

        sendKeysToInput("z5");
        cancelSelection();
        assertInitialItemCount();
        assertThatSelectedValueIs("", "null");
    }

    @Test
    public void arrowNavigatedValueIsSelectedWithEnter() {
        sendKeysToInput("z");
        sendKeysToInput(Keys.DOWN, Keys.DOWN, getReturn());

        assertInitialItemCount();
        assertThatSelectedValueIs("z1");
    }

    @Test
    public void arrowNavigatedValueIsSelectedWithTab() {
        sendKeysToInput("z");
        sendKeysToInput(Keys.DOWN, Keys.DOWN, Keys.TAB);

        assertInitialItemCount();
        assertThatSelectedValueIs("z1");
    }

    private void clearInputAndHitEnter() {
        sendKeysToInput(Keys.BACK_SPACE, Keys.BACK_SPACE, Keys.BACK_SPACE);
        sendKeysToInput(getReturn());
    }

    private void typeInputAndHitEnter(String input) {
        clearInputAndType(input);
        sendKeysToInput(getReturn());
    }

    private void typeInputAndHitTab(String input) {
        clearInputAndType(input);
        sendKeysToInput(Keys.TAB);
    }

    private void typeInputAndClickOut(String input) {
        clearInputAndType(input);
        new Actions(getDriver()).moveToElement(comboBoxElement, 10, 10)
                .moveByOffset(comboBoxElement.getSize().getWidth(), 0).click()
                .perform();
    }

    private void clearInputAndType(String input) {
        comboBoxElement.clear();
        sendKeysToInput(input);
    }

    private void sendKeysToInput(CharSequence... keys) {
        comboBoxElement.sendKeys(keys);
    }

    private Keys getReturn() {
        if (BrowserUtil.isPhantomJS(getDesiredCapabilities())) {
            return Keys.ENTER;
        } else {
            return Keys.RETURN;
        }
    }

    private void openPopup() {
        // Need to wait to make sure popup is closed first.
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        comboBoxElement.openPopup();
    }

    private void cancelSelection() {
        sendKeysToInput(Keys.ESCAPE);
    }

    private void assertThatSelectedValueIs(final String value) {
        assertThatSelectedValueIs(value, value);
    }

    private void assertThatSelectedValueIs(final String value,
            final String labelValue) {
        assertThat(comboBoxElement.getText(), is(value));

        waitUntil(new ExpectedCondition<Boolean>() {
            private String actualValue;

            @Override
            public Boolean apply(WebDriver input) {
                actualValue = $(LabelElement.class).id("value").getText();
                return actualValue.equals(labelValue);
            }

            @Override
            public String toString() {
                // Timed out after 10 seconds waiting for ...
                return String.format("label value to match '%s' (was: '%s')",
                        labelValue, actualValue);
            }
        });
    }

    private void assertInitialItemCount() {
        // wait for a bit in case the count is updating
        sleep(1000);
        assertThat("Wrong initial item count.", labelElement.getText(),
                is("2600"));
    }

    private void assertOneMoreThanInitial() {
        waitUntil(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver input) {
                return "2601".equals(labelElement.getText());
            }

            @Override
            public String toString() {
                // Timed out after 10 seconds waiting for ...
                return String.format("item count to become 2601 (was: %s)",
                        labelElement.getText());
            }
        });
    }
}
