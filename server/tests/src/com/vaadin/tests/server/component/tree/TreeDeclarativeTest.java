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

import com.vaadin.server.ExternalResource;
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
    public void testDragMode() {
        String design = "<v-tree drag-mode='node' />";

        Tree tree = new Tree();
        tree.setDragMode(TreeDragMode.NODE);

        testRead(design, tree);
        testWrite(design, tree);
    }

    @Test
    public void testEmpty() {
        testRead("<v-tree />", new Tree());
        testWrite("<v-tree />", new Tree());
    }

    @Test
    public void testNodes() {
        String design = "<v-tree>" //
                + "  <node text='Node'/>" //
                + "  <node text='Parent'>" //
                + "    <node text='Child'>" //
                + "      <node text='Grandchild'/>" //
                + "    </node>" //
                + "  </node>" //
                + "  <node text='With icon' icon='http://example.com/icon.png'/>" //
                + "</v-tree>";

        Tree tree = new Tree();

        tree.addItem("Node");

        tree.addItem("Parent");

        tree.addItem("Child");
        tree.setParent("Child", "Parent");

        tree.addItem("Grandchild");
        tree.setParent("Grandchild", "Child");

        tree.addItem("With icon");
        tree.setItemIcon("With icon", new ExternalResource(
                "http://example.com/icon.png"));

        testRead(design, tree);
        testWrite(design, tree, true);
    }
}
