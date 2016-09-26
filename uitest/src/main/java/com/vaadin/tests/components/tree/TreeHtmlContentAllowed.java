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
package com.vaadin.tests.components.tree;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Tree;

public class TreeHtmlContentAllowed extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        String textParent = "Just text";
        String htmlParent = "Some <b>html</b>";
        String textChild = "Child text";
        String htmlChild = "Child <i>html</i>";
        String htmlElementChild = "Child <span id='my-html-element'>element html</span>";

        final Tree tree = new Tree("A tree");
        tree.addItem(textParent);
        tree.addItem(htmlParent);
        tree.addItem(textChild);
        tree.addItem(htmlChild);
        tree.addItem(htmlElementChild);
        tree.setParent(textChild, textParent);
        tree.setParent(htmlChild, htmlParent);

        tree.setChildrenAllowed(textChild, false);
        tree.setChildrenAllowed(htmlChild, false);
        tree.setChildrenAllowed(htmlElementChild, false);

        final CheckBox toggle = new CheckBox("HTML content allowed",
                tree.isHtmlContentAllowed());
        toggle.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                tree.setHtmlContentAllowed(toggle.getValue().booleanValue());
            }
        });

        addComponents(tree, toggle);
    }

}
