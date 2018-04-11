package com.vaadin.tests.components.ui;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class WindowAndUIShortcuts extends UI {

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setMargin(true);

        final VerticalLayout mainLayout = new VerticalLayout();

        mainLayout.addComponent(new Button("Show page", event -> {
            final VerticalLayout pageLayout = new VerticalLayout();
            pageLayout.setSpacing(true);

            pageLayout.addComponent(new Label("Page"));
            pageLayout.addComponent(
                    new Button("Open dialog window", clickEvent -> {
                        Window dialog = new Window();
                        dialog.setModal(true);
                        dialog.setCaption("Press ESC shortcut");
                        dialog.setWidth("300px");
                        dialog.setHeight("100px");

                        dialog.setContent(new Button("Button in window"));
                        addWindow(dialog);
                    }));
            Button closeButton = new Button("Close page", clickEvent -> {
                mainLayout.removeComponent(pageLayout);

                Notification.show("OMG! Page is also closed!");
            });
            closeButton.setClickShortcut(ShortcutAction.KeyCode.ESCAPE);
            pageLayout.addComponent(closeButton);

            mainLayout.addComponent(pageLayout);
            mainLayout.setExpandRatio(pageLayout, 1);
        }));

        layout.addComponent(mainLayout);
        layout.setExpandRatio(mainLayout, 1);

        setContent(layout);
    }
}
