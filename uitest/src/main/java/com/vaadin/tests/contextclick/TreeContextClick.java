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
package com.vaadin.tests.contextclick;

import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.TreeContextClickEvent;

public class TreeContextClick
        extends AbstractContextClickUI<Tree, TreeContextClickEvent> {

    @Override
    protected Tree createTestComponent() {
        Tree tree = new Tree();
        tree.addItem("Foo");
        tree.addItem("Bar");
        tree.addItem("Baz");
        tree.setParent("Baz", "Bar");
        tree.setHeight("200px");
        return tree;
    }

    @Override
    protected void handleContextClickEvent(TreeContextClickEvent event) {
        log("ContextClickEvent: " + event.getItemId());
    }
}
