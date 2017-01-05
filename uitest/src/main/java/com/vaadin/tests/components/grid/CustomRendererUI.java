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

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.tests.widgetset.client.EmptyEnum;
import com.vaadin.tests.widgetset.client.SimpleTestBean;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Label;

@Widgetset(TestingWidgetSet.NAME)
public class CustomRendererUI extends AbstractTestUI {

    public static class Data {
        private int[] array;

        private EmptyEnum emptyProperty;

        private SimpleTestBean bean;

        private final String id;

        public Data(String id) {
            this.id = id;
        }

        public int[] getArray() {
            return array;
        }

        public void setArray(int[] array) {
            this.array = array;
        }

        public EmptyEnum getEmptyProperty() {
            return emptyProperty;
        }

        public void setEmptyProperty(EmptyEnum emptyProperty) {
            this.emptyProperty = emptyProperty;
        }

        public SimpleTestBean getBean() {
            return bean;
        }

        public void setBean(SimpleTestBean bean) {
            this.bean = bean;
        }

        @Override
        public String toString() {
            return id;
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        Data data = new Data("test-data");
        data.setArray(new int[] { 1, 1, 2, 3, 5, 8, 13 });

        SimpleTestBean bean = new SimpleTestBean();
        bean.setValue(42);
        data.setBean(bean);

        Grid<Data> grid = new Grid<>();

        Label debugLabel = new Label("Debug label placeholder");
        debugLabel.setId("debuglabel");
        grid.addColumn(Data::getArray, new IntArrayRenderer());
        grid.addColumn(Data::getEmptyProperty,
                new RowAwareRenderer(debugLabel));
        grid.addColumn(Data::getBean, new BeanRenderer());

        grid.setSelectionMode(SelectionMode.NONE);

        grid.setItems(data);

        addComponent(grid);
        addComponent(debugLabel);
    }

    @Override
    protected String getTestDescription() {
        return "Verifies that renderers operating on other data than "
                + "just Strings also work ";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13334;
    }

}
