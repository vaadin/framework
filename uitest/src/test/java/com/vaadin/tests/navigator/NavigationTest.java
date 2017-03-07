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
package com.vaadin.tests.navigator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TableRowElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class NavigationTest extends SingleBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return NavigatorTest.class;
    }

    @Test
    public void testNavigateToSameViewWithDifferentParameters() {
        openTestURL();

        ButtonElement listButton = $(ButtonElement.class)
                .caption("Navigate to list").first();
        listButton.click();

        TableElement table = $(TableElement.class).first();
        assertEquals("Unexpected navigation message",
                "2. Navigated to ListView without params", getLogRow(0));

        assertFalse("Table should not have contents",
                table.isElementPresent(By.vaadin("#row[0]")));

        listButton.click();
        assertEquals("Should not navigate to same view again.",
                "2. Navigated to ListView without params", getLogRow(0));

        $(TextFieldElement.class).first().sendKeys("foo=1");
        listButton.click();

        assertEquals("Should not navigate to same view again.",
                "3. Navigated to ListView with params foo=1", getLogRow(0));

        assertTrue("Table should have content",
                table.isElementPresent(By.vaadin("#row[0]")));
        TableRowElement row = table.getRow(0);
        assertEquals("Unexpected row content", "foo", row.getCell(0).getText());
        assertEquals("Unexpected row content", "1", row.getCell(1).getText());
    }
}
