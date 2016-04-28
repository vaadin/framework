/*
 * Copyright 2000-2013 Vaadin Ltd.
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

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.table.DndTableTargetDetails;
import com.vaadin.ui.Tree;

/**
 * Test UI for tree as a drop target: AbstractSelectTargetDetails should provide
 * getMouseEvent() method.
 * 
 * @author Vaadin Ltd
 */
public class DndTreeTargetDetails extends DndTableTargetDetails {

    @Override
    protected void setup(VaadinRequest request) {
        createSourceTable();

        Tree target = new Tree();
        target.addStyleName("target");
        target.setWidth(100, Unit.PERCENTAGE);
        target.addItem("treeItem");
        target.setDropHandler(new TestDropHandler());
        addComponent(target);
    }

    @Override
    protected String getTestDescription() {
        return "Mouse details should be available for AbstractSelectTargetDetails DnD when tree is a target";
    }

}
