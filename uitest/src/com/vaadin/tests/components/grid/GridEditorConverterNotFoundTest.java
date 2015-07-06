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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.components.grid.basicfeatures.GridBasicFeaturesTest;

public class GridEditorConverterNotFoundTest extends GridBasicFeaturesTest {

    @Override
    protected Class<?> getUIClass() {
        // Use the correct UI with helpers from GridBasicFeatures
        return GridEditorConverterNotFound.class;
    }

    @Test
    public void testConverterNotFound() {
        openTestURL();

        $(GridElement.class).first().getCell(0, 0).doubleClick();

        assertEquals("1. com.vaadin.data.Buffered$SourceException",
                getLogRow(0));
    }
}
