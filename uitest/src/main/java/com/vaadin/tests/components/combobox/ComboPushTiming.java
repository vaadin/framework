package com.vaadin.tests.components.combobox;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.vaadin.server.VaadinSession;
import com.vaadin.tests.components.TestBase;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.ProgressIndicator;
import com.vaadin.v7.ui.TextField;

public class ComboPushTiming extends TestBase {

    private int counter = 0;
    private final MyExecutor executor = new MyExecutor();

    @Override
    protected void setup() {

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add("Item " + i);
        }

        final ComboBox cb = new ComboBox("Combobox", list);
        cb.setImmediate(true);
        cb.setInputPrompt("Enter text");
        cb.setDescription("Some Combobox");
        addComponent(cb);

        final ObjectProperty<String> log = new ObjectProperty<>("");

        cb.addFocusListener(event -> {
            log.setValue(log.getValue() + "<br>" + counter + ": Focus event!");
            counter++;
            changeValue(cb);
        });

        cb.addBlurListener(event -> {
            log.setValue(log.getValue() + "<br>" + counter + ": Blur event!");
            counter++;
        });

        TextField field = new TextField("Some textfield");
        field.setImmediate(true);
        addComponent(field);

        Label output = new Label(log);
        output.setCaption("Events:");

        output.setContentMode(ContentMode.HTML);
        addComponent(output);

        ProgressIndicator progressIndicator = new ProgressIndicator();
        addComponent(progressIndicator);
        progressIndicator.setPollingInterval(3000);
    }

    private void changeValue(final ComboBox cb) {
        executor.execute(() -> {
            VaadinSession.getCurrent().lock();
            try {
                cb.setEnabled(true);
                cb.setValue("B");
                cb.setEnabled(true);

                // If this isn't sent by push or poll in the background, the
                // problem will go away
            } finally {
                VaadinSession.getCurrent().unlock();
            }
        });
    }

    class MyExecutor extends ThreadPoolExecutor {
        public MyExecutor() {
            super(5, 20, 20, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>());
        }
    }

    @Override
    protected String getDescription() {
        return "When an update is received while the popup is open, the suggestion popup blurs away";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10924;
    }

}
