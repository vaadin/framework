package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

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
