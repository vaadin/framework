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

import com.vaadin.annotations.Widgetset;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.tests.widgetset.client.SimpleTestBean;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Label;

@Widgetset(TestingWidgetSet.NAME)
public class CustomRenderer extends AbstractTestUI {

    private static final Object INT_ARRAY_PROPERTY = "int array";
    private static final Object VOID_PROPERTY = "void";
    private static final Object BEAN_PROPERTY = "pojo";

    static final Object ITEM_ID = "itemId1";
    static final String DEBUG_LABEL_ID = "debuglabel";
    static final String INIT_DEBUG_LABEL_CAPTION = "Debug label placeholder";

    @Override
    protected void setup(VaadinRequest request) {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty(INT_ARRAY_PROPERTY, int[].class,
                new int[] {});
        container.addContainerProperty(VOID_PROPERTY, Void.class, null);
        container.addContainerProperty(BEAN_PROPERTY, SimpleTestBean.class,
                null);

        Item item = container.addItem(ITEM_ID);

        @SuppressWarnings("unchecked")
        Property<int[]> propertyIntArray = item
                .getItemProperty(INT_ARRAY_PROPERTY);
        propertyIntArray.setValue(new int[] { 1, 1, 2, 3, 5, 8, 13 });

        @SuppressWarnings("unchecked")
        Property<SimpleTestBean> propertyPojo = item
                .getItemProperty(BEAN_PROPERTY);
        SimpleTestBean bean = new SimpleTestBean();
        bean.setValue(42);
        propertyPojo.setValue(bean);

        Label debugLabel = new Label(INIT_DEBUG_LABEL_CAPTION);
        debugLabel.setId(DEBUG_LABEL_ID);

        Grid grid = new Grid(container);

        grid.getColumn(INT_ARRAY_PROPERTY).setRenderer(new IntArrayRenderer());
        grid.getColumn(VOID_PROPERTY).setRenderer(
                new RowAwareRenderer(debugLabel));
        grid.getColumn(BEAN_PROPERTY).setRenderer(new BeanRenderer());

        grid.setSelectionMode(SelectionMode.NONE);

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
        return Integer.valueOf(13334);
    }

}
