package com.vaadin.tests.components.combobox;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

public class ComboFocusBlurEvents extends TestBase {

    private int counter = 0;

    @Override
    protected void setup() {

        List<String> list = new ArrayList<String>();
        for (int i = 0; i < 100; i++) {
            list.add("Item " + i);
        }

        ComboBox cb = new ComboBox("Combobox", list);
        cb.setImmediate(true);
        cb.setInputPrompt("Enter text");
        cb.setDescription("Some Combobox");
        addComponent(cb);

        final ObjectProperty<String> log = new ObjectProperty<String>("");

        cb.addListener(new FieldEvents.FocusListener() {
            @Override
            public void focus(FocusEvent event) {
                log.setValue(log.getValue().toString() + "<br>" + counter
                        + ": Focus event!");
                counter++;
            }
        });

        cb.addListener(new FieldEvents.BlurListener() {
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
