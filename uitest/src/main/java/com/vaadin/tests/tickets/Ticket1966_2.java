package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Layout.AlignmentHandler;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class Ticket1966_2 extends LegacyApplication {

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getName());
        setMainWindow(w);
        w.setContent(new GridLayout(2, 2));

        // Panel p = new Panel("test");
        // p.setWidth(500);
        // p.setHeight(500);
        // p.setContent(new GridLayout(1, 2));
        // p.getLayout().setSizeFull();
        //
        // p.addComponent(new Button("asjkdfhakshdf"));
        // p.addComponent(new Button("öalijgto8aq5"));

        // GridLayout gl = new GridLayout(4, 1);
        // // gl.setCaption("Vertical");
        // gl.setWidth("100%");
        // gl.setHeight(500);

        // addButtons(gl);
        // gl.addComponent(new Label("abc"));
        // p.addComponent(gl);

        // w.getLayout().addComponent(p);
        createUI((Layout) w.getContent());
    }

    private void createUI(Layout layout) {
        orderedLayout(layout);
        gridLayout(layout);
        expandLayout(layout);
    }

    private void gridLayout(Layout layout) {
        VerticalLayout pl = new VerticalLayout();
        pl.setMargin(true);
        Panel p = new Panel("GridLayout", pl);
        p.setWidth("500px");
        p.setHeight("500px");
        pl.setSizeFull();
        layout.addComponent(p);

        GridLayout gl = new GridLayout(1, 4);
        gl.setMargin(true);
        gl.setCaption("Horizontal");
        gl.setWidth("100%");

        // Button b;

        // b = new Button("Wide button");
        // b.setWidth("500");
        // gl.addComponent(b);

        addButtons(gl);

        p.setContent(gl);

        /* VERTICAL */

        gl = new GridLayout(4, 1);
        gl.setMargin(true);
        // gl.setCaption("Vertical");
        gl.setHeight("100%");
        addButtons(gl);

        // Button b = new Button("High button");
        // b.setHeight(200);
        // gl.addComponent(b);

        p.setContent(gl);

    }

    private void orderedLayout(Layout layout) {
        VerticalLayout pl = new VerticalLayout();
        pl.setMargin(true);
        Panel p = new Panel("OrderedLayout", pl);
        p.setWidth("500px");
        p.setHeight("500px");
        pl.setWidth("100%");
        layout.addComponent(p);

        AbstractOrderedLayout ol = new VerticalLayout();
        ol.setMargin(true);
        // ol.setCaption("Horizontal");
        ol.setWidth("100%");
        addButtons(ol);
        pl.addComponent(ol);

        /* VERTICAL */

        ol = new HorizontalLayout();
        ol.setMargin(true);
        // ol.setCaption("Vertical");
        ol.setHeight("200px");
        addButtons(ol);
        // Button b = new Button("High button");
        // b.setHeight(200);
        // ol.addComponent(b);
        pl.addComponent(ol);

    }

    private void expandLayout(Layout layout) {
        VerticalLayout panelLayout = new VerticalLayout();
        panelLayout.setMargin(true);
        Panel p = new Panel("ExpandLayout", panelLayout);
        layout.addComponent(p);
        panelLayout.setWidth("500");
        panelLayout.setHeight("400");

        AbstractOrderedLayout el = new VerticalLayout();
        // el.setCaption("Horizontal");
        // el.setSizeUndefined();
        // el.setWidth("100%");
        // ol.setWidth("100%");
        Button b;

        b = new Button("Wide button");
        b.setWidth("100%");
        // b.setHeight(200);
        // el.setExpandRatio(b,1);
        // el.addComponent(b);

        addButtons(el);
        panelLayout.addComponent(el);

        /* VERTICAL */

        el = new HorizontalLayout();
        // el.setHeight(400);
        // el.setWidth("100%");
        // el.setCaption("Vertical");

        addButtons(el);
        // b = new Button("High button");
        // el.setExpandRatio(b,1);
        // b.setHeight(100);
        // el.addComponent(b);

        panelLayout.addComponent(el);

    }

    private void addButtons(Layout ol) {
        ol.addComponent(getButton(ol, Alignment.TOP_LEFT));
        ol.addComponent(getButton(ol, Alignment.MIDDLE_CENTER));
        ol.addComponent(getButton(ol, Alignment.BOTTOM_RIGHT));

    }

    private Button getButton(Layout l, Alignment align) {
        Button b = new Button(align.getHorizontalAlignment() + " - "
                + align.getVerticalAlignment());
        // b.setWidth("100");
        ((AlignmentHandler) l).setComponentAlignment(b, align);

        return b;

    }
}
