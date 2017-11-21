package com.vaadin.tests.components.textfield;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.ui.TextField;

@SuppressWarnings("unchecked")
public class TextFieldWithProperty extends TestBase {

    @Override
    protected void setup() {

        final TextField tf1 = new TextField();

        final ObjectProperty<String> op = new ObjectProperty<>("FOO");

        tf1.setPropertyDataSource(op);

        addComponent(tf1);

        Button b = new Button(
                "Set BAR to underlaying property (should propagate to UI)");
        b.addClickListener(event -> op.setValue("BAR"));
        addComponent(b);
    }

    @Override
    protected String getDescription() {
        return "Should work";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6588;
    }

}
