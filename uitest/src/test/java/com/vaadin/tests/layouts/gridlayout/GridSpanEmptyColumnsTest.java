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
package com.vaadin.tests.layouts.gridlayout;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests that GridLayout handles elements spanning otherwise empty columns
 * correctly (#14335)
 *
 * @since 7.2.5
 * @author markus
 */
public class GridSpanEmptyColumnsTest extends MultiBrowserTest {

    @Test
    public void componentsShouldMoveRight() throws IOException {
        openTestURL();

        LabelElement bigCell = $(LabelElement.class).id("bigCell");
        LabelElement smallCell = $(LabelElement.class).id("smallCell");

        // Width is 1000px. Big cell should take up 2/3, small cell should take
        // up 1/3.
        assertEquals(667, bigCell.getSize().width);
        assertEquals(333, smallCell.getSize().width);

    }

}
