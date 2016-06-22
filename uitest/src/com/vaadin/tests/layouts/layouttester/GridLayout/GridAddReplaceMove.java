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
package com.vaadin.tests.layouts.layouttester.GridLayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;

/**
 *
 * @author Vaadin Ltd
 */

public class GridAddReplaceMove extends GridBaseLayoutTestUI {

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        buildLayout();
        super.setup(request);
    }

    private void buildLayout() {

        final HorizontalLayout source = new HorizontalLayout();
        source.addComponent(new Label("OTHER LABEL 1"));
        source.addComponent(new Label("OTHER LABEL 2"));

        final AbstractComponent c1 = new Label("<b>LABEL</b>", ContentMode.HTML);
        final AbstractComponent c2 = new Label("<b>LABEL</b>", ContentMode.HTML);
        final AbstractComponent c3 = new Table("TABLE");
        c3.setHeight("100px");
        c3.setWidth("100%");

        final Button btnAdd = new Button("Test add");
        final Button btnReplace = new Button("Test replace");
        final Button btnMove = new Button("Test move");
        final Button btnRemove = new Button("Test remove");

        layout.addComponent(btnAdd);
        layout.addComponent(btnReplace);
        layout.addComponent(btnMove);
        layout.addComponent(btnRemove);

        btnAdd.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                layout.addComponent(new TextField());
            }
        });
        btnReplace.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                layout.replaceComponent(c1, c3);
            }
        });
        btnMove.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                layout.moveComponentsFrom(source);
            }
        });
        btnRemove.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                layout.removeComponent(c1);
                layout.removeComponent(c2);
            }
        });

        layout.addComponent(c1);
        layout.addComponent(c2);
        layout.addComponent(c3);
    }
}
