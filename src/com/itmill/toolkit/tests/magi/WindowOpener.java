package com.itmill.toolkit.tests.magi;

import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Window.CloseEvent;

/** Component contains a button that allows opening a window. */
public class WindowOpener extends CustomComponent implements
        Window.CloseListener {
    Window mainwindow; // Reference to main window
    Window mywindow; // The window to be opened
    Button openbutton; // Button for opening the window
    Button closebutton; // A button in the window
    Label explanation; // A descriptive text

    public WindowOpener(String label, Window main) {
        mainwindow = main;

        /* The component consists of a button that opens the window. */
        OrderedLayout layout = new OrderedLayout();
        layout.addComponent(openbutton = new Button("Open Window", this,
                "openButtonClick"));
        layout.addComponent(explanation = new Label("Explanation"));
        setCompositionRoot(layout);
    }

    /** Handle the clicks for the two buttons. */
    public void openButtonClick(Button.ClickEvent event) {
        /* Create a new window. */
        mywindow = new Window("My Dialog");

        /* Listen for close events for the window. */
        mywindow.addListener(this);

        /* Add components in the window. */
        mywindow.addComponent(new Label("A text label in the window."));
        closebutton = new Button("Close", this, "closeButtonClick");
        mywindow.addComponent(closebutton);

        /* Add the window inside the main window. */
        mainwindow.addWindow(mywindow);

        /* Allow opening only one window at a time. */
        openbutton.setEnabled(false);

        explanation.setValue("Window opened");
    }

    /** Handle Close button click and close the window. */
    public void closeButtonClick(Button.ClickEvent event) {
        /* Windows are managed by the application object. */
        mainwindow.removeWindow(mywindow);

        /* Return to initial state. */
        openbutton.setEnabled(true);

        explanation.setValue("Closed with button");
    }

    /** In case the window is closed otherwise. */
    public void windowClose(CloseEvent e) {
        /* Return to initial state. */
        openbutton.setEnabled(true);

        explanation.setValue("Closed with window controls");
    }
}
