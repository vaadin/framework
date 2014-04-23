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
package com.vaadin.tests.components.window;

import com.vaadin.data.Item;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class CloseModalSubWindow extends AbstractTestUIWithLog {

    public static final String SUB_WINDOW = "sub-win";
    public static final String TREE_TABLE = "treetable";
    public static final String DELETE_BUTTON = "del-btn";
    public static final String CONFIRM_BUTTON = "confirm-btn";

    private ConfirmWindow win;

    private TreeTable table;

    @Override
    protected String getTestDescription() {
        return "Lists a dozen items in a TreeTable with a Delete Button in each row. "
                + "Delete button will open an sub-window allowing user to either confirm delete operation or cancel. "
                + "Confirming should close the sub-window at the same time as the item is removed from the TreeTable.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13188;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void setup(VaadinRequest request) {
        table = new TreeTable();
        table.setId(TREE_TABLE);
        table.addContainerProperty("column", String.class, null);
        table.addContainerProperty("delete", Button.class, null);

        for (int i = 0; i < 10; i++) {
            Item item = table.addItem(i);
            item.getItemProperty("column").setValue("" + i);

            Button b = new Button("Delete", deleteClickListener);
            b.setId(DELETE_BUTTON + i);
            b.setData(i);
            item.getItemProperty("delete").setValue(b);
        }

        table.setSortEnabled(false);
        table.setColumnReorderingAllowed(false);
        table.setEditable(true);
        table.setPageLength(0);

        addComponent(table);

    }

    private void deleteItem(Object itemId) {
        table.removeItem(itemId);
    }

    private ClickListener deleteClickListener = new ClickListener() {

        @Override
        public void buttonClick(ClickEvent event) {
            win = new ConfirmWindow(event.getButton().getData());
            log("Modal sub-window opened");
        }
    };

    private ClickListener confirmClickListener = new ClickListener() {

        @Override
        public void buttonClick(ClickEvent event) {
            deleteItem(event.getButton().getData());
            win.close();
            log("Modal sub-window closed");
        }
    };

    private ClickListener cancelClickListener = new ClickListener() {

        @Override
        public void buttonClick(ClickEvent event) {
            win.close();
            log("Modal sub-window closed");
        }
    };

    /** Modal confirmation sub-window. */
    class ConfirmWindow extends Window {

        public ConfirmWindow(Object itemId) {
            setModal(true);
            setWidth("300px");
            setHeight("200px");
            setId(SUB_WINDOW);

            Button ok = new Button("Confirm Delete", confirmClickListener);
            ok.setId(CONFIRM_BUTTON);
            ok.setData(itemId);
            Button cancel = new Button("Cancel", cancelClickListener);

            HorizontalLayout l = new HorizontalLayout();
            l.addComponent(ok);
            l.addComponent(cancel);

            setContent(l);

            UI.getCurrent().addWindow(this);
        }
    }
}
