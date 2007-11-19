package com.itmill.toolkit.demo;

import com.itmill.toolkit.event.Action;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

/**
 * Simple program that demonstrates "modal windows" that block all access other
 * windows.
 * 
 * @author IT Mill Ltd.
 * @since 4.0.1
 * @see com.itmill.toolkit.Application
 * @see com.itmill.toolkit.ui.Window
 * @see com.itmill.toolkit.ui.Label
 */
public class ModalWindow extends com.itmill.toolkit.Application implements
        Action.Handler, ClickListener {

    private Window test;

    public void init() {

        // Create main window
        Window main = new Window("ModalWindow demo");
        setMainWindow(main);
        main.addComponent(new Label("ModalWindow demo"));

        // Main window textfield
        TextField f = new TextField();
        f.setTabIndex(1);
        main.addComponent(f);

        // Main window button
        Button b = new Button("Button on main window");
        b.addListener(this);
        b.setTabIndex(2);
        main.addComponent(b);

        // Modal window
        test = new Window("Modal window");
        test.setStyle("modal");
        addWindow(test);
        test.addComponent(new Label(
                "You have to close this window before accessing others."));

        // Textfield for modal window
        f = new TextField();
        f.setTabIndex(4);
        test.addComponent(f);
        f.focus();

        // Modal window button
        b = new Button("Button on modal window");
        b.setTabIndex(3);
        b.addListener(this);
        test.addComponent(b);

    }

    public Action[] getActions(Object target, Object sender) {
        Action actionA = new Action("Action A for " + target.toString());
        Action actionB = new Action("Action B for " + target.toString());
        Action[] actions = new Action[] { actionA, actionB };
        return actions;
    }

    public void handleAction(Action action, Object sender, Object target) {
        test.addComponent(new Label(action.getCaption() + " clicked on "
                + target));

    }

    public void buttonClick(ClickEvent event) {
        test.addComponent(new Label("Clicked " + event));

    }
}
