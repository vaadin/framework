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
package com.vaadin.tests.layouts.layouttester;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractLayout;
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
public class BaseAddReplaceMove extends BaseLayoutTestUI {

    /**
     * @param layoutClass
     */
    public BaseAddReplaceMove(Class<? extends AbstractLayout> layoutClass) {
        super(layoutClass);
    }

    @Override
    protected void setup(VaadinRequest request) {
        init();
        buildLayout();
        super.setup(request);
    }

    private void buildLayout() {
        // Set undefined height to avoid expanding
        l2.setHeight(null);
        // extra layout from which components will be moved
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

        l1.addComponent(btnAdd);
        l1.addComponent(btnReplace);
        l1.addComponent(btnMove);
        l1.addComponent(btnRemove);

        btnAdd.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                l2.addComponent(new TextField());
            }
        });
        btnReplace.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                l2.replaceComponent(c1, c3);
            }
        });
        btnMove.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                l2.moveComponentsFrom(source);
            }
        });
        btnRemove.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                l2.removeComponent(c1);
                l2.removeComponent(c2);
            }
        });

        l2.addComponent(c1);
        l2.addComponent(c2);
        l2.addComponent(c3);
    }
}
