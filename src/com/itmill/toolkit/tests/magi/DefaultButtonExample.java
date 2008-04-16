/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests.magi;

import com.itmill.toolkit.event.Action;
import com.itmill.toolkit.event.ShortcutAction;
import com.itmill.toolkit.event.Action.Handler;
import com.itmill.toolkit.ui.AbstractField;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.FormLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;

public class DefaultButtonExample extends CustomComponent implements Handler {
    // Define and create user interface components
    Panel panel = new Panel("Login");
    OrderedLayout formlayout = new FormLayout();
    TextField username = new TextField("Username");
    TextField password = new TextField("Password");
    OrderedLayout buttons = new FormLayout();

    // Create buttons and define their listener methods. Here we use parameterless
    // methods so that we can use same methods for both click events and
    // keyboard actions.
    Button ok = new Button("OK", this, "okHandler");
    Button cancel = new Button("Cancel", this, "cancelHandler");

    // Have the unmodified Enter key cause an event
    Action action_ok = new ShortcutAction("Default key",
                                          ShortcutAction.KeyCode.ENTER,
                                          null);

    // Have the C key modified with Alt cause an event
    Action action_cancel = new ShortcutAction("Alt+C",
                                              ShortcutAction.KeyCode.C,
                                              new int[] { ShortcutAction.ModifierKey.ALT });

    Window window = null;

    public DefaultButtonExample(Window win) {
        // Set up the user interface
        setCompositionRoot(panel);
        panel.addComponent(formlayout);
        formlayout.setOrientation(OrderedLayout.ORIENTATION_VERTICAL);
        formlayout.addComponent(username);
        formlayout.addComponent(password);
        formlayout.addComponent(buttons);
        buttons.setOrientation(OrderedLayout.ORIENTATION_HORIZONTAL);
        buttons.addComponent(ok);
        buttons.addComponent(cancel);

        // Set focus to username
        username.focus();

        // Set this object as the action handler
        System.out.println("adding ah");
        win.addActionHandler(this);
        window = win;

        System.out.println("start done.");
    }

    /**
     * Retrieve actions for a specific component. This method will be called for
     * each object that has a handler; in this example the Ok and Cancel
     * buttons.
     */
    public Action[] getActions(Object target, Object sender) {
        System.out.println("getActions()");
        return new Action[] {action_ok, action_cancel};
    }

    /**
     * Handle actions received from keyboard. This simply directs the actions to
     * the same listener methods that are called with ButtonClick events.
     */
    public void handleAction(Action action, Object sender, Object target) {
        if (action == action_ok)
            okHandler();
        if (action == action_cancel)
            cancelHandler();
    }

    public void okHandler() {
        // Do something: report the click
        formlayout.addComponent(new Label("OK clicked. "+
                                          "User="+username.getValue()+
                                          ", password="+password.getValue()));
        //  
    }

    public void cancelHandler() {
        // Do something: report the click
        formlayout.addComponent(new Label("Cancel clicked. User="+username.getValue()+", password="+password.getValue()));
    }
}
