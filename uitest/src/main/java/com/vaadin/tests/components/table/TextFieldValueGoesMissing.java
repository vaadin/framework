package com.vaadin.tests.components.table;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class TextFieldValueGoesMissing extends AbstractTestUI {

    @SuppressWarnings("unchecked")
    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout verticalLayout = new VerticalLayout();

        final Label label1 = new Label("1");
        final Label label2 = new Label("2");

        Button button = new Button("Replace label");
        button.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                if (verticalLayout.getComponentIndex(label1) > -1) {
                    verticalLayout.replaceComponent(label1, label2);
                } else {
                    verticalLayout.replaceComponent(label2, label1);
                }
            }
        });
        verticalLayout.addComponent(button);
        verticalLayout.addComponent(label1);

        Table table = new Table();
        table.addContainerProperty("Field", TextField.class, null);
        Object id = table.addItem();
        TextField tf = new TextField();
        table.getItem(id).getItemProperty("Field").setValue(tf);

        verticalLayout.addComponent(table);

        addComponent(verticalLayout);

    }

    @Override
    protected String getTestDescription() {
        return "Enter a text in the TextField in the table and press the 'Replace label' button. This replaces the label which is in the same layout as the table but should not cause the TextField in the table to lose its contents";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6902;
    }

}
