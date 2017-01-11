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
package com.vaadin.tests.elements.tabsheet;

import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;

import com.vaadin.testbench.elements.TabSheetElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that an exception is thrown when attempting to select a tab that does
 * not exist in the tab sheet.
 */
public class TabSheetElementExceptionTest extends MultiBrowserTest {

    @Test
    public void testNoExceptionWhenFound() {
        openTestURL();
        TabSheetElement tse = $(TabSheetElement.class).first();
        for (int i = 1; i <= 5; i++) {
            tse.openTab("Tab " + i);
        }
    }

    @Test(expected = NoSuchElementException.class)
    public void testExceptionWhenNotFound() {
        openTestURL();
        TabSheetElement tse = $(TabSheetElement.class).first();
        tse.openTab("Tab 6");
    }
}