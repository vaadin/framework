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
package com.vaadin.tests.components.grid;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Verifies that there's no client side errors when removing a range of rows
 * starting from the visible ones and ending into the cached ones.
 */
public class GridRemoveCachedRowsTest extends MultiBrowserTest {

    @Test
    public void testNoClientExceptionWhenRemovingARangeOfRows() {
        setDebug(true);
        openTestURL();

        // do remove a range of rows
        $(ButtonElement.class).first().click();

        assertNoErrorNotifications();
    }
}
