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
package com.vaadin.tests.components.grid;

import java.util.Random;

import com.vaadin.annotations.Theme;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;

@Theme("valo")
public class GridSubPixelProblemWrapping extends AbstractTestUI {

    Random r = new Random();

    public static class DataObject {
        String foo;
        String Bar;

        public DataObject(Random r) {
            foo = r.nextInt() + "";
            Bar = r.nextInt() + "";
        }

        public DataObject(String foo, String bar) {
            this.foo = foo;
            Bar = bar;
        }

        public String getFoo() {
            return foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }

        public String getBar() {
            return Bar;
        }

        public void setBar(String bar) {
            Bar = bar;
        }

    }

    Button button = new Button("Click", new ClickListener() {
        @Override
        public void buttonClick(ClickEvent event) {
            addDAO();
        }
    });

    private BeanItemContainer<DataObject> container;
    private int counter = 0;

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(button);
        container = new BeanItemContainer<DataObject>(DataObject.class);
        container.addBean(new DataObject("Foo", "Bar"));
        Grid grid = new Grid(container);
        grid.getColumn("foo").setWidth(248.525);
        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.setEditorEnabled(true);
        grid.setWidth("500px");

        addComponent(grid);
    }

    private void addDAO() {
        counter++;
        container.addBean(new DataObject("Foo" + counter, "Bar" + counter));

    }
}
