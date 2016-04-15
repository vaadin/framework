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

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.SingleBrowserTest;

@TestCategory("grid")
public class ProgrammaticEditorControlTest extends SingleBrowserTest {

    @Test
    public void multipleOpenFromServerSide() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        ButtonElement editButton = $(ButtonElement.class).caption("Edit")
                .first();
        ButtonElement cancelButton = $(ButtonElement.class).caption("Cancel")
                .first();

        editButton.click();
        assertEditorFieldContents(grid, "test");
        cancelButton.click();

        assertEditorNotPresent(grid);

        editButton.click();
        assertEditorFieldContents(grid, "test");
    }

    private void assertEditorFieldContents(GridElement grid, String text) {
        TextFieldElement editorField = wrap(TextFieldElement.class, grid
                .getEditor().getField(0));
        Assert.assertEquals(text, editorField.getValue());
    }

    private void assertEditorNotPresent(GridElement grid) {
        try {
            grid.getEditor();
            Assert.fail("Editor should not be present");
        } catch (Exception e) {

        }
    }

}