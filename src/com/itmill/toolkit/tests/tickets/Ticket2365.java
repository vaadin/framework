package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class Ticket2365 extends Application {

    @Override
    public void init() {

        final Window mainWin = new Window(getClass().getSimpleName());
        setMainWindow(mainWin);

        VerticalLayout lo = new VerticalLayout();
        lo.setSizeFull();
        mainWin.setLayout(lo);

        final Panel p = createMultilevelPanel(5, (Panel) null);

        Button b = new Button("Toggle parent level size");
        lo.addComponent(b);
        b.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                if (p.getWidth() > 0) {
                    p.setSizeUndefined();
                } else {
                    p.setSizeFull();
                }
            }
        });

        lo.addComponent(p);

        lo.setExpandRatio(p, 1);

    }

    private Panel createMultilevelPanel(int i, Panel panel) {
        if (panel == null) {
            panel = new Panel("Panel level " + i);
            panel.setSizeFull();
            panel.getLayout().setSizeFull();
        }
        Panel p = new Panel("Panel level " + i--);
        p.getLayout().setSizeFull();
        p.setSizeFull();
        panel.addComponent(p);
        if (i > 0) {
            createMultilevelPanel(i, p);
        }
        return panel;
    }

}
