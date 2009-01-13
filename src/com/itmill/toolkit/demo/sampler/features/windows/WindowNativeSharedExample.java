package com.itmill.toolkit.demo.sampler.features.windows;

import java.net.URL;

import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Window.CloseEvent;

public class WindowNativeSharedExample extends VerticalLayout implements
        Window.CloseListener {

    private URL nativeWindowURL = null;
    private Button b1;
    private Button b2;
    private Label l;

    public WindowNativeSharedExample() {
        setSpacing(true);

        b1 = new Button("Create/open a new native window with shared state",
                this, "openButtonClick");
        addComponent(b1);

        l = new Label("Amount of windows in memory:");
        addComponent(l);

        b2 = new Button("Refresh", this, "refreshButtonClick");
        addComponent(b2);
    }

    public void openButtonClick(ClickEvent event) {
        if (nativeWindowURL == null) {
            final Window w = new Window("Native subwindow");
            final Label desc = new Label(
                    "This is a new native window with a shared state."
                            + " You'll notice that even if you open this several"
                            + " times, the URL will always be the same, and the"
                            + " amount of windows in memory will not increase. "
                            + " The window is added to the application and then"
                            + " opened through its unique URL.");
            w.addComponent(desc);
            w.addListener(this);
            getApplication().addWindow(w);
            nativeWindowURL = w.getURL();
        }
        getApplication().getMainWindow().open(
                new ExternalResource(nativeWindowURL), "_blank");
    }

    public void refreshButtonClick(ClickEvent event) {
        l.setValue("Amount of windows in memory: "
                + getApplication().getWindows().size());
    }

    public void windowClose(CloseEvent e) {
        // In this example, the window will not be removed after closing,
        // in order to preserve the window's URL functional. Normally you would
        // remove the window after closing as follows:
        // getApplication().removeWindow(e.getWindow());

        System.err
                .println("Sampler->WindowNativeSharedExample: Window close event");
    }

}