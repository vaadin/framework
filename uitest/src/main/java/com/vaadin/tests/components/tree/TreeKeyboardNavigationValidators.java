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
package com.vaadin.tests.components.tree;

import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.AlwaysFailValidator;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.util.HierarchicalContainer;
import com.vaadin.v7.ui.Tree;

public class TreeKeyboardNavigationValidators extends TestBase {

    @Override
    protected void setup() {
        addComponent(getTree());
    }

    private Tree getTree() {
        Tree tree = new Tree();
        tree.setSizeFull();
        tree.setContainerDataSource(generateHierarchicalContainer());
        tree.setImmediate(true);
        tree.addValidator(new AlwaysFailValidator("failed"));
        return tree;
    }

    private Container generateHierarchicalContainer() {
        HierarchicalContainer cont = new HierarchicalContainer();
        for (int i = 1; i < 6; i++) {
            cont.addItem(i);
            for (int j = 1; j < 3; j++) {
                String id = i + " -> " + j;
                cont.addItem(id);
                cont.setChildrenAllowed(id, false);
                cont.setParent(id, i);
            }
        }
        return cont;
    }

    @Override
    protected String getDescription() {
        return "Keyboard navigation should still work in a tree with validators.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7057;
    }

}
