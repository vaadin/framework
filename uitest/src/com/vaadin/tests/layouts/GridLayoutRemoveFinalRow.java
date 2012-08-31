package com.vaadin.tests.layouts;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;

/**
 * Tests removing rows from a GridLayout
 */
@SuppressWarnings("serial")
public class GridLayoutRemoveFinalRow extends TestBase {

    @Override
    protected void setup() {
        getLayout().setSpacing(true);

        final GridLayout layout = new GridLayout(2, 2);
        layout.setSpacing(true);
        layout.addComponent(new Label("Label1"));
        layout.addComponent(new Label("Label2"));
        layout.addComponent(new Label("Label3"));
        layout.addComponent(new Label("Label4"));
        addComponent(layout);

        Button removeRowBtn = new Button("Remove row",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        layout.removeRow(0);
                    }
                });
        addComponent(removeRowBtn);
    }

    @Override
    protected String getDescription() {
        return "Removing last row of a GridLayout throws a IllegalArgumentException";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4542;
    }

}
