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
package com.vaadin.v7.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Grid;

@SuppressWarnings("serial")
// @Push
public class ProgrammaticEditorControl extends AbstractTestUIWithLog {

    private Grid grid;
    private IndexedContainer container = new IndexedContainer();

    @Override
    protected void setup(VaadinRequest request) {
        container.addContainerProperty("name", String.class, null);
        container.addItem("test").getItemProperty("name").setValue("test");
        grid = new Grid();
        grid.setContainerDataSource(container);
        grid.setEditorEnabled(true);
        addComponent(grid);

        Button button = new Button("Edit", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                grid.editItem("test");
            }
        });
        addComponent(button);
        Button button2 = new Button("Cancel", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                grid.cancelEditor();
            }
        });
        addComponent(button2);

    }

}
