package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.util.ObjectProperty;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CheckBox;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Window;

public class Ticket2151 extends Application {

    private Label status;

    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        createUI((OrderedLayout) w.getLayout());
    }

    private void createUI(OrderedLayout layout) {
        Button b = new Button("This is a button");
        CheckBox cb = new CheckBox("This is a checkbox");
        cb.setImmediate(true);
        setTheme("tests-tickets");
        layout.setStyleName("mylayout");
        status = new Label("Result:");
        layout.addComponent(status);
        layout.setSpacing(true);
        layout.setMargin(true);

        layout.addComponent(b);
        layout.addComponent(cb);

        layout.addComponent(new Label("a"));
        layout.addComponent(new Label("b"));
        layout.addComponent(new Label("c"));

        check(Button.class);
        check(CheckBox.class);
        checkDataBinding(Button.class);
        checkDataBinding(CheckBox.class);

    }

    private void check(Class<? extends Button> class1) {
        boolean ok = false;
        Button b;
        try {
            b = class1.newInstance();
            b.setCaption("Button of type " + class1.getSimpleName());
            try {
                // This should throw an exception
                b.setValue("ON");
            } catch (IllegalArgumentException e) {
                ok = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            return;
        }

        if (ok) {
            status.setValue(status.getValue() + " "
                    + class1.getClass().getSimpleName() + ": OK");
        } else {
            status.setValue(status.getValue() + " "
                    + class1.getClass().getSimpleName() + ": FAILED");
        }

    }

    private void checkDataBinding(Class<? extends Button> class1) {
        boolean ok = false;
        Button b;
        try {
            b = class1.newInstance();
            b.setCaption("Button of type " + class1.getSimpleName());
            try {
                b.setWriteThrough(true);
                b.setReadThrough(true);
                ObjectProperty prop = new ObjectProperty("ABC 123");
                /*
                 * This should throw an exception or somehow tell that the
                 * property was invalid (wrong type). See #2223.
                 */
                b.setPropertyDataSource(prop);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            return;
        }

        if (ok) {
            status.setValue(status.getValue() + " "
                    + class1.getClass().getSimpleName() + "/DB: OK");
        } else {
            status.setValue(status.getValue() + " "
                    + class1.getClass().getSimpleName() + "/DB: FAILED");
        }

    }
}
