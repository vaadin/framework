package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.terminal.Resource;
import com.itmill.toolkit.terminal.ThemeResource;
import com.itmill.toolkit.terminal.UserError;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Layout;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

public class Ticket1878 extends Application {

    private Layout orderedLayout;
    private Layout gridLayout;
    private GridLayout mainLayout;
    private Button switchButton;

    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");

        mainLayout = new GridLayout(1, 2);
        w.setLayout(mainLayout);
        orderedLayout = createOL();
        gridLayout = createGL();
        switchButton = new Button("Switch to GridLayout", new ClickListener() {

            public void buttonClick(ClickEvent event) {
                changeLayout();
            }

        });
        mainLayout.addComponent(switchButton);
        mainLayout.addComponent(orderedLayout);
        // w.setLayout(orderedLayout);
    }

    private static Layout createOL() {
        GridLayout layout = new GridLayout(1, 5);

        GridLayout l1 = new GridLayout(1, 3);
        createOrderedLayout(l1, OrderedLayout.ORIENTATION_HORIZONTAL, "1000",
                "150");
        createOrderedLayout(l1, OrderedLayout.ORIENTATION_HORIZONTAL, "1000",
                "100");
        GridLayout l2 = new GridLayout(6, 1);
        createOrderedLayout(l2, OrderedLayout.ORIENTATION_VERTICAL, "200",
                "500");
        createOrderedLayout(l2, OrderedLayout.ORIENTATION_VERTICAL, "200",
                "500", "100%", null);
        createOrderedLayout(l2, OrderedLayout.ORIENTATION_VERTICAL, "150",
                "500");
        createOrderedLayout(l2, OrderedLayout.ORIENTATION_VERTICAL, "150",
                "500", "100%", null);
        createOrderedLayout(l2, OrderedLayout.ORIENTATION_VERTICAL, "100",
                "500");
        createOrderedLayout(l2, OrderedLayout.ORIENTATION_VERTICAL, "100",
                "500", "100%", null);
        layout.addComponent(l1);
        layout.addComponent(l2);

        return layout;
    }

    private static Layout createGL() {
        GridLayout layout = new GridLayout(1, 5);

        GridLayout l1 = new GridLayout(1, 3);
        createGridLayout(l1, OrderedLayout.ORIENTATION_HORIZONTAL, "1000",
                "150");
        createGridLayout(l1, OrderedLayout.ORIENTATION_HORIZONTAL, "1000",
                "100");
        GridLayout l2 = new GridLayout(6, 1);
        createGridLayout(l2, OrderedLayout.ORIENTATION_VERTICAL, "200", "500");
        createGridLayout(l2, OrderedLayout.ORIENTATION_VERTICAL, "200", "500",
                "100%", null);
        createGridLayout(l2, OrderedLayout.ORIENTATION_VERTICAL, "150", "500");
        createGridLayout(l2, OrderedLayout.ORIENTATION_VERTICAL, "150", "500",
                "100%", null);
        createGridLayout(l2, OrderedLayout.ORIENTATION_VERTICAL, "100", "500");
        createGridLayout(l2, OrderedLayout.ORIENTATION_VERTICAL, "100", "500",
                "100%", null);
        layout.addComponent(l1);
        layout.addComponent(l2);

        return layout;
    }

    protected void changeLayout() {
        java.util.Iterator i = mainLayout.getComponentIterator();
        i.next();
        Layout l = (Layout) i.next();
        if (l == orderedLayout) {
            switchButton.setCaption("Switch to OrderedLayout");
            mainLayout.replaceComponent(l, gridLayout);
        } else {
            switchButton.setCaption("Switch to GridLayout");
            mainLayout.replaceComponent(l, orderedLayout);
        }
    }

    private static void createOrderedLayout(GridLayout parentLayout, int dir,
            String w, String h) {
        createOrderedLayout(parentLayout, dir, w, h, null, null);
    }

    private static void createOrderedLayout(GridLayout parentLayout, int dir,
            String w, String h, String componentWidth, String componentHeight) {
        OrderedLayout ol = new OrderedLayout(dir);

        String dirText = (dir == OrderedLayout.ORIENTATION_HORIZONTAL ? "H"
                : "V");
        String cWidth = componentWidth == null ? "" : " - " + componentWidth;
        Panel p = new Panel("OL/" + dirText + " " + w + "x" + h + cWidth, ol);

        p.setWidth(w);
        p.setHeight(h);

        ol.setSizeFull();

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
                    }

                    if (componentHeight != null) {
                        tf.setHeight(componentWidth);
                    }

                    p.addComponent(tf);
                    ol.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_LEFT,
                            OrderedLayout.ALIGNMENT_BOTTOM);
                }
            }
        }

        parentLayout.addComponent(p);

    }

    private static void createGridLayout(GridLayout parentLayout, int dir,
            String w, String h) {
        createGridLayout(parentLayout, dir, w, h, null, null);
    }

    private static void createGridLayout(GridLayout parentLayout, int dir,
            String w, String h, String componentWidth, String componentHeight) {
        GridLayout gl;
        if (dir == OrderedLayout.ORIENTATION_HORIZONTAL) {
            gl = new GridLayout(8, 1);
        } else {
            gl = new GridLayout(1, 8);
        }

        String dirText = (dir == OrderedLayout.ORIENTATION_HORIZONTAL ? "H"
                : "V");
        String cWidth = componentWidth == null ? "" : " - " + componentWidth;
        Panel p = new Panel("GL/" + dirText + " " + w + "x" + h + cWidth, gl);

        p.setWidth(w);
        p.setHeight(h);

        gl.setSizeFull();

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
                    }

                    if (componentHeight != null) {
                        tf.setHeight(componentWidth);
                    }

                    p.addComponent(tf);
                    gl.setComponentAlignment(tf, OrderedLayout.ALIGNMENT_LEFT,
                            OrderedLayout.ALIGNMENT_BOTTOM);
                }
            }
        }

        parentLayout.addComponent(p);

    }

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
