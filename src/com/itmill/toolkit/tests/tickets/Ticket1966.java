package com.itmill.toolkit.tests.tickets;

import java.util.HashMap;
import java.util.Map;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.Layout;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Layout.AlignmentHandler;

public class Ticket1966 extends Application {

    private static final int LEFT = OrderedLayout.ALIGNMENT_LEFT;
    private static final int CENTER = OrderedLayout.ALIGNMENT_HORIZONTAL_CENTER;
    private static final int RIGHT = OrderedLayout.ALIGNMENT_RIGHT;
    private static final int TOP = OrderedLayout.ALIGNMENT_TOP;
    private static final int VCENTER = OrderedLayout.ALIGNMENT_VERTICAL_CENTER;
    private static final int BOTTOM = OrderedLayout.ALIGNMENT_BOTTOM;

    private static Map names = new HashMap();
    static {
        names.put(new Integer(LEFT), "Left");
        names.put(new Integer(CENTER), "Center");
        names.put(new Integer(RIGHT), "Right");
        names.put(new Integer(BOTTOM), "Bottom");
        names.put(new Integer(VCENTER), "Vcenter");
        names.put(new Integer(TOP), "Top");
    }

    public void init() {
        Window w = new Window(getClass().getName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        w.setLayout(new GridLayout(2, 2));
        // w.getLayout().setSizeFull();
        createUI(w.getLayout());
    }

    private void createUI(Layout layout) {
        orderedLayout(layout);
        gridLayout(layout);
    }

    private void gridLayout(Layout layout) {
        Panel p = new Panel("GridLayout");
        layout.addComponent(p);

        GridLayout gl = new GridLayout(1, 4);
        gl.setCaption("Horizontal");
        Button b;

        b = new Button("Wide button");
        b.setWidth("500px");
        gl.addComponent(b);

        addButtons(gl);

        p.addComponent(gl);

        /* VERTICAL */

        gl = new GridLayout(4, 1);
        gl.setCaption("Vertical");

        addButtons(gl);

        b = new Button("High button");
        b.setHeight(200);
        gl.addComponent(b);

        p.addComponent(gl);

    }

    private void orderedLayout(Layout layout) {
        Panel p = new Panel("OrderedLayout");
        layout.addComponent(p);

        OrderedLayout ol = new OrderedLayout();
        ol.setCaption("Horizontal");
        // ol.setWidth("100%");

        Button b;

        b = new Button("Wide button");
        b.setWidth("500px");
        ol.addComponent(b);

        addButtons(ol);
        p.addComponent(ol);

        /* VERTICAL */

        ol = new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL);
        ol.setCaption("Vertical");

        addButtons(ol);
        b = new Button("High button");
        b.setHeight(200);
        ol.addComponent(b);

        p.addComponent(ol);

    }

    private void addButtons(Layout ol) {
        ol.addComponent(getButton(ol, LEFT, TOP));
        ol.addComponent(getButton(ol, CENTER, VCENTER));
        ol.addComponent(getButton(ol, RIGHT, BOTTOM));

    }

    private Button getButton(Layout l, int hAlign, int vAlign) {
        Button b = new Button("Narrow Button - "
                + names.get(new Integer(hAlign)) + " - "
                + names.get(new Integer(vAlign)));
        b.setWidth("100px");
        ((AlignmentHandler) l).setComponentAlignment(b, hAlign, vAlign);

        return b;

    }
}
