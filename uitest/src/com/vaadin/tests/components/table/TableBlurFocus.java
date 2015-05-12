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

import java.util.Map;

import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;

/**
 * Tests that previously focused component's blur event happens before any
 * variable changes in the focused Table.
 * 
 * @author Vaadin Ltd
 */
public class TableBlurFocus extends AbstractTestUIWithLog {

    enum Columns {
        COLUMN1, COLUMN2, COLUMN3, COLUMN4, COLUMN5
    }

    private int count = 0;
    private Button focusButton;

    @Override
    protected void setup(VaadinRequest request) {
        System.out
                .println("TableBlurFocus/TableInIframeRowClickScrollJumpTest");
        Button button = new Button("click to focus");
        button.addFocusListener(new FocusListener() {

            @Override
            public void focus(FocusEvent event) {
                log("focus");
            }
        });
        button.addBlurListener(new BlurListener() {

            @Override
            public void blur(BlurEvent event) {
                log("blur");
            }
        });
        final Button scrollButton = new Button(
                "focus lowest button to scroll down");
        scrollButton.setId("scroll-button");
        scrollButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                focusButton.focus();
            }
        });

        Label spacerLabel = new Label("spacer");
        spacerLabel.setHeight("300px");

        addComponent(button);
        addComponent(scrollButton);
        addComponent(createTable());
        addComponent(spacerLabel);
        addComponent(focusButton = new Button("for focus"));
        focusButton.setId("focus-button");
        focusButton.addFocusListener(new FocusListener() {
            @Override
            public void focus(FocusEvent event) {
                focusButton.setCaption("focused");
            }
        });
    }

    private Table createTable() {
        Table table = new Table() {
            @Override
            public void changeVariables(Object source,
                    Map<String, Object> variables) {
                log("variable change");
                super.changeVariables(source, variables);
            }
        };
        table.setSelectable(true);
        table.setImmediate(true);

        table.addContainerProperty(Columns.COLUMN1, String.class, " ");
        table.addContainerProperty(Columns.COLUMN2, Label.class, null);
        table.addContainerProperty(Columns.COLUMN3, Button.class, null);
        table.addContainerProperty(Columns.COLUMN4, String.class, " ");
        table.setColumnCollapsingAllowed(true);
        table.setColumnCollapsible(Columns.COLUMN4, true);
        table.setColumnCollapsed(Columns.COLUMN4, true);
        table.setSortEnabled(true);
        table.setFooterVisible(true);
        table.setPageLength(14);
        table.addGeneratedColumn(Columns.COLUMN5, new Table.ColumnGenerator() {

            @Override
            public Object generateCell(Table source, Object itemId,
                    Object columnId) {
                return "Generated";
            }
        });

        table.setColumnHeader(Columns.COLUMN1, "Column");
        for (int x = 0; x < 120; x++) {
            final Label buttonLabel = new Label("Not clicked");
            Button button = new Button("Click me?", new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    ++count;
                    buttonLabel.setValue("Clicked " + count + " times");
                    Notification.show("Clicked!");
                }
            });
            table.addItem(new Object[] { "entryString" + x, buttonLabel,
                    button, " " }, "entryID" + x);
        }
        return table;
    }

    @Override
    protected String getTestDescription() {
        return "Click button to focus, then click Table header. Blur event should arrive before the next variable change.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 15294;
    }

}
