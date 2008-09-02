package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.terminal.Resource;
import com.itmill.toolkit.terminal.ThemeResource;
import com.itmill.toolkit.terminal.UserError;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.ExpandLayout;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Layout;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;
import com.itmill.toolkit.ui.Layout.AlignmentHandler;

public class Ticket1878 extends Application {

    private Layout orderedLayout;
    private Layout gridLayout;
    private Layout expandLayout;
    private GridLayout mainLayout;
    private Button switchToGridButton;
    private Button switchToOrderedButton;
    private Button switchToExpandButton;

    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");

        mainLayout = new GridLayout(1, 2);
        w.setLayout(mainLayout);
        orderedLayout = createOL();
        gridLayout = createGL();
        expandLayout = createEL();

        switchToGridButton = new Button("Switch to GridLayout",
                new ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        changeLayout(switchToGridButton, gridLayout);
                    }

                });
        switchToOrderedButton = new Button("Switch to OrderedLayout",
                new ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        changeLayout(switchToOrderedButton, orderedLayout);
                    }

                });
        switchToOrderedButton.setEnabled(false);
        switchToExpandButton = new Button("Switch to ExpandLayout",
                new ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        changeLayout(switchToExpandButton, expandLayout);
                    }

                });

        OrderedLayout buttonLayout = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);
        buttonLayout.addComponent(switchToOrderedButton);
        buttonLayout.addComponent(switchToGridButton);
        buttonLayout.addComponent(switchToExpandButton);

        mainLayout.addComponent(buttonLayout);
        mainLayout.addComponent(orderedLayout);
        // w.setLayout(orderedLayout);
    }

    private static Layout createOL() {
        GridLayout layout = new GridLayout(1, 5);

        GridLayout l1 = new GridLayout(1, 3);
        createLayout(l1,
                new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL),
                "1000", "150", "100%", null, true);
        createLayout(l1,
                new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL),
                "1000", "150", "100", null, false);
        GridLayout l2 = new GridLayout(6, 1);
        createLayout(l2, new OrderedLayout(OrderedLayout.ORIENTATION_VERTICAL),
                "200", "500", true);
        createLayout(l2, new OrderedLayout(OrderedLayout.ORIENTATION_VERTICAL),
                "200", "500", "100%", null, true);
        createLayout(l2, new OrderedLayout(OrderedLayout.ORIENTATION_VERTICAL),
                "150", "500", true);
        createLayout(l2, new OrderedLayout(OrderedLayout.ORIENTATION_VERTICAL),
                "150", "500", "100%", null, true);
        createLayout(l2, new OrderedLayout(OrderedLayout.ORIENTATION_VERTICAL),
                "100", "500", true);
        createLayout(l2, new OrderedLayout(OrderedLayout.ORIENTATION_VERTICAL),
                "100", "500", "100%", null, true);
        layout.addComponent(l1);
        layout.addComponent(l2);

        return layout;
    }

    private static Layout createGL() {
        GridLayout layout = new GridLayout(1, 5);

        GridLayout l1 = new GridLayout(1, 3);
        createLayout(l1, new GridLayout(8, 1), "1000", "150", "100%", null,
                true);
        createLayout(l1, new GridLayout(8, 1), "1000", "150", "100", null,
                false);
        GridLayout l2 = new GridLayout(6, 1);
        createLayout(l2, new GridLayout(1, 8), "200", "500", true);
        createLayout(l2, new GridLayout(1, 8), "200", "500", "100%", null, true);
        createLayout(l2, new GridLayout(1, 8), "150", "500", true);
        createLayout(l2, new GridLayout(1, 8), "150", "500", "100%", null, true);
        createLayout(l2, new GridLayout(1, 8), "100", "500", true);
        createLayout(l2, new GridLayout(1, 8), "100", "500", "100%", null, true);
        layout.addComponent(l1);
        layout.addComponent(l2);

        return layout;
    }

    private static Layout createEL() {
        GridLayout layout = new GridLayout(1, 5);

        GridLayout l1 = new GridLayout(1, 3);
        createLayout(l1, new ExpandLayout(ExpandLayout.ORIENTATION_HORIZONTAL),
                "1000", "150", "100", null, true);
        createLayout(l1, new ExpandLayout(ExpandLayout.ORIENTATION_HORIZONTAL),
                "1000", "150", "50", null, false);

        GridLayout l2 = new GridLayout(6, 1);
        createLayout(l2, new ExpandLayout(ExpandLayout.ORIENTATION_VERTICAL),
                "200", "500", true);
        createLayout(l2, new ExpandLayout(ExpandLayout.ORIENTATION_VERTICAL),
                "200", "500", "100%", null, true);
        createLayout(l2, new ExpandLayout(ExpandLayout.ORIENTATION_VERTICAL),
                "150", "500", true);
        createLayout(l2, new ExpandLayout(ExpandLayout.ORIENTATION_VERTICAL),
                "150", "500", "100%", null, true);
        createLayout(l2, new ExpandLayout(ExpandLayout.ORIENTATION_VERTICAL),
                "100", "500", true);
        createLayout(l2, new ExpandLayout(ExpandLayout.ORIENTATION_VERTICAL),
                "100", "500", "100%", null, true);
        layout.addComponent(l1);
        layout.addComponent(l2);

        return layout;
    }

    protected void changeLayout(Button b, Layout newLayout) {
        switchToOrderedButton.setEnabled(true);
        switchToGridButton.setEnabled(true);
        switchToExpandButton.setEnabled(true);

        b.setEnabled(false);
        java.util.Iterator i = mainLayout.getComponentIterator();
        i.next();
        Layout l = (Layout) i.next();

        mainLayout.replaceComponent(l, newLayout);
    }

    private static void createLayout(GridLayout parentLayout, Layout newLayout,
            String w, String h, boolean align) {
        createLayout(parentLayout, newLayout, w, h, null, null, align);
    }

    private static void createLayout(GridLayout parentLayout, Layout newLayout,
            String w, String h, String componentWidth, String componentHeight,
            boolean align) {
        int dir;
        String type;
        if (newLayout instanceof ExpandLayout) {
            dir = ((ExpandLayout) newLayout).getOrientation();
            type = "EL";
        } else if (newLayout instanceof OrderedLayout) {
            dir = ((OrderedLayout) newLayout).getOrientation();
            type = "OL";
        } else {
            dir = ((GridLayout) newLayout).getColumns() == 1 ? OrderedLayout.ORIENTATION_VERTICAL
                    : OrderedLayout.ORIENTATION_HORIZONTAL;
            type = "GL";
        }
        String dirText = (dir == OrderedLayout.ORIENTATION_HORIZONTAL ? "H"
                : "V");
        String alignText = align ? "-A" : "";
        String cWidth = componentWidth == null ? "" : " - " + componentWidth;
        Panel p = new Panel(type + "/" + dirText + alignText + " " + w + "x"
                + h + cWidth, newLayout);

        p.setWidth(w);
        p.setHeight(h);

        newLayout.setSizeFull();

        String captions[] = new String[] { "TextField with caption", null };
        Resource icons[] = new Resource[] {
                new ThemeResource("icons/16/document-delete.png"), null };
        boolean required[] = new boolean[] { true, false };
        TextField fields[][] = new TextField[captions.length][icons.length];
        for (int caption = 0; caption < captions.length; caption++) {
            for (int icon = 0; icon < icons.length; icon++) {
                for (int req = 0; req < required.length; req++) {
                    TextField tf = createTextFieldWithError(captions[caption],
                            icons[icon], required[req]);

                    fields[caption][icon] = tf;
                    if (componentWidth != null) {
                        tf.setWidth(componentWidth);
                        tf.setValue(tf.getValue() + " w:" + componentWidth);
                    }

                    if (componentHeight != null) {
                        tf.setHeight(componentWidth);
                        tf.setValue(tf.getValue() + " h:" + componentHeight);
                    }

                    p.addComponent(tf);

                    if (align) {
                        ((AlignmentHandler) newLayout).setComponentAlignment(
                                tf, OrderedLayout.ALIGNMENT_LEFT,
                                OrderedLayout.ALIGNMENT_BOTTOM);
                    }
                }
            }
        }

        parentLayout.addComponent(p);

    }

    // private static void createGridLayout(GridLayout parentLayout, int dir,
    // String w, String h) {
    // createGridLayout(parentLayout, dir, w, h, null, null);
    // }

    // private static void createGridLayout(GridLayout parentLayout, int dir,
    // String w, String h, String componentWidth, String componentHeight) {
    // GridLayout gl;
    // if (dir == OrderedLayout.ORIENTATION_HORIZONTAL) {
    // gl = new GridLayout(8, 1);
    // } else {
    // gl = new GridLayout(1, 8);
    // }
    //
    // String dirText = (dir == OrderedLayout.ORIENTATION_HORIZONTAL ? "H"
    // : "V");
    // String cWidth = componentWidth == null ? "" : " - " + componentWidth;
    // Panel p = new Panel("GL/" + dirText + " " + w + "x" + h + cWidth, gl);
    //
    // p.setWidth(w);
    // p.setHeight(h);
    //
    // gl.setSizeFull();
    //
    // String captions[] = new String[] { "TextField with caption", null };
    // Resource icons[] = new Resource[] {
    // new ThemeResource("icons/16/document-delete.png"), null };
    // boolean required[] = new boolean[] { true, false };
    // TextField fields[][] = new TextField[captions.length][icons.length];
    // for (int caption = 0; caption < captions.length; caption++) {
    // for (int icon = 0; icon < icons.length; icon++) {
    // for (int req = 0; req < required.length; req++) {
    // TextField tf = createTextFieldWithError(captions[caption],
    // icons[icon], required[req]);
    //
    // fields[caption][icon] = tf;
    // if (componentWidth != null) {
    // tf.setWidth(componentWidth);
    // }
    //
    // if (componentHeight != null) {
    // tf.setHeight(componentWidth);
    // }
    //
    // p.addComponent(tf);
    // gl.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_LEFT,
    // OrderedLayout.ALIGNMENT_BOTTOM);
    // }
    // }
    // }
    //
    // parentLayout.addComponent(p);
    //
    // }

    private static TextField createTextFieldWithError(String caption,
            Resource icon, boolean required) {
        TextField tf = new TextField();
        tf.setCaption(caption);
        tf.setIcon(icon);
        tf.setRequired(required);
        tf.setComponentError(new UserError("Test error message"));
        return tf;
    }
}
