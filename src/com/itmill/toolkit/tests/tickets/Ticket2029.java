package com.itmill.toolkit.tests.tickets;

import java.util.Random;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.data.Property.ValueChangeListener;
import com.itmill.toolkit.terminal.UserError;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;

public class Ticket2029 extends Application {

    int COMPONENTS;
    int DIM1, DIM2;
    Random r = new Random();

    public void init() {
        COMPONENTS = 5;
        DIM1 = 504;
        DIM2 = 100;

        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        Panel p = createPanel();
        w.getLayout().addComponent(p);
        // w.getLayout().addComponent(createGLPanel());
        w.getLayout().addComponent(createPanelV());
    }

    private Panel createPanel() {
        Panel p = new Panel(DIM1 + "x" + DIM2 + " OrderedLayout");
        p.setWidth(DIM1 + "px");
        p.setHeight(DIM2 + "px");

        OrderedLayout layout = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);
        p.setLayout(layout);
        p.getLayout().setSizeFull();

        for (int i = 0; i < COMPONENTS; i++) {
            TextField tf = new TextField();
            if (r.nextBoolean()) {
                tf.setCaption("Caption");
            }
            if (r.nextBoolean()) {
                tf.setRequired(true);
            }
            if (r.nextBoolean()) {
                tf.setComponentError(new UserError("Error"));
            }
            tf.setWidth("100%");
            layout.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_LEFT,
                    OrderedLayout.ALIGNMENT_BOTTOM);
            p.addComponent(tf);

        }

        return p;
    }

    private Panel createGLPanel() {
        Panel p = new Panel("" + DIM1 + "x" + DIM2 + " GridLayout");
        p.setWidth("" + DIM1 + "px");
        p.setHeight("" + DIM2 + "px");

        GridLayout layout = new GridLayout(COMPONENTS, 1);
        p.setLayout(layout);
        p.getLayout().setSizeFull();

        for (int i = 0; i < COMPONENTS; i++) {
            TextField tf = new TextField();
            tf.setImmediate(true);
            tf.addListener(new ValueChangeListener() {

                public void valueChange(ValueChangeEvent event) {
                    Component c = ((Component) event.getProperty());
                    c.setCaption("askfdj");

                }
            });
            if (r.nextBoolean()) {
                tf.setCaption("Caption");
            }
            if (r.nextBoolean()) {
                tf.setRequired(true);
            }
            if (r.nextBoolean()) {
                tf.setComponentError(new UserError("Error"));
            }
            tf.setWidth("100%");
            layout.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_LEFT,
                    OrderedLayout.ALIGNMENT_VERTICAL_CENTER);
            p.addComponent(tf);

        }

        return p;
    }

    private Panel createPanelV() {
        Panel p = new Panel("" + DIM1 + "x" + DIM2 + " OrderedLayout");
        p.setWidth("" + DIM2 + "px");
        p.setHeight("" + DIM1 + "px");

        OrderedLayout layout = new OrderedLayout(
                OrderedLayout.ORIENTATION_VERTICAL);
        p.setLayout(layout);
        p.getLayout().setSizeFull();

        for (int i = 0; i < COMPONENTS; i++) {
            TextField tf = new TextField();
            if (r.nextBoolean()) {
                tf.setCaption("Caption");
            }
            if (r.nextBoolean()) {
                tf.setRequired(true);
            }
            if (r.nextBoolean()) {
                tf.setComponentError(new UserError("Error"));
            }

            tf.setRows(2);
            tf.setSizeFull();

            layout.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_LEFT,
                    OrderedLayout.ALIGNMENT_BOTTOM);
            p.addComponent(tf);

        }

        return p;
    }
}
