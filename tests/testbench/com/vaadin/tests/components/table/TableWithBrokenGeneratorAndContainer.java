/*
 * Copyright 2012 Vaadin Ltd.
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

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ListenerMethod.MethodException;
import com.vaadin.terminal.Terminal;
import com.vaadin.terminal.Terminal.ErrorListener;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.CacheUpdateException;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.Window.Notification;

public class TableWithBrokenGeneratorAndContainer extends TestBase {

    private CheckBox brokenContainer = new CheckBox("Broken container");
    private CheckBox brokenGenerator = new CheckBox("Broken generator");
    private CheckBox clearTableOnError = new CheckBox("Clear Table on Error");

    /**
     * Container which throws an exception on every fifth call to
     * {@link #getContainerProperty(Object, Object)}.
     * 
     * @author Vaadin Ltd
     * @version @VERSION@
     * @since 7.0
     * 
     */
    public class BrokenContainer extends IndexedContainer {
        private int counter = 0;

        public BrokenContainer() {
            super();
        }

        @Override
        public Property getContainerProperty(Object itemId, Object propertyId) {
            if (counter++ % 5 == 0 && brokenContainer.booleanValue()) {
                throw new RuntimeException(getClass().getSimpleName()
                        + " cannot fetch the property for " + itemId + "/"
                        + propertyId + " right now");
            }
            return super.getContainerProperty(itemId, propertyId);
        }
    }

    public class BrokenColumnGenerator implements ColumnGenerator {
        private int brokenInterval;
        private int counter = 0;

        public BrokenColumnGenerator(int brokenInterval) {
            this.brokenInterval = brokenInterval;
        }

        public Object generateCell(Table source, Object itemId, Object columnId) {
            if (counter++ % brokenInterval == 0
                    && brokenGenerator.booleanValue()) {
                throw new IllegalArgumentException("Broken generator for "
                        + itemId + "/" + columnId);
            } else {
                return "Generated " + itemId + "/" + columnId;
            }
        }

    }

    @Override
    protected void setup() {
        brokenContainer.setImmediate(true);
        brokenGenerator.setImmediate(true);
        clearTableOnError.setImmediate(true);
        clearTableOnError.addListener(new ValueChangeListener() {

            public void valueChange(ValueChangeEvent event) {
                setErrorHandler(clearTableOnError.booleanValue());
            }
        });
        final Table table = new Table("Semi-broken table");
        table.setContainerDataSource(createBrokenContainer(10, 4));
        table.addGeneratedColumn("Gen", new BrokenColumnGenerator(4));
        table.setPageLength(20);

        Button refreshTableCache = new Button("Refresh table cache",
                new Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        table.requestRepaint();
                        table.refreshRowCache();
                    }
                });
        addComponent(refreshTableCache);
        addComponent(brokenContainer);
        addComponent(brokenGenerator);
        addComponent(clearTableOnError);
        addComponent(table);
    }

    protected void setErrorHandler(boolean enabled) {
        if (enabled) {
            setErrorHandler(new ErrorListener() {

                public void terminalError(Terminal.ErrorEvent event) {
                    Throwable t = event.getThrowable();
                    if (t instanceof MethodException
                            && t.getCause() instanceof CacheUpdateException) {
                        Table table = ((CacheUpdateException) t.getCause())
                                .getTable();
                        table.removeAllItems();
                        table.getWindow()
                                .showNotification(
                                        "Problem updating table. Please try again later",
                                        Notification.TYPE_ERROR_MESSAGE);
                    }
                }
            });
        } else {
            setErrorHandler(this);
        }
    }

    private BrokenContainer createBrokenContainer(int rows, int cols) {
        BrokenContainer container = new BrokenContainer();
        for (int j = 1; j <= cols; j++) {
            container.addContainerProperty("prop" + j, String.class, null);
        }
        for (int i = 1; i <= rows; i++) {
            Item item = container.addItem("item" + i);
            for (int j = 1; j <= cols; j++) {
                item.getItemProperty("prop" + j).setValue(
                        "item" + i + "/prop" + j);
            }
        }
        return container;
    }

    @Override
    protected Integer getTicketNumber() {
        return 10312;
    }

    @Override
    protected String getDescription() {
        return "A Table should not show 'Internal Error' just because a column generator or container throws an exception during filling of the cache";
    }

}
