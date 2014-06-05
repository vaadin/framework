/*
 * Copyright 2000-2013 Vaadin Ltd.
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

import com.vaadin.annotations.Push;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;

/**
 * Test to see if VScrollTable handles Push updates correctly.
 * 
 * @author Vaadin Ltd
 */
@Push
public class AsyncPushUpdates extends AbstractTestUI {

    public int clickCount = 0;

    public static final String VALUE_PROPERTY_ID = "value";

    private final IndexedContainer container = createContainer();
    private final Table table = new Table();

    @Override
    public void setup(VaadinRequest request) {
        table.setWidth("100%");
        table.setContainerDataSource(container);

        Button button = new Button("START");
        button.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                ++clickCount;

                container.removeAllItems();
                for (int i = 0; i < 100; i++) {
                    container.getContainerProperty(container.addItem(),
                            VALUE_PROPERTY_ID).setValue("A" + i);
                }

                Runnable generateNewRows = new Runnable() {
                    public int id = 0;

                    @Override
                    public void run() {
                        getSession().lock();
                        try {
                            Thread.sleep(500);
                            ++id;
                            container.removeAllItems();
                            for (int i = 0; i < 11; i++) {
                                container.getContainerProperty(
                                        container.addItem(), VALUE_PROPERTY_ID)
                                        .setValue(
                                                clickCount + " - " + id + " - "
                                                        + i);
                            }

                        } catch (InterruptedException e) {
                            // NOOP
                        } finally {
                            getSession().unlock();
                        }
                    }
                };
                new Thread(generateNewRows).start();
            }
        });
        addComponent(table);
        addComponent(button);
    }

    private static IndexedContainer createContainer() {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty(VALUE_PROPERTY_ID, String.class, "");
        return container;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Make sure there are no duplicates on the table.";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 13562;
    }

}