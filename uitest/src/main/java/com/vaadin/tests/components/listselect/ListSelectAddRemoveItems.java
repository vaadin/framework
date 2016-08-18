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
package com.vaadin.tests.components.listselect;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ListSelect;

public class ListSelectAddRemoveItems extends AbstractTestUIWithLog {

    private IndexedContainer container = new IndexedContainer();

    @Override
    protected void setup(VaadinRequest request) {
        ListSelect listSelect = new ListSelect("ListSelect", container);
        listSelect.setWidth("100px");
        listSelect.setRows(10);

        resetContainer();
        logContainer();

        addComponent(listSelect);
        addComponent(new Button("Reset", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                resetContainer();
                log.clear();
                logContainer();
            }
        }));

        addComponent(new Button("Add first", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                container.addItemAt(0, "first");
                logContainer();
            }
        }));

        addComponent(new Button("Add middle", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                container.addItemAt(container.size() / 2, "middle");
                logContainer();
            }
        }));

        addComponent(new Button("Add last", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                container.addItem("last");
                logContainer();
            }
        }));

        addComponent(new Button("Swap", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Object lastItem = container.lastItemId();
                Object firstItem = container.firstItemId();
                if (lastItem != firstItem) {
                    container.removeItem(lastItem);
                    container.removeItem(firstItem);

                    container.addItemAt(0, lastItem);
                    container.addItem(firstItem);
                }

                logContainer();
            }
        }));

        addComponent(new Button("Remove first", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                container.removeItem(container.firstItemId());
                logContainer();
            }
        }));

        addComponent(new Button("Remove middle", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                container.removeItem(
                        container.getIdByIndex(container.size() / 2));
                logContainer();
            }
        }));

        addComponent(new Button("Remove last", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                container.removeItem(container.lastItemId());
                logContainer();
            }
        }));

    }

    private void logContainer() {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < container.size(); i++) {
            Object id = container.getIdByIndex(i);
            if (i != 0) {
                b.append(", ");
            }
            b.append(id);
        }

        log(b.toString());
    }

    public void resetContainer() {
        container.removeAllItems();
        for (String value : new String[] { "a", "b", "c" }) {
            container.addItem(value);
        }
    }

    @Override
    protected String getTestDescription() {
        return "Test for verifying that items are added to and removed from the correct locations";
    }

}
