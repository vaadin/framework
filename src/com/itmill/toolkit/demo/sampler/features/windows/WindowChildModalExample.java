package com.itmill.toolkit.demo.sampler.features.windows;

import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Window.CloseEvent;

public class WindowChildModalExample extends VerticalLayout implements
        Window.CloseListener {

    private Button b;

    public WindowChildModalExample() {
        setSpacing(true);

        b = new Button("Create and open a new modal child window", this,
                "openButtonClick");
        addComponent(b);
    }

    public void openButtonClick(ClickEvent event) {
        Window w = new Window("Modal child window");
        Label desc = new Label(
                "This is a modal child window with its own state."
                        + " You cannot access the main window while the modal child"
                        + " window is shown.");
        w.addComponent(desc);
        w.addListener(this);
        w.setResizable(false);
        w.setModal(true);
        getApplication().getMainWindow().addWindow(w);
    }

    public void windowClose(CloseEvent e) {
        // In this example, the window will be removed after closing
        getApplication().getMainWindow().removeWindow(e.getWindow());
        System.err
                .println("Sampler->WindowChildModalExample: Window close event");
    }

}