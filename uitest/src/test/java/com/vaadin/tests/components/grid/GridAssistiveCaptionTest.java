/*
 * Copyright 2000-2017 Vaadin Ltd.
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

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Vaadin Ltd
 */
public class GridAssistiveCaptionTest extends SingleBrowserTest {

    @Test
    public void checkGridAriaLabel() {
        openTestURL();

        GridElement.GridCellElement headerCell = $(GridElement.class).first()
                .getHeaderCell(0, 1);

        // default grid has no aria-label
        assertNull("Column should not contain aria-label",
                headerCell.getAttribute("aria-label"));

        $(ButtonElement.class).caption("addAssistiveCaption").first().click();
        assertTrue("Column should contain aria-label",
                headerCell.getAttribute("aria-label").equals("Press Enter to sort."));

        $(ButtonElement.class).caption("removeAssistiveCaption").first().click();
        assertNull("Column should not contain aria-label",
                headerCell.getAttribute("aria-label"));
    }
}
