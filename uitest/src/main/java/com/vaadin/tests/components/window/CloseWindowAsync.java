package com.vaadin.tests.components.window;

import java.util.concurrent.CompletableFuture;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.PushStateNavigation;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.UIDetachedException;
import com.vaadin.ui.Window;

@Theme("tests-valo-disabled-animations")
@PreserveOnRefresh
@Push(value = PushMode.MANUAL, transport = Transport.LONG_POLLING)
@PushStateNavigation
public class CloseWindowAsync extends AbstractTestUIWithLog {
    private final boolean manualPush;

    public CloseWindowAsync() {
        this(true);
    }

    public CloseWindowAsync(boolean manualPush) {
        this.manualPush = manualPush;
    }

    @Override
    protected void setup(VaadinRequest request) {
        final Button button = new Button("Open and directly close busy window");
        button.addClickListener((Button.ClickListener) event -> {
            final Window window = createWindow(1);
            final UI ui = getUI();
            ui.addWindow(window);

            CompletableFuture.runAsync(() -> {
                // task duration variable, could be a few ms or longer
                ui.accessSynchronously(() -> {
                    window.close();
                    if (manualPush) {
                        ui.push();
                    }
                });
            });
        });
        addComponents(button);

        final Button button2 = new Button(
                "Open and directly close busy window with error notification");
        button2.addClickListener((Button.ClickListener) event -> {
            final Window window = createWindow(2);
            final UI ui = getUI();
            ui.addWindow(window);

            CompletableFuture.runAsync(() -> {
                // task duration variable, could be a few ms or longer
                ui.accessSynchronously(() -> {
                    window.close();
                    Notification.show("error", Notification.Type.ERROR_MESSAGE);
                    if (manualPush) {
                        ui.push();
                    }
                });
            });
        });
        addComponents(button2);

        // Reconstructed the issue using the vaadin push training
        // https://vaadin.com/learn/training/vaadin-push

        final Button button3 = new Button(
                "Open and directly close busy window (vaadin push training)");
        button3.addClickListener((Button.ClickListener) event -> {
            final Window window = createWindow(3);
            final UI ui = getUI();
            ui.addWindow(window);

            CompletableFuture.runAsync(() -> {
                // task duration variable, could be a few ms or longer
                try {
                    ui.access(() -> {
                        ui.removeWindow(window);
                        if (manualPush) {
                            ui.push();
                        }
                    });
                } catch (UIDetachedException e) {
                    // browser closed
                }
            });
        });
        addComponents(button3);
    }

    private Window createWindow(int index) {
        final Window window = new Window();
        window.setCaption("Window");
        window.setWidth(30, Unit.PERCENTAGE);
        window.setHeight(30, Unit.PERCENTAGE);
        window.setModal(true);
        window.setContent(new Label("Window content"));
        window.addCloseListener(e -> log("closed " + index));
        return window;
    }

    @Override
    protected Integer getTicketNumber() {
        return 11942;
    }

    @Override
    protected String getTestDescription() {
        return "All buttons should successfully open and close window on all browsers.";
    }
}