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
package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class JavaScriptRenderers extends AbstractReindeerTestUI {

    public static class ItemBean {
        private Integer id;
        private String string;
        private MyBean bean;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }

        public MyBean getBean() {
            return bean;
        }

        public void setBean(MyBean bean) {
            this.bean = bean;
        }
    }

    public static class MyBean {
        private int integer;
        private String string;

        public MyBean(int integer, String string) {
            super();
            this.integer = integer;
            this.string = string;
        }

        public int getInteger() {
            return integer;
        }

        public void setInteger(int integer) {
            this.integer = integer;
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }
    }

    private Grid<ItemBean> grid;

    @Override
    protected void setup(VaadinRequest request) {
        List<ItemBean> items = new ArrayList<>(1000);
        for (int i = 0; i < 1000; i++) {
            ItemBean bean = new ItemBean();
            bean.setId(i);
            bean.setString("string" + i);
            bean.setBean(new MyBean(i + 1, Integer.toString(i - 1)));
            items.add(bean);
        }

        Label clientLog = new Label();
        clientLog.setId("clientLog");
        addComponent(clientLog);
        grid = createGrid(items);
        addComponent(grid);

        addComponent(new Button("Recreate grid", e -> {
            Grid<ItemBean> newGrid = createGrid(items);
            replaceComponent(grid, newGrid);
            grid = newGrid;
        }));
    }

    private Grid<ItemBean> createGrid(List<ItemBean> items) {
        Grid<ItemBean> grid = new Grid<>();

        grid.addColumn(item -> item.getId().toString()).setCaption("Id");
        grid.addColumn(ItemBean::getBean, new MyBeanJSRenderer())
                .setCaption("Bean");
        grid.addColumn(ItemBean::getString, new JavaScriptStringRenderer())
                .setCaption("String");
        grid.addColumn(ItemBean::getString,
                new JavaScriptStringRendererWithDestoryMethod())
                .setCaption("String2");

        grid.setItems(items);

        return grid;
    }

}
