package com.itmill.toolkit.demo.sampler.features.windows;

import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Window.CloseEvent;

public class WindowChildScrollableExample extends VerticalLayout implements
        Window.CloseListener {

    private Button b1;
    private Button b2;
    private Label l;

    public WindowChildScrollableExample() {
        setSpacing(true);

        b1 = new Button("Create and open a scrollable child window", this,
                "openButtonClick");
        addComponent(b1);

        l = new Label("Amount of child windows attached to the main window: x");
        addComponent(l);

        b2 = new Button("Refresh", this, "refreshButtonClick");
        addComponent(b2);
    }

    public void openButtonClick(ClickEvent event) {
        Window w = new Window("Scroll");
        Label desc = new Label(
                "This is a new child window with a preset"
                        + " width, height and position. Resizing has been"
                        + " disabled for this window. Additionally, this text label"
                        + " is intentionally too large to fit the window. You can"
                        + " use the scrollbars to view different parts of the window content.");
        w.addComponent(desc);
        w.addListener(this);

        // Set window position
        w.setPositionX(300);
        w.setPositionY(300);

        // Set window size
        w.setWidth(170, UNITS_PIXELS);
        w.setHeight(100, UNITS_PIXELS);

        // Disable resizing
        w.setResizable(false);

        getApplication().getMainWindow().addWindow(w);
    }

    public void refreshButtonClick(ClickEvent event) {
        l.setValue("Amount of child windows attached to the main window: "
                + getApplication().getMainWindow().getChildWindows().size());
    }

    public void windowClose(CloseEvent e) {
        // In this example, the window will be removed after closing
        getApplication().getMainWindow().removeWindow(e.getWindow());
        System.err
                .println("Sampler->WindowChildScrollableExample: Window close event");
    }

}