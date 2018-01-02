package com.vaadin.tests.core;

import com.vaadin.launcher.CustomDeploymentConfiguration;
import com.vaadin.launcher.CustomDeploymentConfiguration.Conf;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@CustomDeploymentConfiguration({
        @Conf(name = "heartbeatInterval", value = "2") })
public class LockingUI extends UI {

    public static final String LOCKING_ENDED = "Locking has ended";
    public static final String ALL_OK = "All is fine";

    @Override
    protected void init(VaadinRequest request) {
        Button lockButton = new Button("Lock UI for too long",
                new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        int heartbeatInterval = VaadinService.getCurrent()
                                .getDeploymentConfiguration()
                                .getHeartbeatInterval();
                        try {
                            // Wait for 4 heartbeats
                            long timeout = heartbeatInterval * 1000;
                            for (int i = 0; i < 4; ++i) {
                                Thread.sleep(timeout);
                            }

                        } catch (InterruptedException e1) {
                            throw new RuntimeException(
                                    "Timeout should not get interrupted.");
                        }
                        Notification.show(LOCKING_ENDED,
                                Type.TRAY_NOTIFICATION);
                    }
                });
        Button checkButton = new Button("Test communication",
                new ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        Notification.show(ALL_OK, Type.TRAY_NOTIFICATION);
                    }
                });

        lockButton.setId("lock");
        checkButton.setId("check");

        setContent(new VerticalLayout(lockButton, checkButton));
    }

}
