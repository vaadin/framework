package com.vaadin.tests.push;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.vaadin.annotations.Widgetset;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;

@Widgetset(TestingWidgetSet.NAME)
public class BasicPush extends AbstractTestUI {

    private ObjectProperty<Integer> counter = new ObjectProperty<Integer>(0);

    private ObjectProperty<Integer> counter2 = new ObjectProperty<Integer>(0);

    private final Timer timer = new Timer(true);

    private final TimerTask task = new TimerTask() {

        @Override
        public void run() {
            access(new Runnable() {
                @Override
                public void run() {
                    counter2.setValue(counter2.getValue() + 1);
                }
            });
        }
    };

    @Override
    protected void setup(VaadinRequest request) {

        spacer();

        /*
         * Client initiated push.
         */
        Label lbl = new Label(counter);
        lbl.setCaption("Client counter (click 'increment' to update):");
        addComponent(lbl);

        addComponent(new Button("Increment", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                counter.setValue(counter.getValue() + 1);
            }
        }));

        spacer();

        /*
         * Server initiated push.
         */
        lbl = new Label(counter2);
        lbl.setCaption("Server counter (updates each 3s by server thread) :");
        addComponent(lbl);

        addComponent(new Button("Reset", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                counter2.setValue(0);
            }
        }));
    }

    @Override
    protected String getTestDescription() {
        return "This test tests the very basic operations of push. "
                + "It tests that client initiated changes are "
                + "recieved back to the client as well as server "
                + "initiated changes are correctly updated to the client.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11494;
    }

    private void spacer() {
        addComponent(new Label("<hr/>", ContentMode.HTML));
    }

    @Override
    public void attach() {
        super.attach();
        timer.scheduleAtFixedRate(task, new Date(), 3000);
    }

    @Override
    public void detach() {
        super.detach();
        timer.cancel();
    }
}
