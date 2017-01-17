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

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridRowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * @author Vaadin Ltd
 *
 */
public class DisallowedDeselectionTest extends MultiBrowserTest {

    @Test
    public void checkDeselection() {
        openTestURL();

        GridRowElement row = $(GridElement.class).first().getRow(0);
        Assert.assertFalse(row.isSelected());

        select(row);
        Assert.assertTrue(row.isSelected());

        // deselection is disallowed
        select(row);
        Assert.assertTrue(row.isSelected());

        // select another row
        GridRowElement oldRow = row;
        row = $(GridElement.class).first().getRow(1);
        select(row);
        Assert.assertTrue(row.isSelected());
        Assert.assertFalse(oldRow.isSelected());

        $(ButtonElement.class).first().click();

        select(row);
        Assert.assertFalse(row.isSelected());
    }

    private void select(GridRowElement row) {
        row.getCell(0).click();
    }
}
