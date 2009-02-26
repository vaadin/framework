package com.itmill.toolkit.demo.sampler.features.windows;

import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Window.CloseEvent;

public class NativeWindowExample extends VerticalLayout {

    Window window;

    public NativeWindowExample() {

        // Create the window
        window = new Window("Automatically sized subwindow");
        // Native windows should be explicitly removed from the application
        // when appropriate; in this case when closed:
        window.addListener(new Window.CloseListener() {
            // inline close-listener
            public void windowClose(CloseEvent e) {
                getApplication().removeWindow(window);
            }
        });

        // Configure the windows layout; by default a VerticalLayout
        VerticalLayout layout = (VerticalLayout) window.getLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        // make it undefined for auto-sizing window
        layout.setSizeUndefined();

        // Add some content;
        window.addComponent(new Label("This is is native (browser) window."));

        // Add a button for opening the window
        Button open = new Button("Open native window",
                new Button.ClickListener() {
                    // inline click-listener
                    public void buttonClick(ClickEvent event) {
                        getApplication().addWindow(window);
                        getApplication().getMainWindow().open(
                                new ExternalResource(window.getURL()),
                                "NativeWindowExample", 500, 500,
                                Window.BORDER_NONE);
                    }
                });
        addComponent(open);

    }

}