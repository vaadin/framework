package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.terminal.UserError;
import com.itmill.toolkit.ui.ExpandLayout;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;

public class Ticket2033 extends Application {

    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        GridLayout layout = new GridLayout(2, 2);
        w.setLayout(layout);
        createUI(layout);
    }

    private void createUI(GridLayout layout) {
        layout.addComponent(createExpandLayoutPanel());
        layout.addComponent(createOrderedLayoutPanel());
        layout.addComponent(createGridLayoutPanel());
    }

    private Panel createExpandLayoutPanel() {
        ExpandLayout el = new ExpandLayout(ExpandLayout.ORIENTATION_HORIZONTAL);
        Panel p = new Panel("ExpandLayout", el);
        p.setWidth(600);
        p.setHeight(500);
        p.getLayout().setSizeFull();

        TextField tf = new TextField("TextField 1");
        tf.setValue("Expanded");
        el.addComponent(tf);
        el.expand(tf);
        tf.setSizeFull();

        tf = new TextField("TextField 2 has a longer caption");
        // tf.setComponentError(new UserError("Error"));
        tf.setWidth(100);
        tf.setValue("Vertical bottom");
        // el.setComponentAlignment(tf, ExpandLayout.ALIGNMENT_LEFT,
        // ExpandLayout.ALIGNMENT_BOTTOM);
        el.addComponent(tf);

        tf = new TextField(
                "TextField 3 has a very, very long caption for some weird reason.");
        tf.setWidth(100);
        tf.setComponentError(new UserError("Error"));
        el.addComponent(tf);
        tf.setValue("Vertical top");
        // el.setComponentAlignment(tf, ExpandLayout.ALIGNMENT_LEFT,
        // ExpandLayout.ALIGNMENT_TOP);
        tf = new TextField("TextField 4");
        el.addComponent(tf);
        tf.setValue("Vertical center");
        // tf.setComponentError(new UserError("Error"));
        // el.setComponentAlignment(tf, ExpandLayout.ALIGNMENT_LEFT,
        // ExpandLayout.ALIGNMENT_VERTICAL_CENTER);

        return p;
    }

    private Panel createOrderedLayoutPanel() {
        OrderedLayout ol = new OrderedLayout(
                ExpandLayout.ORIENTATION_HORIZONTAL);
        Panel p = new Panel("OrderedLayout", ol);
        p.setWidth(600);
        p.setHeight(500);
        p.getLayout().setSizeFull();

        TextField tf = new TextField("TextField 1");
        tf.setValue("Expanded");
        ol.addComponent(tf);
        // ol.expand(tf);
        tf.setSizeFull();

        tf = new TextField("TextField 2 has a longer caption");
        // tf.setComponentError(new UserError("Error"));
        tf.setWidth(100);
        tf.setValue("Vertical bottom");
        // el.setComponentAlignment(tf, ExpandLayout.ALIGNMENT_LEFT,
        // ExpandLayout.ALIGNMENT_BOTTOM);
        ol.addComponent(tf);

        tf = new TextField(
                "TextField 3 has a very, very long caption for some weird reason.");
        tf.setWidth(100);
        tf.setComponentError(new UserError("Error"));
        ol.addComponent(tf);
        tf.setValue("Vertical top");
        // el.setComponentAlignment(tf, ExpandLayout.ALIGNMENT_LEFT,
        // ExpandLayout.ALIGNMENT_TOP);
        tf = new TextField("TextField 4");
        ol.addComponent(tf);
        tf.setValue("Vertical center");
        // tf.setComponentError(new UserError("Error"));
        // el.setComponentAlignment(tf, ExpandLayout.ALIGNMENT_LEFT,
        // ExpandLayout.ALIGNMENT_VERTICAL_CENTER);

        return p;
    }

    private Panel createGridLayoutPanel() {
        GridLayout gl = new GridLayout(4, 1);
        Panel p = new Panel("GridLayout", gl);
        p.setWidth(600);
        p.setHeight(500);
        p.getLayout().setSizeFull();

        TextField tf = new TextField("TextField 1");
        tf.setValue("Expanded");
        gl.addComponent(tf);
        // ol.expand(tf);
        tf.setSizeFull();

        tf = new TextField("TextField 2 has a longer caption");
        // tf.setComponentError(new UserError("Error"));
        tf.setWidth(100);
        tf.setValue("Vertical bottom");
        // el.setComponentAlignment(tf, ExpandLayout.ALIGNMENT_LEFT,
        // ExpandLayout.ALIGNMENT_BOTTOM);
        gl.addComponent(tf);

        tf = new TextField(
                "TextField 3 has a very, very long caption for some weird reason.");
        tf.setWidth(100);
        tf.setComponentError(new UserError("Error"));
        gl.addComponent(tf);
        tf.setValue("Vertical top");
        // el.setComponentAlignment(tf, ExpandLayout.ALIGNMENT_LEFT,
        // ExpandLayout.ALIGNMENT_TOP);
        tf = new TextField("TextField 4");
        gl.addComponent(tf);
        tf.setValue("Vertical center");
        // tf.setComponentError(new UserError("Error"));
        // el.setComponentAlignment(tf, ExpandLayout.ALIGNMENT_LEFT,
        // ExpandLayout.ALIGNMENT_VERTICAL_CENTER);

        return p;
    }
}
