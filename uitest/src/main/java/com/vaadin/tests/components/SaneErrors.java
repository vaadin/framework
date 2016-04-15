package com.vaadin.tests.components;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.RowHeaderMode;
import com.vaadin.ui.VerticalLayout;

public class SaneErrors extends AbstractTestUI {

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
