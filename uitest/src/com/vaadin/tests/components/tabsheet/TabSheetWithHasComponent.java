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
package com.vaadin.tests.components.tabsheet;

import com.vaadin.data.Item;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.PopupView.Content;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.VerticalLayout;

public class TabSheetWithHasComponent extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        TabSheet ts = new TabSheet();
        ts.addComponent(createPopupView(createSmallTabSheet()));
        ts.addComponent(createTable());
        ts.addComponent(createPanel());
        ts.addComponent(new CustomComponent(new Panel(
                "Panel in custom component", new Button("In panel"))));

        addComponent(ts);
    }

    private PopupView createPopupView(final Component content) {
        PopupView pv = new PopupView(new Content() {

            @Override
            public String getMinimizedValueAsHTML() {
                return "foo";
            }

            @Override
            public Component getPopupComponent() {
                return content;
            }

        });
        pv.setCaption("A popup view");
        return pv;
    }

    protected TabSheet createSmallTabSheet() {
        return new TabSheet(new Button("Tab1 inside popup"), new NativeButton(
                "Tab 2 inside popup"));
    }

    private Panel createPanel() {
        return new Panel("Panel containing stuff", new VerticalLayout(
                new Label("A Label"), new Button("A button")));
    }

    private Table createTable() {
        Table t = new Table("A table");
        t.addContainerProperty("Column 1", String.class, "");
        t.addContainerProperty("Column 2", Integer.class, null);
        t.addGeneratedColumn("Generated", new ColumnGenerator() {

            @Override
            public Object generateCell(Table source, Object itemId,
                    Object columnId) {
                if ("Item 2".equals(itemId)) {
                    return createPopupView(createSmallTabSheet());
                } else {
                    return createPopupView(createPanel());
                }
            }
        });
        t.setPageLength(5);
        for (int i = 1; i <= 5; i++) {
            Item item = t.addItem("Item " + i);
            item.getItemProperty("Column 1").setValue("Item " + i + " col 1");
            item.getItemProperty("Column 2").setValue(i);
        }
        return t;
    }

    @Override
    protected String getTestDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
