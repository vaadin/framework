package com.itmill.toolkit.demo.sampler.features.windows;

import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Window.CloseEvent;

public class WindowChildExample extends VerticalLayout implements
        Window.CloseListener {

    private Button b1;
    private Button b2;
    private Label l;

    public WindowChildExample() {
        setSpacing(true);

        b1 = new Button(
                "Create and open a new child window with its own state", this,
                "openButtonClick");
        addComponent(b1);

        l = new Label("Amount of child windows attached to the main window: x");
        addComponent(l);

        b2 = new Button("Refresh", this, "refreshButtonClick");
        addComponent(b2);
    }

    public void openButtonClick(ClickEvent event) {
        Window w = new Window("New child window");
        Label desc = new Label("This is a new child window with its own state."
                + " The child window is added to the main window"
                + " instead of the application.");
        w.addComponent(desc);
        w.addListener(this);
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
        System.err.println("Sampler->WindowChildExample: Window close event");
    }

}