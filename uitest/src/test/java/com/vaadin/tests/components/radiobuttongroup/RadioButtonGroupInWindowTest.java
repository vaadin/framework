package com.vaadin.tests.components.radiobuttongroup;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.RadioButtonGroupElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;

public class RadioButtonGroupInWindowTest extends MultiBrowserTest {
    @Test
    public void radioButtonGroup_setFirstItemInWindow_valueShouldBeSet() {
        testSetDayInRadioButtonGroup("Monday");
    }

    @Test
    public void radioButtonGroup_setLastItemInWindow_valueShouldBeSet() {
        testSetDayInRadioButtonGroup("Sunday");
    }

    @Test
    public void radioButtonGroup_setMiddleItemInWindow_valueShouldBeSet() {
        testSetDayInRadioButtonGroup("Thursday");
    }

    private void testSetDayInRadioButtonGroup(String day) {
        openTestURL();

        TextFieldElement defaultDay = $(TextFieldElement.class)
                .id(RadioButtonGroupInWindow.DEFAULT_DAY_TEXT_FIELD_ID);
        defaultDay.setValue(day);
        ButtonElement openWindowButton = $(ButtonElement.class)
                .id(RadioButtonGroupInWindow.OPEN_WINDOW_BUTTON_ID);
        openWindowButton.click();
        waitForElementPresent(By
                .id(RadioButtonGroupInWindow.WEEK_DAYS_RADIO_BUTTON_GROUP_ID));
        RadioButtonGroupElement radioButtonGroupElement = $(
                RadioButtonGroupElement.class).id(
                        RadioButtonGroupInWindow.WEEK_DAYS_RADIO_BUTTON_GROUP_ID);
        Assert.assertEquals(
                "The expected value is not set in RadioButtonGroup.", day,
                radioButtonGroupElement.getValue());
    }
}
