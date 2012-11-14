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

public class Ticket1966 extends LegacyApplication {

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        w.setContent(new GridLayout(2, 2));
        // w.getLayout().setSizeFull();
        createUI((Layout) w.getContent());
    }

    private void createUI(Layout layout) {
        orderedLayout(layout);
        gridLayout(layout);
    }

    private void gridLayout(Layout layout) {
        Panel p = new Panel("GridLayout");
        layout.addComponent(p);

        GridLayout gl = new GridLayout(1, 4);
        gl.setMargin(true);
        gl.setCaption("Horizontal");
        Button b;

        b = new Button("Wide button");
        b.setWidth("500px");
        gl.addComponent(b);

        addButtons(gl);

        p.setContent(gl);

        /* VERTICAL */

        gl = new GridLayout(4, 1);
        gl.setMargin(true);
        gl.setCaption("Vertical");

        addButtons(gl);

        b = new Button("High button");
        b.setHeight("200px");
        gl.addComponent(b);

        p.setContent(gl);

    }

    private void orderedLayout(Layout layout) {
        Panel p = new Panel("OrderedLayout");
        layout.addComponent(p);

        AbstractOrderedLayout ol = new VerticalLayout();
        ol.setMargin(true);
        ol.setCaption("Horizontal");
        // ol.setWidth("100%");

        Button b;

        b = new Button("Wide button");
        b.setWidth("500px");
        ol.addComponent(b);

        addButtons(ol);
        p.setContent(ol);

        /* VERTICAL */

        ol = new HorizontalLayout();
        ol.setMargin(true);
        ol.setCaption("Vertical");

        addButtons(ol);
        b = new Button("High button");
        b.setHeight("200px");
        ol.addComponent(b);

        p.setContent(ol);

    }

    private void addButtons(Layout ol) {
        ol.addComponent(getButton(ol, Alignment.TOP_LEFT));
        ol.addComponent(getButton(ol, Alignment.MIDDLE_CENTER));
        ol.addComponent(getButton(ol, Alignment.BOTTOM_RIGHT));

    }

    private Button getButton(Layout l, Alignment align) {
        Button b = new Button("Narrow Button - "
                + align.getHorizontalAlignment() + " - "
                + align.getVerticalAlignment());
        b.setWidth("100px");
        ((AlignmentHandler) l).setComponentAlignment(b, align);

        return b;

    }
}
