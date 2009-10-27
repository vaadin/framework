package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.ExpandLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

public class Ticket2032 extends Application {

    @Override
    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        GridLayout layout = new GridLayout(10, 10);
        w.setLayout(layout);
        createUI(layout);
    }

    private void createUI(GridLayout layout) {
        ExpandLayout el = new ExpandLayout(ExpandLayout.ORIENTATION_HORIZONTAL);
        Panel p = new Panel(el);
        p.setWidth(600);
        p.setHeight(500);
        p.getLayout().setSizeFull();

        TextField tf = new TextField("Field caption");
        tf.setValue("Expanded");
        el.addComponent(tf);
        el.expand(tf);
        tf.setSizeFull();

        tf = new TextField("Vertical bottom");
        // tf.setComponentError(new UserError("Error"));
        tf.setValue("Vertical bottom");
        el.setComponentAlignment(tf, ExpandLayout.ALIGNMENT_LEFT,
                ExpandLayout.ALIGNMENT_BOTTOM);
        el.addComponent(tf);

        tf = new TextField("Vertical top");
        tf.setComponentError(new UserError("Error"));
        el.addComponent(tf);
        tf.setValue("Vertical top");
        el.setComponentAlignment(tf, ExpandLayout.ALIGNMENT_LEFT,
                ExpandLayout.ALIGNMENT_TOP);
        tf = new TextField("Vertical center");
        el.addComponent(tf);
        tf.setValue("Vertical center");
        // tf.setComponentError(new UserError("Error"));
        el.setComponentAlignment(tf, ExpandLayout.ALIGNMENT_LEFT,
                ExpandLayout.ALIGNMENT_VERTICAL_CENTER);

        layout.addComponent(p);
    }
}
