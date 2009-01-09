package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Alignment;
import com.itmill.toolkit.ui.GridLayout;
import com.itmill.toolkit.ui.HorizontalLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Layout;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Layout.AlignmentHandler;
import com.itmill.toolkit.ui.Layout.SpacingHandler;

public class Ticket2432 extends Application {

    @Override
    public void init() {

        Window w = new Window();
        setMainWindow(w);
        w.getLayout().setSizeFull();
        ((SpacingHandler) w.getLayout()).setSpacing(true);

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
