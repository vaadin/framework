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
package com.vaadin.tests.components.orderedlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.Tree;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class VerticalRelativeSizeWithoutExpand extends UI {

    @Override
    protected void init(VaadinRequest request) {

        final VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setMargin(true);
        layout.setSpacing(true);
        setContent(layout);

        Panel panel1 = new Panel("This should not be seen");
        panel1.setSizeFull();
        VerticalLayout verticalLayout1 = new VerticalLayout();
        verticalLayout1.setSizeFull();
        Tree tree = new Tree();
        tree.setSizeFull();
        tree.setContainerDataSource(new BeanItemContainer<>(String.class));
        String a = "aaaaaaaaaaaaaaaaaaaaaaaa";
        String b = "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb";
        String c = "ccccccccccccccccccccccccccccccccccccccccccccccccc";
        tree.addItem(a);
        tree.addItem(b);
        tree.addItem(c);
        tree.setChildrenAllowed(a, true);
        tree.setChildrenAllowed(b, true);
        tree.setParent(b, a);
        tree.setParent(c, b);
        verticalLayout1.addComponent(tree);
        panel1.setContent(verticalLayout1);
        layout.addComponent(panel1);

        final Panel panel2 = new Panel("This should use all space");
        panel2.setSizeFull();

        layout.addComponent(panel2);
        layout.setExpandRatio(panel2, 1);

    }

}
