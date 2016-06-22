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

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests Table Footer ClickListener
 * 
 * @author Vaadin Ltd
 */
public class HeaderFooterClickLeftRightMiddleTest extends MultiBrowserTest {

    @Test
    public void testFooter() throws IOException {
        openTestURL();

        waitForElementPresent(By.className("v-table"));

        TableElement table = $(TableElement.class).first();

        table.getHeaderCell(0).click();
        assertAnyLogText("1. Click on header col1 using left");

        table.getHeaderCell(0).contextClick();
        assertAnyLogText("2. Click on header col1 using right");

        table.getHeaderCell(0).doubleClick();
        assertAnyLogText("4. Double click on header col1 using left",
                "5. Double click on header col1 using left");

        table.getFooterCell(1).click();
        assertAnyLogText("5. Click on footer col2 using left",
                "6. Click on footer col2 using left");

        table.getFooterCell(1).contextClick();
        assertAnyLogText("6. Click on footer col2 using right",
                "7. Click on footer col2 using right");

        table.getFooterCell(1).doubleClick();
        assertAnyLogText("8. Double click on footer col2 using left",
                "9. Double click on footer col2 using left",
                "10. Double click on footer col2 using left");
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
