package com.vaadin.tests.components.textfield;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TextField;

@SuppressWarnings("unchecked")
public class TextFieldWithProperty extends TestBase {

    @Override
    protected void setup() {

        final TextField tf1 = new TextField();

        final ObjectProperty<String> op = new ObjectProperty<String>("FOO");

        tf1.setPropertyDataSource(op);

        addComponent(tf1);

        Button b = new Button(
                "Set BAR to underlaying property (should propagate to UI)");
        b.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                op.setValue("BAR");
            }
        });
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
