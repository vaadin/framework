package com.vaadin.tests.components.combobox;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.tests.components.TestBase;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.TextField;

public class ComboFocusBlurEvents extends TestBase {

    private int counter = 0;

    @Override
    protected void setup() {

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add("Item " + i);
        }

        ComboBox cb = new ComboBox("Combobox", list);
        cb.setImmediate(true);
        cb.setInputPrompt("Enter text");
        cb.setDescription("Some Combobox");
        addComponent(cb);

        final ObjectProperty<String> log = new ObjectProperty<>("");

        cb.addFocusListener(new FieldEvents.FocusListener() {
            @Override
            public void focus(FocusEvent event) {
                log.setValue(log.getValue().toString() + "<br>" + counter
                        + ": Focus event!");
                counter++;
            }
        });

        cb.addBlurListener(new FieldEvents.BlurListener() {
            @Override
            public void blur(BlurEvent event) {
                log.setValue(log.getValue().toString() + "<br>" + counter
                        + ": Blur event!");
                counter++;
            }
        });

        TextField field = new TextField("Some textfield");
        field.setImmediate(true);
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
