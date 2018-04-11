package com.vaadin.tests.components.combobox;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openqa.selenium.Keys;

import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ComboBoxResetValueTest extends MultiBrowserTest {

    private ComboBoxElement comboBoxWithNullSelectionItemId;
    private ComboBoxElement comboBoxWithoutNullSelectionItemId;
    private ComboBoxElement comboBoxWithNullNotAllowed;

    @Override
    public void setup() throws Exception {
        super.setup();

        openTestURL();

        comboBoxWithNullSelectionItemId = $(ComboBoxElement.class)
                .id(ComboBoxResetValue.WITH_SET_NULL_SELECTION_ITEM_ID);

        comboBoxWithoutNullSelectionItemId = $(ComboBoxElement.class)
                .id(ComboBoxResetValue.WITHOUT_NULL_SELECTION_ITEM_ID);

        comboBoxWithNullNotAllowed = $(ComboBoxElement.class)
                .id(ComboBoxResetValue.NULL_SELECTION_NOT_ALLOWED);

        clickResetButton();
    }

    @Test
    public void testNullSelectionAllowedAndSetNullSelectionItemId() {
        comboBoxWithNullSelectionItemId.openPopup();

        assertThatNullSelectionItemSelected(comboBoxWithNullSelectionItemId);
    }

    @Test
    public void testFilterNullSelectionAllowedAndSetNullSelectionItemId() {
        comboBoxWithNullSelectionItemId.sendKeys("foo", Keys.TAB);

        assertThatNullSelectionItemSelected(comboBoxWithNullSelectionItemId);
    }

    @Test
    public void testNullSelectionAllowedWithoutNullSelectionItemId() {
        comboBoxWithoutNullSelectionItemId.openPopup();

        assertThatSelectionIsEmpty(comboBoxWithoutNullSelectionItemId);
    }

    @Test
    public void testFilterNullSelectionAllowedWithoutNullSelectionItemId() {
        comboBoxWithoutNullSelectionItemId.sendKeys("foo", Keys.TAB);

        assertThatSelectionIsEmpty(comboBoxWithoutNullSelectionItemId);
    }

    @Test
    public void testNullSelectionNotAllowed() {
        comboBoxWithNullNotAllowed.openPopup();

        assertThatSelectionIsEmpty(comboBoxWithNullNotAllowed);
    }

    @Test
    public void testFilterNullSelectionNotAllowed() {
        comboBoxWithNullNotAllowed.sendKeys("1", Keys.TAB);
        comboBoxWithNullNotAllowed.sendKeys(Keys.BACK_SPACE, Keys.TAB);

        assertThat("Selection changed when it shouldn't have.",
                comboBoxWithNullNotAllowed.getText(), is("1"));
    }

    private void assertThatNullSelectionItemSelected(ComboBoxElement comboBox) {
        assertThat("Null selection item not selected.", comboBox.getText(),
                is(ComboBoxResetValue.EMPTY_VALUE));
    }

    private void assertThatSelectionIsEmpty(ComboBoxElement comboBox) {
        assertThat("Something selected when should be empty.",
                comboBox.getText(), is(""));
    }

    private void clickResetButton() {
        ButtonElement resetButton = $(ButtonElement.class).first();
        resetButton.click();
    }
}
