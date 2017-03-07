/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.components.datefield;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.Keys;

import com.vaadin.testbench.By;
import com.vaadin.testbench.customelements.AbstractDateFieldElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DateTimeFieldReadOnlyTest extends MultiBrowserTest {

    @Test
    public void readOnlyDateFieldPopupShouldNotOpen()
            throws IOException, InterruptedException {
        openTestURL();

        compareScreen("initial-date");
        toggleReadOnly();

        openPopup();
        compareScreen("readwrite-popup-date");

        closePopup();
        toggleReadOnly();
        compareScreen("readonly-date");
    }

    private void closePopup() {
        findElement(By.className("v-datefield-calendarpanel"))
                .sendKeys(Keys.RETURN);
    }

    private void openPopup() {
        // waiting for openPopup() in TB4 beta1:
        // http://dev.vaadin.com/ticket/13766
        $(AbstractDateFieldElement.class).first()
                .findElement(By.tagName("button")).click();
    }

    private void toggleReadOnly() {
        $(ButtonElement.class).caption("Switch read-only").first().click();
    }
}
