package com.itmill.toolkit.demo.sampler.features.windows;

import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Window.CloseEvent;

public class SubwindowCloseExample extends VerticalLayout {

    Window subwindow;

    public SubwindowCloseExample() {

        // Create the window
        subwindow = new Window("A subwindow w/ close-listener");
        subwindow.addListener(new Window.CloseListener() {
            // inline close-listener
            public void windowClose(CloseEvent e) {
                getApplication().getMainWindow().showNotification(
                        "Window closed");
            }
        });

        // Configure the windws layout; by default a VerticalLayout
        VerticalLayout layout = (VerticalLayout) subwindow.getLayout();
        layout.setMargin(true);
        layout.setSpacing(true);

        // Add some content; a label and a close-button
        Label message = new Label("This is a subwindow with a close-listener.");
        subwindow.addComponent(message);

        // Add a button for opening the subwindow
        Button open = new Button("Open window", new Button.ClickListener() {
            // inline click-listener
            public void buttonClick(ClickEvent event) {
                if (subwindow.getParent() != null) {
                    // window is already showing
                    getWindow().showNotification("Window is already open");
                } else {
                    // Open the subwindow by adding it to the main window
                    getApplication().getMainWindow().addWindow(subwindow);
                }
            }
        });
        addComponent(open);

    }

}