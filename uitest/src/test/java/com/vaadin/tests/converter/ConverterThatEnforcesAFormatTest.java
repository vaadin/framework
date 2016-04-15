package com.vaadin.tests.converter;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ConverterThatEnforcesAFormatTest extends MultiBrowserTest {

    private TextFieldElement field;

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
        field = $(TextFieldElement.class).first();
    }

    @Test
    public void checkDefault() {
        waitUntilValueIs("50.000");
    }

    @Test
    public void checkRounding() {
        setValue("50.0202", Keys.ENTER);
        waitUntilValueIs("50.020");
    }

    @Test
    public void checkElaborating() {
        setValue("12");
        waitUntilValueIs("12.000");
    }

    @Test
    public void checkText() {
        setValue("abc", Keys.ENTER);
        waitUntilValueIs("abc");
        waitUntilHasCssClass("v-textfield-error");
    }

    private void setValue(String value, CharSequence... keysToSend) {
        field.setValue(value);
        if (keysToSend.length > 0) {
            field.sendKeys(keysToSend);
        } else {
            field.submit();
        }
    }

    private void waitUntilValueIs(final String expected) {
        waitUntil(new ExpectedCondition<Boolean>() {
            private String actual;

            @Override
            public Boolean apply(WebDriver arg0) {
                actual = field.getValue();
                return expected.equals(actual);
            }

            @Override
            public String toString() {
                return String.format(
                        "the field to have value '%s' (was: '%s')", expected,
                        actual);
            }
        });
    }

    private void waitUntilHasCssClass(final String className) {
        waitUntil(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver arg0) {
                return hasCssClass(field, className);
            }

            @Override
            public String toString() {
                return String.format("the field to have css class '%s'",
                        className);
            }
        });
    }

}
