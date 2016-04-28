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
package com.vaadin.tests.components.table;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TableBlurFocusTest extends MultiBrowserTest {

    @Test
    public void testBlurAndFocus() throws InterruptedException {
        openTestURL();
        waitForElementPresent(By.className("v-button"));

        assertAnyLogText("1. variable change");
        assertEquals("Unexpected column header,", "COLUMN2",
                $(TableElement.class).first().getHeaderCell(1).getCaption());
        assertEquals("Unexpected button caption,", "click to focus",
                $(ButtonElement.class).first().getCaption());

        $(ButtonElement.class).first().click();
        assertAnyLogText("2. focus", "3. focus");

        $(TableElement.class).first().getHeaderCell(1).click();
        assertAnyLogText("3. blur", "4. blur");
    }

    private void assertAnyLogText(String... texts) {
        assertThat(String.format(
                "Correct log text was not found, expected any of %s",
                Arrays.asList(texts)), logContainsAnyText(texts));
    }

    private boolean logContainsAnyText(String... texts) {
        for (String text : texts) {
            if (logContainsText(text)) {
                return true;
            }
        }
        return false;
    }
}
