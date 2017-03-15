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

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridEditorElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * @author Vaadin Ltd
 *
 */
public class GridEditorEventsTest extends MultiBrowserTest {

    @Test
    public void editorEvents() throws InterruptedException {
        openTestURL();

        GridElement grid = $(GridElement.class).first();

        assertEditorEvents(0, grid);
        assertEditorEvents(1, grid);
    }

    private void assertEditorEvents(int index, GridElement grid) {
        GridEditorElement editor = updateField(index, grid, "foo");
        editor.save();

        Assert.assertEquals((index * 2 + 1) + ". editor is saved",
                getLogRow(0));

        editor = updateField(index, grid, "bar");
        editor.cancel();

        Assert.assertEquals((index * 2 + 2) + ". editor is canceled",
                getLogRow(0));
    }

    private GridEditorElement updateField(int index, GridElement grid,
            String text) {
        grid.getRow(index).doubleClick();

        GridEditorElement editor = grid.getEditor();
        WebElement focused = getFocusedElement();
        assertEquals("input", focused.getTagName());
        focused.sendKeys(text);
        return editor;
    }
}
