package com.vaadin.tests.push;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

public class PushConfiguration extends AbstractReindeerTestUI {

    private int counter = 0;
    private int counter2 = 0;
    private final Timer timer = new Timer(true);

    private final TimerTask task = new TimerTask() {

        @Override
        public void run() {
            access(() -> {
                counter2++;
                serverCounterLabel.setValue("" + counter2);
            });
        }
    };
    private Label serverCounterLabel;

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new PushConfigurator(this));
        spacer();

        /*
         * Client initiated push.
         */
        Label clientCounterLabel = new Label("0");
        clientCounterLabel
                .setCaption("Client counter (click 'increment' to update):");
        addComponent(clientCounterLabel);

        addComponent(new Button("Increment",
                event -> clientCounterLabel.setValue("" + counter++)));

        spacer();

        serverCounterLabel = new Label(String.valueOf(counter2));
        serverCounterLabel.setCaption(
                "Server counter (updates each 1s by server thread) :");
        addComponent(serverCounterLabel);

        addComponent(new Button("Reset", event ->{
            counter2 = 0;
            serverCounterLabel.setValue("0");
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
        timer.scheduleAtFixedRate(task, new Date(), 1000);
    }

    @Override
    public void detach() {
        super.detach();
        timer.cancel();
    }
}
