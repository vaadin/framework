package com.vaadin.tests.minitutorials.broadcastingmessages;

import com.vaadin.annotations.Push;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.minitutorials.broadcastingmessages.Broadcaster.BroadcastListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.TextArea;

@Push
public class BroadcasterUI extends UI implements BroadcastListener {

    @Override
    protected void init(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        final TextArea message = new TextArea("",
                "The system is going down for maintenance in 10 minutes");
        layout.addComponent(message);

        final Button button = new Button("Broadcast");
        layout.addComponent(button);
        button.addClickListener(
                event -> Broadcaster.broadcast(message.getValue()));

        // Register broadcast listener
        Broadcaster.register(this);
    }

    @Override
    public void detach() {
        Broadcaster.unregister(this);
        super.detach();
    }

    @Override
    public void receiveBroadcast(final String message) {
        access(() -> {
            Notification n = new Notification("Message received", message,
                    Type.TRAY_NOTIFICATION);
            n.show(getPage());
        });
    }

}
