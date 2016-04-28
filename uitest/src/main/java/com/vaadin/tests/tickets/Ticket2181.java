package com.vaadin.tests.tickets;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Set;

import com.vaadin.server.LegacyApplication;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class Ticket2181 extends LegacyApplication implements
        Button.ClickListener {

    // private static final Object PROPERTY_VALUE = new Object();
    // private static final Object PROPERTY_CAPTION = new Object();

    private static final String caption = "This is a caption which is very long and nice and perhaps sometimes should be clipped";
    LegacyWindow main = new LegacyWindow("#2181 test");
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

    @Override
    public void init() {
        setMainWindow(main);
        VerticalLayout ol;
        ol = new VerticalLayout();
        ol.addComponent(tf1);
        main.addComponent(ol);

        ol = new VerticalLayout();
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

    @Override
    public void buttonClick(ClickEvent event) {
        if (event.getButton() == setButton) {
            set();
        }
    }

    private void set() {
        @SuppressWarnings("unchecked")
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
                            .size())) + "?" + timestamp));
                } else if (value.equals("Required")) {
                    tf.setRequired(true);
                } else if (value.equals("Error")) {
                    tf.setComponentError(new UserError("Nooooo..."));
                }
            }
        }
    }
}
