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
package com.vaadin.tests.components.table;

import java.util.Arrays;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

@PreserveOnRefresh
@Theme("valo")
public class TableExpandRatio extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {

        BeanItemContainer<MyItem> container = new BeanItemContainer<TableExpandRatio.MyItem>(
                MyItem.class, Arrays.asList(new MyItem("one", 1), new MyItem(
                        "two", 2)));

        final Table table = new Table(null, container);

        table.setWidth("800px");
        table.setImmediate(true);

        Button widthButton = new Button("Set Width",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        table.setColumnWidth("value", 300);

                    }
                });

        Button expandButton = new Button("Set Expand Ratio",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        table.setColumnExpandRatio("value", 1);

                    }
                });

        widthButton.setId("widthbutton");
        expandButton.setId("expandbutton");

        VerticalLayout layout = new VerticalLayout(widthButton, expandButton,
                table);
        addComponent(layout);
    }

    public class MyItem {

        private String name;
        private Integer value;

        public MyItem(String name, Integer value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }
    }

    @Override
    protected String getTestDescription() {
        return "When a column has fixed width and it is changed to expand ratio, the width should update accordingly";
    }

    @Override
    protected Integer getTicketNumber() {
        return 15101;
    }
}
