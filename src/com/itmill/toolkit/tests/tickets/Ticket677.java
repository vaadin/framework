package com.itmill.toolkit.tests.tickets;

import java.util.Date;
import java.util.Set;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.DateField;
import com.itmill.toolkit.ui.Form;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Layout;
import com.itmill.toolkit.ui.OptionGroup;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

public class Ticket677 extends Application {

    private static final Object P1 = new Object();
    private static final Object P2 = new Object();
    private static final Object P3 = new Object();

    private Panel panel;
    private Form form;

    public void init() {
        Window w = new Window(getClass().getName());
        setMainWindow(w);
        // setTheme("tests-ticket");
        GridLayout layout = new GridLayout(10, 10);
        w.setLayout(layout);
        createUI(layout);

        createMultiLevelHierarchy();
    }

    private void createMultiLevelHierarchy() {

        Layout lo = new OrderedLayout();

        final OptionGroup disabled = new OptionGroup("Levels to disable");
        disabled.addItem("L1");
        disabled.addItem("L2");
        disabled.addItem("L3");
        disabled.setMultiSelect(true);
        disabled.setImmediate(true);
        lo.addComponent(disabled);

        final Label lastClick = new Label("-");
        lastClick.setCaption("Last Click:");
        lo.addComponent(lastClick);

        ClickListener clickListener = new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                lastClick.setValue(event.getButton().getCaption() + " : "
                        + new Date());
            }
        };

        final Panel[] p = new Panel[4];

        p[1] = new Panel("Level1");
        lo.addComponent(p[1]);
        Button b1 = new Button("Inside level1");
        b1.addListener(clickListener);
        p[1].addComponent(b1);
        Button b1d = new Button("Disabked Inside level1");
        b1d.setEnabled(false);
        b1d.addListener(clickListener);
        p[1].addComponent(b1d);

        p[2] = new Panel("Level2");
        p[1].addComponent(p[2]);
        Button b2 = new Button("Inside level2");
        b2.addListener(clickListener);
        p[2].addComponent(b2);
        Button b2d = new Button("Disabled Inside level2");
        b2d.setEnabled(false);
        b2d.addListener(clickListener);
        p[2].addComponent(b2d);

        p[3] = new Panel("Level3");
        p[2].addComponent(p[3]);
        Button b3 = new Button("Inside level3");
        b3.addListener(clickListener);
        p[3].addComponent(b3);
        Button b3d = new Button("Disabled Inside level3");
        b3d.setEnabled(false);
        b3d.addListener(clickListener);
        p[3].addComponent(b3d);

        disabled.addListener(new OptionGroup.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                Set disabledIds = (Set) disabled.getValue();
                for (int i = 1; i < 4; i++) {
                    boolean ena = !disabledIds.contains("L" + i);
                    if (p[i].isEnabled() != ena) {
                        p[i].setEnabled(ena);
                    }
                }
            }
        });

        getMainWindow().addComponent(lo);
    }

    private void createUI(GridLayout layout) {
        createPanel(layout);
        createForm(layout);

        layout.addComponent(new Button("Enable", new ClickListener() {

            public void buttonClick(ClickEvent event) {
                panel.setEnabled(true);
                form.setEnabled(true);
            }

        }));

        layout.addComponent(new Button("Disable", new ClickListener() {

            public void buttonClick(ClickEvent event) {
                panel.setEnabled(false);
                form.setEnabled(false);
            }

        }));

    }

    private void createForm(GridLayout layout) {
        form = new Form();
        form.addField(P1, new TextField());
        form.addField(P2, new DateField());
        form.addField(P3, new DateField());

        layout.addComponent(form);
    }

    private void createPanel(GridLayout layout) {
        panel = new Panel("panel caption");
        layout.addComponent(panel);

        panel.addComponent(new Label("Label 1"));

        OrderedLayout innerLayout1 = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);
        innerLayout1.setSpacing(true);
        panel.addComponent(innerLayout1);

        TextField tf = new TextField("TextField inside orderedLayout");
        tf.setImmediate(true);
        innerLayout1.addComponent(tf);
        innerLayout1.addComponent(new Button("Button inside orderedLayout",
                new ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        System.out.println("Clicked "
                                + event.getButton().getCaption());
                    }

                }));
        innerLayout1.addComponent(new Label("Label inside orderedLayout"));

        panel.addComponent(new Label("Label 2"));

        GridLayout innerLayout2 = new GridLayout(3, 3);
        innerLayout2.setSpacing(true);
        tf = new TextField("TextField inside gridLayout");
        tf.setImmediate(true);
        innerLayout2.addComponent(tf);
        innerLayout2.addComponent(new Button("Button inside gridLayout"));
        innerLayout2.addComponent(new Label("Label inside gridLayout"));
        panel.addComponent(innerLayout2);

    }
}
