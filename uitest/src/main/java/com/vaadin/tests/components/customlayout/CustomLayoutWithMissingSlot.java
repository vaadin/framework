package com.vaadin.tests.components.customlayout;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.v7.ui.TextField;

public class CustomLayoutWithMissingSlot extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        CustomLayout cl;
        try {
            cl = new CustomLayout(new ByteArrayInputStream(
                    "<div>First: <div location='first'></div><p>Second: <div location='second'></div><p>"
                            .getBytes(UTF_8)));
            cl.addComponent(new TextField("This should be visible"), "first");
            Button button = new Button(
                    "This button is visible, together with one label");
            button.addClickListener(event -> log("Button clicked"));
            cl.addComponent(button, "second");
            cl.addComponent(
                    new TextField("This won't be as the slot is missing"),
                    "third");

            addComponent(cl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
