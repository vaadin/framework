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

import java.util.List;

import org.openqa.selenium.WebElement;

/**
 * Test to see if all items of the table can be selected by pressing shift and
 * selecting the first row, and then press shift then select last row (#13483)
 * 
 * @author Vaadin Ltd
 */
public class SelectAllRowsShiftFirstTest extends SelectAllRowsTest {

    @Override
    protected void clickFirstRow() {
        List<WebElement> rows = getVisibleTableRows();
        shiftClickElement(rows.get(0));
    }

}
