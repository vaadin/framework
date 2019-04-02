package com.vaadin.tests.components.radiobuttongroup;

import com.vaadin.testbench.elements.RadioButtonGroupElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class RadioButtonGroupChangeDataProviderTest extends MultiBrowserTest {
    @Test
    public void verifyOptionIsSelectable() {
        openTestURL();

        getRadioButtonGroupElement().selectByText("ccc");
        isSelectedOnClientSide("ccc");
        findElement(By.id("changeProvider")).click();
        assertTrue(findElement(By.id("radioButton"))
                .findElements(By.className("v-select-option-selected"))
                .isEmpty());
        isValueChangeListenerFired("null");
        getRadioButtonGroupElement().selectByText("222");
        isSelectedOnClientSide("222");
    }

    private RadioButtonGroupElement getRadioButtonGroupElement() {
        RadioButtonGroupElement radioButtonGroup = $(
                RadioButtonGroupElement.class).first();
        return radioButtonGroup;
    }

    private void isSelectedOnClientSide(String selectedText) {
        List<WebElement> selectOptions = findElement(By.id("radioButton"))
                .findElements(By.className("v-select-option-selected"));
        assertEquals("One item should be selected", selectOptions.size(), 1);
        String value = selectOptions.get(0).getText();
        assertTrue(String.format("Expected value was %s, but %s is selected",
                selectedText, value), value.equals(selectedText));
        isValueChangeListenerFired(selectedText);
    }

    private void isValueChangeListenerFired(String value) {
        assertTrue(String.format(
                "ValueChangeListener was not fired. Current value: %s ", value),
                findElement(By.id("Log_row_0")).getText().contains(value));
    }
}
