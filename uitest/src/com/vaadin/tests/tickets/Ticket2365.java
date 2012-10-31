package com.vaadin.tests.tickets;

import com.vaadin.LegacyApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class Ticket2365 extends LegacyApplication {

    @Override
    public void init() {

        final LegacyWindow mainWin = new LegacyWindow(getClass()
                .getSimpleName());
        setMainWindow(mainWin);

        VerticalLayout lo = new VerticalLayout();
        lo.setSizeFull();
        mainWin.setContent(lo);

        final Panel p = createMultilevelPanel(5, (Panel) null);

        Button b = new Button("Toggle parent level size");
        lo.addComponent(b);
        b.addListener(new Button.ClickListener() {
            @Override
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
            panel.getContent().setSizeFull();
        }
        Panel p = new Panel("Panel level " + i--);
        p.getContent().setSizeFull();
        p.setSizeFull();
        panel.addComponent(p);
        if (i > 0) {
            createMultilevelPanel(i, p);
        }
        return panel;
    }

}
