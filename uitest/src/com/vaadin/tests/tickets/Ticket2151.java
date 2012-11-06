package com.vaadin.tests.tickets;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;

public class Ticket2151 extends LegacyApplication {

    private Label status;

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        createUI((AbstractOrderedLayout) w.getContent());
    }

    private void createUI(AbstractOrderedLayout layout) {
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

        checkButton(Button.class);
        checkCheckBox(CheckBox.class);
        checkDataBinding(CheckBox.class);

    }

    private void checkButton(Class<? extends Button> class1) {
        boolean ok = false;
        AbstractComponent b;
        try {
            b = class1.newInstance();
            b.setCaption("Button of type " + class1.getSimpleName());
            ok = true;
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        if (ok) {
            status.setValue(status.getValue() + " "
                    + class1.getClass().getSimpleName() + ": OK");
        } else {
            status.setValue(status.getValue() + " "
                    + class1.getClass().getSimpleName() + ": FAILED");
        }

    }

    private void checkCheckBox(Class<? extends CheckBox> class1) {
        boolean ok = false;
        CheckBox b;
        try {
            b = class1.newInstance();
        } catch (Exception e1) {
            e1.printStackTrace();
            return;
        }

        b.setCaption("Button of type " + class1.getSimpleName());
        status.setValue(status.getValue() + " "
                + class1.getClass().getSimpleName() + ": OK");

    }

    private void checkDataBinding(Class<? extends AbstractField> class1) {
        boolean ok = false;
        AbstractField b;
        try {
            b = class1.newInstance();
            b.setCaption("Button of type " + class1.getSimpleName());
            try {
                b.setBuffered(false);
                ObjectProperty<String> prop = new ObjectProperty<String>(
                        "ABC 123");
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
