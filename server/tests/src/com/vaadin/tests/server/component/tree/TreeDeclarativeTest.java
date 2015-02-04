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
package com.vaadin.tests.server.component.tree;

import org.junit.Test;

import com.vaadin.shared.ui.MultiSelectMode;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.TreeDragMode;

/**
 * Tests the declarative support for implementations of {@link Tree}.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class TreeDeclarativeTest extends DeclarativeTestBase<Tree> {

    @Test
    public void testReadBasic() {
        testRead(getBasicDesign(), getBasicExpected());
    }

    @Test
    public void testWriteBasic() {
        testWrite(getBasicDesign(), getBasicExpected());
    }

    private String getBasicDesign() {
        return "<v-tree selectable='false' drag-mode='node' multiselect-mode='simple' />";
    }

    private Tree getBasicExpected() {
        Tree t = new Tree();
        t.setSelectable(false);
        t.setDragMode(TreeDragMode.NODE);
        t.setMultiselectMode(MultiSelectMode.SIMPLE);
        return t;
    }

    @Test
    public void testReadEmpty() {
        testRead("<v-tree />", new Tree());
    }

    @Test
    public void testWriteEmpty() {
        testWrite("<v-tree />", new Tree());
    }
}
