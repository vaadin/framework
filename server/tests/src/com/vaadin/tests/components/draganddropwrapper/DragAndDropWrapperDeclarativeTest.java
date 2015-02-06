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
package com.vaadin.tests.components.draganddropwrapper;

import org.junit.Test;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.declarative.DesignContext;

public class DragAndDropWrapperDeclarativeTest extends
        DeclarativeTestBase<DragAndDropWrapper> {

    @Test
    public void testDefaultDnDWrapper() {
        Button okButton = new Button("OK");
        String input = "<v-drag-and-drop-wrapper>"
                + new DesignContext().createElement(okButton)
                + "</v-drag-and-drop-wrapper>";
        DragAndDropWrapper wrapper = new DragAndDropWrapper(okButton);
        testWrite(input, wrapper);
        testRead(input, wrapper);
    }

    @Test
    public void testNoDragImage() {
        Button okButton = new Button("OK");
        String input = "<v-drag-and-drop-wrapper drag-start-mode='wrapper'>"
                + new DesignContext().createElement(okButton)
                + "</v-drag-and-drop-wrapper>";
        DragAndDropWrapper wrapper = new DragAndDropWrapper(okButton);
        wrapper.setDragStartMode(DragStartMode.WRAPPER);
        testWrite(input, wrapper);
        testRead(input, wrapper);
    }

    @Test
    public void testWithDragImage() {
        Button dragImage = new Button("Cancel");
        Button okButton = new Button("OK");
        String input = "<v-drag-and-drop-wrapper drag-start-mode='component_other'>"
                + new DesignContext().createElement(okButton)
                + new DesignContext().createElement(dragImage).attr(
                        ":drag-image", "") + "</v-drag-and-drop-wrapper>";
        DragAndDropWrapper wrapper = new DragAndDropWrapper(okButton);
        wrapper.setDragStartMode(DragStartMode.COMPONENT_OTHER);
        wrapper.setDragImageComponent(dragImage);
        testWrite(input, wrapper);
        testRead(input, wrapper);
    }
}
