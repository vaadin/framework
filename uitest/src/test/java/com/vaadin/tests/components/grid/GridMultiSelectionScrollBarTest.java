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
package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Locale;

import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridMultiSelectionScrollBarTest extends MultiBrowserTest {

    @Test
    public void testNoVisibleScrollBar() throws IOException {
        setDebug(true);
        openTestURL();

        assertTrue("Horizontal scrollbar should not be visible.",
                $(GridElement.class).first().getHorizontalScroller()
                        .getAttribute("style").toLowerCase(Locale.ROOT)
                        .contains("display: none;"));

        // Just to make sure nothing odd happened.
        assertNoErrorNotifications();
    }

}
