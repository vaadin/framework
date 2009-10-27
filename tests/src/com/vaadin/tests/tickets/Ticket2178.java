package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;

public class Ticket2178 extends Application {

    @Override
    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        createUI((OrderedLayout) w.getLayout());
    }

    private void createUI(OrderedLayout layout) {
        OrderedLayout ol;
        Panel p;
        ComboBox cb;

        ol = new OrderedLayout();
        p = new Panel(ol);
        p.setCaption("Combobox without width");
        // p.setWidth("100px");
        cb = new ComboBox();
        // cb.setCaption("A combobox");
        // cb.setWidth("100%");
        p.addComponent(cb);
        layout.addComponent(p);

        ol = new OrderedLayout();
        p = new Panel(ol);
        p.setCaption("Combobox without width with caption");
        // p.setWidth("100px");
        cb = new ComboBox();
        cb.setCaption("A combobox");
        // cb.setWidth("100px");
        p.addComponent(cb);
        layout.addComponent(p);

        ol = new OrderedLayout();
        p = new Panel(ol);
        p.setCaption("Combobox 100px wide");
        // p.setWidth("100px");
        cb = new ComboBox();
        // cb.setCaption("A combobox");
        cb.setWidth("100px");
        p.addComponent(cb);
        layout.addComponent(p);

        ol = new OrderedLayout();
        p = new Panel(ol);
        p.setCaption("Combobox 100px wide with caption");
        // p.setWidth("100px");
        cb = new ComboBox();
        cb.setCaption("A combobox");
        cb.setWidth("100px");
        p.addComponent(cb);
        layout.addComponent(p);

        ol = new OrderedLayout();
        p = new Panel(ol);
        p.setCaption("Combobox 500px wide");
        // p.setWidth("500px");
        cb = new ComboBox();
        // cb.setCaption("A combobox");
        cb.setWidth("500px");
        p.addComponent(cb);
        layout.addComponent(p);

        ol = new OrderedLayout();
        p = new Panel(ol);
        p.setCaption("Combobox 500px wide with caption");
        // p.setWidth("500px");
        cb = new ComboBox();
        cb.setCaption("A combobox");
        cb.setWidth("500px");
        p.addComponent(cb);
        layout.addComponent(p);

        ol = new OrderedLayout();
        p = new Panel(ol);
        p.setCaption("Combobox 100% wide inside 200px panel");
        p.setWidth("200px");
        ol.setWidth("100%");
        cb = new ComboBox();
        // cb.setCaption("A combobox");
        cb.setWidth("100%");
        // cb.setWidth("500px");
        p.addComponent(cb);
        layout.addComponent(p);

        ol = new OrderedLayout();
        p = new Panel(ol);
        p.setCaption("Combobox 100% wide inside 200px panel with caption");
        p.setWidth("200px");
        ol.setWidth("100%");
        cb = new ComboBox();
        cb.setCaption("A combobox");
        cb.setWidth("100%");
        p.addComponent(cb);
        layout.addComponent(p);

    }
}
