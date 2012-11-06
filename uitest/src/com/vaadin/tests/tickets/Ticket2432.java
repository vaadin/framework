package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Layout.AlignmentHandler;
import com.vaadin.ui.Layout.SpacingHandler;
import com.vaadin.ui.LegacyWindow;

public class Ticket2432 extends LegacyApplication {

    @Override
    public void init() {

        LegacyWindow w = new LegacyWindow();
        setMainWindow(w);
        w.getContent().setSizeFull();
        ((SpacingHandler) w.getContent()).setSpacing(true);

        Layout layout = new GridLayout(3, 3);
        populateLayout(layout);
        w.addComponent(layout);
        layout = new HorizontalLayout();
        populateLayout(layout);
        w.addComponent(layout);

    }

    private static Alignment[] alignments = new Alignment[] {
            Alignment.TOP_LEFT, Alignment.TOP_CENTER, Alignment.TOP_RIGHT,
            Alignment.MIDDLE_LEFT, Alignment.MIDDLE_CENTER,
            Alignment.MIDDLE_RIGHT, Alignment.BOTTOM_LEFT,
            Alignment.BOTTOM_CENTER, Alignment.BOTTOM_RIGHT };

    private void populateLayout(Layout layout) {
        layout.setSizeFull();
        for (int i = 0; i < 9; i++) {
            Label l = new Label("M");
            Alignment a = alignments[i];
            l.setCaption(a.getHorizontalAlignment() + " "
                    + a.getVerticalAlignment() + " " + a.getBitMask());
            layout.addComponent(l);
            ((AlignmentHandler) layout).setComponentAlignment(l, a);
        }

    }

}
