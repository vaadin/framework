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
package com.vaadin.tests.server.component.grid.declarative;

import org.junit.Test;

import com.vaadin.ui.Grid;
import com.vaadin.ui.declarative.DesignException;

public class GridStructureDeclarativeTest extends GridDeclarativeTestBase {

    @Test
    public void testReadEmptyGrid() {
        String design = "<vaadin-grid />";
        testRead(design, new Grid(), false);
    }

    @Test
    public void testEmptyGrid() {
        String design = "<vaadin-grid></vaadin-grid>";
        Grid expected = new Grid();
        testWrite(design, expected);
        testRead(design, expected, true);
    }

    @Test(expected = DesignException.class)
    public void testMalformedGrid() {
        String design = "<vaadin-grid><vaadin-label /></vaadin-grid>";
        testRead(design, new Grid());
    }

    @Test(expected = DesignException.class)
    public void testGridWithNoColGroup() {
        String design = "<vaadin-grid><table><thead><tr><th>Foo</tr></thead></table></vaadin-grid>";
        testRead(design, new Grid());
    }
}
