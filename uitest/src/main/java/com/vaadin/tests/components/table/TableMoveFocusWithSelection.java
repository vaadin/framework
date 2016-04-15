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
import java.util.HashSet;
import java.util.Set;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class TableMoveFocusWithSelection extends AbstractTestUI {

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        final Table t = new Table();
        t.setImmediate(true);
        t.setId("test-table");
        t.setSizeFull();
        t.setSelectable(true);
        t.addContainerProperty("layout", VerticalLayout.class, null);
        t.addContainerProperty("string", String.class, null);

        for (int i = 0; i < 100; i++) {
            t.addItem(i);
            final VerticalLayout l = new VerticalLayout();
            l.setId("row-" + i);
            l.setHeight(20, Unit.PIXELS);
            l.setData(i);
            l.addLayoutClickListener(new LayoutClickListener() {
                @Override
                public void layoutClick(LayoutClickEvent event) {
                    if (t.isMultiSelect()) {
                        Set<Object> values = new HashSet<Object>(
                                (Set<Object>) t.getValue());
                        values.add(l.getData());
                        t.setValue(values);
                    } else {
                        t.setValue(l.getData());
                    }
                }
            });
            t.getContainerProperty(i, "layout").setValue(l);
            t.getContainerProperty(i, "string").setValue("Item #" + i);
        }
        addComponent(t);

        // Select mode
        Button toggleSelectMode = new Button(
                t.isMultiSelect() ? "Press to use single select"
                        : "Press to use multi select");
        toggleSelectMode.setId("toggle-mode");
        toggleSelectMode.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                t.setMultiSelect(!t.isMultiSelect());

                event.getButton().setCaption(
                        t.isMultiSelect() ? "Press to use single select"
                                : "Press to use multi select");
            }
        });

        addComponent(toggleSelectMode);

        Button select5210 = new Button("Select row 5-10",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        t.setValue(Arrays.asList(5, 6, 7, 8, 9, 10));
                    }
                });
        select5210.setId("select-510");
        addComponent(select5210);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Changing selection in single select mode should move focus";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 12540;
    }

}
