/*
 * Copyright 2000-2018 Vaadin Ltd.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;

/**
 * @author Vaadin Ltd
 */
public class GridAssistiveDeviceOnlyTextTest extends SingleBrowserTest {

    // @Test
    // https://github.com/vaadin/framework/pull/10567#issuecomment-366211475
    public void checkAssistiveDeviceOnlyText() {
        openTestURL();

        assertTrue("The select all cell of the default grid should not contain any text.",
                getFirstCell("first").getText().isEmpty());

        assertEquals("The select all cell of the aria-enabled grid should contain " +
                        "the given string.", "Selects all rows of the table.",
                getFirstCell("second").getText());
    }

    private GridElement.GridCellElement getFirstCell(String gridId) {
        return $(GridElement.class).id(gridId).getHeaderCell(0, 0);
    }
}
