package com.vaadin.tests.components.combobox;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ComboBoxInputPromptTest extends MultiBrowserTest {

    @Test
    public void promptIsHiddenForDisabledAndReadonly() {
        openTestURL();

        ComboBoxElement normalComboBox = getComboBoxWithCaption("Normal");
        ComboBoxElement disabledComboBox = getComboBoxWithCaption("Disabled");
        ComboBoxElement readOnlyComboBox = getComboBoxWithCaption("Read-only");

        assertThat(getInputPromptValue(normalComboBox),
                is("Normal input prompt"));
        assertThat(getInputPromptValue(disabledComboBox), isEmptyString());
        assertThat(getInputPromptValue(readOnlyComboBox), isEmptyString());

        toggleDisabledAndReadonly();
        assertThat(getInputPromptValue(disabledComboBox),
                is("Disabled input prompt"));
        assertThat(getInputPromptValue(readOnlyComboBox),
                is("Read-only input prompt"));

        toggleDisabledAndReadonly();
        assertThat(getInputPromptValue(disabledComboBox), isEmptyString());
        assertThat(getInputPromptValue(readOnlyComboBox), isEmptyString());
    }

    private void toggleDisabledAndReadonly() {
        $(ButtonElement.class).first().click();
    }

    private String getInputPromptValue(ComboBoxElement comboBox) {
        WebElement input = comboBox.findElement(By.tagName("input"));

        return input.getAttribute("value");
    }

    private ComboBoxElement getComboBoxWithCaption(String caption) {
        return $(ComboBoxElement.class).caption(caption).first();
    }

}
