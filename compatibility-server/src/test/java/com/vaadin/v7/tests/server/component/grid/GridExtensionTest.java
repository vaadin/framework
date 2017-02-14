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
package com.vaadin.v7.tests.server.component.grid;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.AbstractGridExtension;

public class GridExtensionTest {

    public static class DummyGridExtension extends AbstractGridExtension {

        public DummyGridExtension(Grid grid) {
            super(grid);
        }
    }

    @Test
    public void testCreateExtension() {
        Grid grid = new Grid();
        DummyGridExtension dummy = new DummyGridExtension(grid);
        assertTrue("DummyGridExtension never made it to Grid",
                grid.getExtensions().contains(dummy));
    }
}
