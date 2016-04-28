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

import java.util.Iterator;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;

public class ContainerSizeChangeDuringTablePaint extends AbstractTestUI {

    /**
     * A test {@link Table} that simply acts a hook for when Vaadin starts
     * painting the Table.
     */
    private static class WobblyTable extends Table {
        /**
         * A flag for the container to know when it should change the size.
         */
        boolean isBeingPainted;

        public WobblyTable(String caption, Container dataSource) {
            super(caption, dataSource);
        }

        @Override
        public void paintContent(PaintTarget target) throws PaintException {
            isBeingPainted = true;
            try {
                super.paintContent(target);
            } finally {
                isBeingPainted = false;
            }
        }
    }

    /**
     * A {@link Container} that can change its size while its
     * {@link WobblyTable} is being painted.
     */
    private static class WobblyContainer extends IndexedContainer {
        private WobblyTable table = null;
        private boolean shouldSabotageNextPaint = false;

        public void setWobblyTable(WobblyTable table) {
            this.table = table;
        }

        @Override
        public int size() {
            if (table != null && table.isBeingPainted
                    && shouldSabotageNextPaint) {
                try {
                    System.out.print("Firing item set change "
                            + "event during Table paint... ");
                    fireItemSetChange();
                    System.out.println("Success!");
                } finally {
                    shouldSabotageNextPaint = false;
                }
            }

            return super.size();
        }

        public void sabotageNextPaint() {
            shouldSabotageNextPaint = true;
        }
    }

    private static final Object PROPERTY_1 = new Object();
    private static final Object PROPERTY_2 = new Object();
    private static final Object PROPERTY_3 = new Object();

    @Override
    protected void setup(VaadinRequest request) {
        final WobblyContainer container = generateContainer();
        final WobblyTable table = createTable(container);
        container.setWobblyTable(table);

        addComponent(table);
        Button button = new Button(
                "Add an item and also trigger an ItemSetChangeEvent in Container during next Table paint",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {

                        // we need this to simply trigger a table paint.
                        addItem(container, "A", "New", "Row");
                        container.sabotageNextPaint();

                        event.getButton().setCaption(
                                "Event was fired successfully.");
                    }
                });
        button.setId("addRow");
        addComponent(button);
    }

    private static WobblyTable createTable(IndexedContainer container) {
        WobblyTable t = new WobblyTable(null, container);
        t.setColumnHeader(PROPERTY_1, "Property 1");
        t.setColumnHeader(PROPERTY_2, "Property 2");
        t.setColumnHeader(PROPERTY_3, "Property 3");
        t.setPageLength(container.size() + 1);
        return t;
    }

    private static WobblyContainer generateContainer() {
        WobblyContainer c = new WobblyContainer();
        c.addContainerProperty(PROPERTY_1, String.class, null);
        c.addContainerProperty(PROPERTY_2, String.class, null);
        c.addContainerProperty(PROPERTY_3, String.class, null);
        addItem(c, "Hello", "World", "!");
        return c;
    }

    @SuppressWarnings("unchecked")
    private static void addItem(Container c, Object... properties) {
        Object itemId = c.addItem();
        Item item = c.getItem(itemId);
        int i = 0;
        Iterator<?> propIter = c.getContainerPropertyIds().iterator();
        while (propIter.hasNext()) {
            Object propertyId = propIter.next();
            item.getItemProperty(propertyId).setValue(properties[i]);
            i++;
        }
    }

    @Override
    protected String getTestDescription() {
        return "Container changes during the painting cycle should not lead to an IllegalStateException";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12258;
    }

}
