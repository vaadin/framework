package com.vaadin.tests.components.datefield;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DateFieldPopupClosingTest extends MultiBrowserTest {
    /*
     * try to open/close many times (not one time) because this defect is
     * reproduced randomly (depends on timer)
     */
    private static final int N = 100;

    @Test
    public void testDateFieldPopupClosing() throws InterruptedException,
            IOException {
        openTestURL();

        for (int i = 0; i < N; i++) {
            clickDateDatePickerButton();

            waitUntil(ExpectedConditions.visibilityOfElementLocated(By
                    .className("v-datefield-popup")));

            clickDateDatePickerButton();

            waitUntil(ExpectedConditions.invisibilityOfElementLocated(By
                    .className("v-datefield-popup")));
        }
    }

    private void clickDateDatePickerButton() {
        DateFieldElement dateField = $(DateFieldElement.class).first();
        dateField.findElement(By.tagName("button")).click();
    }

}