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

package com.vaadin.tests.application.calculator;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class Calc extends AbstractTestUI {

    private class Log extends VerticalLayout {

        private Table table;
        private Button addCommentButton;
        private int line = 0;

        public Log() {
            super();

            table = new Table();
            table.setSizeFull();

            setWidth("200px");
            setHeight("100%");

            table.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
            table.addContainerProperty("Operation", String.class, "");

            addComponent(table);

            addCommentButton = new Button("Add Comment");
            addCommentButton.setWidth("100%");

            addCommentButton.addClickListener(new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {

                    final Window w = new Window("Add comment");
                    VerticalLayout vl = new VerticalLayout();
                    vl.setMargin(true);

                    final TextField tf = new TextField();
                    tf.setSizeFull();
                    vl.addComponent(tf);

                    HorizontalLayout hl = new HorizontalLayout();

                    Button okButton = new Button("OK");
                    okButton.setWidth("100%");
                    okButton.addClickListener(new ClickListener() {
                        @Override
                        public void buttonClick(ClickEvent event) {
                            addRow("[ " + tf.getValue() + " ]");
                            tf.setValue("");
                            w.close();
                            removeWindow(w);
                        }
                    });

                    Button cancelButton = new Button("Cancel");
                    cancelButton.setWidth("100%");
                    cancelButton.addClickListener(new ClickListener() {
                        @Override
                        public void buttonClick(ClickEvent event) {
                            tf.setValue("");
                            w.close();
                            removeWindow(w);
                        }
                    });

                    hl.addComponent(cancelButton);
                    hl.addComponent(okButton);
                    hl.setSpacing(true);
                    hl.setWidth("100%");

                    vl.addComponent(hl);
                    vl.setSpacing(true);

                    w.setContent(vl);
                    addWindow(w);
                }
            });

            addComponent(addCommentButton);

            setExpandRatio(table, 1);
            setSpacing(true);
        }

        public void addRow(String row) {
            Integer id = ++line;
            table.addItem(new Object[] { row }, id);
            table.setCurrentPageFirstItemIndex(line + 1);
        }

    }

    // All variables are automatically stored in the session.
    private Double current = 0.0;
    private double stored = 0.0;
    private char lastOperationRequested = 'C';
    private VerticalLayout topLayout = new VerticalLayout();

    // User interface components
    private final TextField display = new TextField();

    private final Log log = new Log();

    // Calculator "business logic" implemented here to keep the example
    // minimal
    private double calculate(char requestedOperation) {
        if ('0' <= requestedOperation && requestedOperation <= '9') {
            if (current == null) {
                current = 0.0;
            }
            current = current * 10
                    + Double.parseDouble("" + requestedOperation);
            return current;
        }

        if (current == null) {
            current = stored;
        }
        switch (lastOperationRequested) {
        case '+':
            stored += current;
            break;
        case '-':
            stored -= current;
            break;
        case '/':
            stored /= current;
            break;
        case '*':
            stored *= current;
            break;
        default:
            stored = current;
            break;
        }

        switch (requestedOperation) {
        case '+':
            log.addRow(current + " +");
            break;
        case '-':
            log.addRow(current + " -");
            break;
        case '/':
            log.addRow(current + " /");
            break;
        case '*':
            log.addRow(current + " x");
            break;
        case '=':
            log.addRow(current + " =");
            log.addRow("------------");
            log.addRow("" + stored);
            break;
        }

        lastOperationRequested = requestedOperation;
        current = null;
        if (requestedOperation == 'C') {
            log.addRow("0.0");
            stored = 0.0;
        }
        return stored;
    }

    @Override
    protected void setup(VaadinRequest request) {
        setContent(topLayout);

        // Create the main layout for our application (4 columns, 5 rows)
        final GridLayout layout = new GridLayout(4, 5);

        topLayout.setMargin(true);
        topLayout.setSpacing(true);
        Label title = new Label("Calculator");
        topLayout.addComponent(title);
        topLayout.addComponent(log);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        horizontalLayout.addComponent(layout);
        horizontalLayout.addComponent(log);
        topLayout.addComponent(horizontalLayout);

        // Create a result label that over all 4 columns in the first row
        layout.setSpacing(true);
        layout.addComponent(display, 0, 0, 3, 0);
        layout.setComponentAlignment(display, Alignment.MIDDLE_RIGHT);
        display.setSizeFull();
        display.setId("display");
        display.setValue("0.0");

        // The operations for the calculator in the order they appear on the
        // screen (left to right, top to bottom)
        String[] operations = new String[] { "7", "8", "9", "/", "4", "5", "6",
                "*", "1", "2", "3", "-", "0", "=", "C", "+" };

        for (String caption : operations) {

            // Create a button and use this application for event handling
            Button button = new Button(caption);
            button.setWidth("40px");
            button.addClickListener(new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    // Get the button that was clicked
                    Button button = event.getButton();

                    // Get the requested operation from the button caption
                    char requestedOperation = button.getCaption().charAt(0);

                    // Calculate the new value
                    double newValue = calculate(requestedOperation);

                    // Update the result label with the new value
                    display.setValue("" + newValue);
                }
            });
            button.setId("button_" + caption);

            // Add the button to our main layout
            layout.addComponent(button);
        }

    }

    @Override
    protected String getTestDescription() {
        return "Provide test application for generic testing purposes";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12444;
    }

}
