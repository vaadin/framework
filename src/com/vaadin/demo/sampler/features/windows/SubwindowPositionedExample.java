package com.vaadin.demo.sampler.features.windows;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class SubwindowPositionedExample extends VerticalLayout {

    Window subwindow;

    public SubwindowPositionedExample() {
        setSpacing(true);

        // Create the window
        subwindow = new Window("A positioned subwindow");
        // let's give it a size (optional)
        subwindow.setWidth("300px");
        subwindow.setHeight("200px");

        // Configure the windws layout; by default a VerticalLayout
        VerticalLayout layout = (VerticalLayout) subwindow.getLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        // make it fill the whole window
        layout.setSizeFull();

        // Add some content; a label and a close-button
        Label message = new Label("This is a positioned window");
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

        // Add buttons for opening the subwindow
        Button fifty = new Button("Open window at position 50x50",
                new Button.ClickListener() {
                    // inline click-listener
                    public void buttonClick(ClickEvent event) {
                        if (subwindow.getParent() == null) {
                            // Open the subwindow by adding it to the main
                            // window
                            getApplication().getMainWindow().addWindow(
                                    subwindow);
                        }

                        // Set window position
                        subwindow.setPositionX(50);
                        subwindow.setPositionY(50);
                    }
                });
        addComponent(fifty);
        Button onefifty = new Button("Open window at position 150x200",
                new Button.ClickListener() {
                    // inline click-listener
                    public void buttonClick(ClickEvent event) {
                        if (subwindow.getParent() == null) {
                            // Open the subwindow by adding it to the main
                            // window
                            getApplication().getMainWindow().addWindow(
                                    subwindow);
                        }

                        // Set window position
                        subwindow.setPositionX(150);
                        subwindow.setPositionY(200);
                    }
                });
        addComponent(onefifty);
        Button center = new Button("Open centered window",
                new Button.ClickListener() {
                    // inline click-listener
                    public void buttonClick(ClickEvent event) {
                        if (subwindow.getParent() == null) {
                            // Open the subwindow by adding it to the main
                            // window
                            getApplication().getMainWindow().addWindow(
                                    subwindow);
                        }

                        // Center the window
                        subwindow.center();
                    }
                });
        addComponent(center);

    }

}