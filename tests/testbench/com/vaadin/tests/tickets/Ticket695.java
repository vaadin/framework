package com.vaadin.tests.tickets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Root;

@SuppressWarnings("serial")
public class Ticket695 extends Application.LegacyApplication {

    @Override
    public void init() {
        final Root w = new Root("Serialization test #695");
        setMainWindow(w);
        Button b = new Button("Serialize ApplicationContext");
        w.addComponent(b);
        b.addListener(new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(buffer);
                    long t = System.currentTimeMillis();
                    oos.writeObject(getContext());
                    w.showNotification("ApplicationContext serialized ("
                            + buffer.size() + "bytes) in "
                            + (System.currentTimeMillis() - t) + "ms");
                } catch (IOException e) {
                    e.printStackTrace();
                    w.showNotification("ApplicationContext serialization failed - see console for stacktrace");
                }

            }
        });
    }

}
