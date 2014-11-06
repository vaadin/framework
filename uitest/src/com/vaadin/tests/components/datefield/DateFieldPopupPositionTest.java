/*
 * Copyright 2000-2014 Vaadin Ltd.
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

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for date field popup calendar position.
 * 
 * @author Vaadin Ltd
 */
public abstract class DateFieldPopupPositionTest extends MultiBrowserTest {

    @Test
    public void testPopupPosition() {
        openTestURL();

        int height = getFieldBottom() + 150;
        adjustBrowserWindow(height);

        openPopup();

        checkPopupPosition();
    }

    protected abstract void checkPopupPosition();

    protected WebElement getPopup() {
        return findElement(By.className("v-datefield-popup"));
    }

    private void adjustBrowserWindow(int height) {
        Dimension size = getDriver().manage().window().getSize();
        getDriver().manage().window()
                .setSize(new Dimension(size.getWidth(), height));
    }

    private int getFieldBottom() {
        DateFieldElement dateField = $(DateFieldElement.class).first();
        return dateField.getLocation().getY() + dateField.getSize().getHeight();
    }

    private void openPopup() {
        findElement(By.className("v-datefield-button")).click();
        if (!isElementPresent(By.className("v-datefield-popup"))) {
            findElement(By.className("v-datefield-button")).click();
        }
    }
}
