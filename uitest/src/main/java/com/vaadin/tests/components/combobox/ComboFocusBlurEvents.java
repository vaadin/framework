package com.vaadin.tests.components.combobox;

import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.ItemDataProvider;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.ui.Label;

public class ComboFocusBlurEvents extends TestBase {

    private int counter = 0;

    @Override
    protected void setup() {

        ComboBox<String> cb = new ComboBox<>("Combobox");
        cb.setDataProvider(new ItemDataProvider(100));
        cb.setPlaceholder("Enter text");
        cb.setDescription("Some Combobox");
        addComponent(cb);

        final ObjectProperty<String> log = new ObjectProperty<>("");

        cb.addFocusListener(event -> {
            log.setValue(log.getValue().toString() + "<br>" + counter
                    + ": Focus event!");
            counter++;
        });

        cb.addBlurListener(event -> {
            log.setValue(log.getValue().toString() + "<br>" + counter
                    + ": Blur event!");
            counter++;
        });

        TextField field = new TextField("Some textfield");
        addComponent(field);

        Label output = new Label(log);
        output.setCaption("Events:");

        output.setContentMode(ContentMode.HTML);
        addComponent(output);

    }

    @Override
    protected String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        return 6536;
    }

}
