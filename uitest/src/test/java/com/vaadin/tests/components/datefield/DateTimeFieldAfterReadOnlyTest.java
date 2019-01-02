package com.vaadin.tests.components.datefield;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.AbstractDateFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class DateTimeFieldAfterReadOnlyTest extends MultiBrowserTest {

    @Test
    public void readOnlyDateFieldPopupShouldNotOpen() {
        openTestURL();
        toggleReadOnly();
        openPopup();
        assertEquals(2, numberOfSelectsField());
    }

    private void openPopup() {
        $(AbstractDateFieldElement.class).first()
                .findElement(By.tagName("button")).click();
    }

    private void toggleReadOnly() {
        findElement(By.id("readOnlySwitch")).click();
    }

    private int numberOfSelectsField() {
        return findElement(By.className("v-datefield-calendarpanel-time"))
                .findElements(By.className("v-select")).size();
    }
}
