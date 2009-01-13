package com.itmill.toolkit.demo.sampler.features.windows;

import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Window.CloseEvent;

public class WindowNativeNewExample extends VerticalLayout implements
        Window.CloseListener {

    private Button b1;
    private Button b2;
    private Label l;

    public WindowNativeNewExample() {
        setSpacing(true);

        b1 = new Button(
                "Create and open a new native window with its own state", this,
                "openButtonClick");
        addComponent(b1);

        l = new Label("Amount of windows in memory:");
        addComponent(l);

        b2 = new Button("Refresh", this, "refreshButtonClick");
        addComponent(b2);
    }

    public void openButtonClick(ClickEvent event) {
        Window w = new Window("Native subwindow");
        Label desc = new Label(
                "This is a new native window with its own state."
                        + " You'll notice that if you open a new window several"
                        + " times, the URL will be unique for each window, and the"
                        + " amount of windows in memory will increase by one."
                        + " When you close this window, the amount of windows in"
                        + " memory should decrease by one."
                        + " The window is added to the application and then"
                        + " opened through its unique URL.");
        w.addComponent(desc);
        w.addListener(this);
        getApplication().addWindow(w);
        getApplication().getMainWindow().open(new ExternalResource(w.getURL()),
                "_blank");
    }

    public void refreshButtonClick(ClickEvent event) {
        l.setValue("Amount of windows in memory: "
                + getApplication().getWindows().size());
    }

    public void windowClose(CloseEvent e) {
        // In this example, the window will be removed after closing,
        getApplication().removeWindow(e.getWindow());
        System.err
                .println("Sampler->WindowNativeNewExample: Window close event");
    }

}