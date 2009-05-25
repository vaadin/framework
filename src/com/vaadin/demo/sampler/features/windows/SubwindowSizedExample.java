package com.vaadin.demo.sampler.features.windows;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class SubwindowSizedExample extends VerticalLayout {

    Window subwindow;

    public SubwindowSizedExample() {

        // Create the window
        subwindow = new Window("A sized subwindow");
        subwindow.setWidth("500px");
        subwindow.setHeight("80%");

        // Configure the windws layout; by default a VerticalLayout
        VerticalLayout layout = (VerticalLayout) subwindow.getContent();
        layout.setMargin(true);
        layout.setSpacing(true);
        // make it fill the whole window
        layout.setSizeFull();

        // Add some content; a label and a close-button
        Label message = new Label("This is a sized window");
        subwindow.addComponent(message);

        Button close = new Button("Close", new Button.ClickListener() {
            // inline click-listener
            public void buttonClick(ClickEvent event) {
                // close the window by removing it from the main window
                getApplication().getMainWindow().removeWindow(subwindow);
            }
        });
        // The components added to the window are actually added to the window's
        // layout; you can use either. Alignments are set using the layout
        layout.addComponent(close);
        layout.setComponentAlignment(close, "right bottom");

        // Add a button for opening the subwindow
        Button open = new Button("Open sized window",
                new Button.ClickListener() {
                    // inline click-listener
                    public void buttonClick(ClickEvent event) {
                        if (subwindow.getParent() != null) {
                            // window is already showing
                            getWindow().showNotification(
                                    "Window is already open");
                        } else {
                            // Open the subwindow by adding it to the main
                            // window
                            getApplication().getMainWindow().addWindow(
                                    subwindow);
                        }
                    }
                });
        addComponent(open);

    }

}