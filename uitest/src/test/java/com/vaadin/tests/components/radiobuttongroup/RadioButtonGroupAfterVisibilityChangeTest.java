package com.vaadin.tests.components.radiobuttongroup;

import com.vaadin.testbench.elements.RadioButtonGroupElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class RadioButtonGroupAfterVisibilityChangeTest
        extends MultiBrowserTest {

    @Test
    public void verifyOptionIsSelectable() {
        openTestURL();

        getRadioButtonGroupElement().selectByText("false");
        findElement(By.id("hideB")).click();
        findElement(By.id("setAndShow")).click();
        isSelectedOnClientSide("true");
        getRadioButtonGroupElement().selectByText("false");
        isSelectedOnClientSide("false");
    }

    private RadioButtonGroupElement getRadioButtonGroupElement() {
        RadioButtonGroupElement radioButtonGroup = $(
                RadioButtonGroupElement.class).first();
        return radioButtonGroup;
    }

    private void isSelectedOnClientSide(String selectedText) {
        List<WebElement> selectOptions = findElement(By.id("radioButton"))
                .findElements(By.className("v-select-option-selected"));
        assertEquals("Item should be selected", selectOptions.size(), 1);
        assertTrue(selectOptions.get(0).getText().equals(selectedText));
    }
}
