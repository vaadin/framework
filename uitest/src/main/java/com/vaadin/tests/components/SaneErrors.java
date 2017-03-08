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
package com.vaadin.tests.components;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.v7.event.ItemClickEvent.ItemClickListener;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.RowHeaderMode;

public class SaneErrors extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Button b = new Button("Show me my NPE!");
        b.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                throwError();
            }

        });

        /*
         * Errors from "legacy variable changes"
         */
        final Table table = new Table();
        table.addItem("Show me my NPE!");
        table.setRowHeaderMode(RowHeaderMode.ID);
        table.addItemClickListener(new ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                throwError();
            }
        });

        final VerticalLayout content = new VerticalLayout(b, table);

        /**
         * Button that shows reported exception for TB integration test
         */
        Button button = new Button("Collect exceptions", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                reportException(b, content);
                reportException(table, content);
            }

            private void reportException(final AbstractComponent b,
                    final VerticalLayout content) {
                String message = b.getErrorMessage().getFormattedHtmlMessage();
                message = message.replaceAll("&#46;", ".");
                message = message.substring(message.indexOf("h2>") + 3,
                        message.indexOf("&#10;"));
                Label label = new Label(message);
                content.addComponent(label);
            }
        });
        content.addComponent(button);

        setContent(content);

    }

    private void throwError() {
        Object o = null;
        o.getClass();
    }

    @Override
    protected String getTestDescription() {
        return "Vaadin should by default report exceptions relevant for the developer.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11599;
    }

}
