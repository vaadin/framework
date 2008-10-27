package com.itmill.toolkit.tests.tickets;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Set;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.terminal.ThemeResource;
import com.itmill.toolkit.terminal.UserError;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.OptionGroup;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class Ticket2181 extends Application implements Button.ClickListener {

    // private static final Object PROPERTY_VALUE = new Object();
    // private static final Object PROPERTY_CAPTION = new Object();

    private static final String caption = "This is a caption which is very long and nice and perhaps sometimes should be clipped";
    Window main = new Window("#2181 test");
    TextField tf1 = new TextField(caption, "Test field - undefined width");
    TextField tf2 = new TextField(caption, "Test field - 150px wide");
    Button setButton = new Button("Set", this);
    private Random random = new Random(123);
    private OptionGroup options;

    private static ArrayList<String> icons = new ArrayList<String>();
    static {
        icons.add("icons/64/ok.png");
        icons.add("icons/64/arrow-down.png");
        icons.add("icons/64/arrow-left.png");
        icons.add("icons/64/arrow-right.png");
        icons.add("icons/64/arrow-up.png");
    }

    public void init() {
        setMainWindow(main);
        OrderedLayout ol;
        ol = new OrderedLayout();
        ol.addComponent(tf1);
        main.addComponent(ol);

        ol = new OrderedLayout();
        ol.setWidth("150px");
        tf2.setWidth("150px");
        ol.addComponent(tf2);
        main.addComponent(ol);

        main.addComponent(createSelection());
        main.addComponent(setButton);
    }

    private Component createSelection() {
        options = new OptionGroup();
        options.addItem("Icon");
        options.addItem("Caption");
        options.addItem("Required");
        options.addItem("Error");
        options.setMultiSelect(true);
        options.select("Caption");

        // ol.addComponent(og);
        return options;
    }

    public void buttonClick(ClickEvent event) {
        if (event.getButton() == setButton) {
            set();
        }
    }

    private void set() {
        Set<String> values = (Set<String>) options.getValue();
        TextField[] tfs = new TextField[] { tf1, tf2 };
        for (TextField tf : tfs) {
            // Clear all
            tf.setCaption(null);
            tf.setComponentError(null);
            tf.setRequired(false);
            tf.setIcon(null);

            for (String value : values) {
                if (value.equals("Caption")) {
                    tf.setCaption(caption);
                } else if (value.equals("Icon")) {
                    String timestamp = String.valueOf(new Date().getTime());
                    tf.setIcon(new ThemeResource(icons.get(random.nextInt(icons
                            .size()))
                            + "?" + timestamp));
                } else if (value.equals("Required")) {
                    tf.setRequired(true);
                } else if (value.equals("Error")) {
                    tf.setComponentError(new UserError("Nooooo..."));
                }
            }
        }
    }
}
