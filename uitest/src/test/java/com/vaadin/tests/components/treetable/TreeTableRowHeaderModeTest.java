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
package com.vaadin.tests.components.treetable;

import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for absence of empty row header for RowHeaderMode.ICON_ONLY
 *
 * @author Vaadin Ltd
 */
public class TreeTableRowHeaderModeTest extends MultiBrowserTest {

    @Test
    public void testIconRowHeaderMode() {
        openTestURL();

        assertFalse("Unexpected row header for icons is found in TreeTable",
                isElementPresent(
                        By.className("v-table-header-cell-rowheader")));
    }
}
