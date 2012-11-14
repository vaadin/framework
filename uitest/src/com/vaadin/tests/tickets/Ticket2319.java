package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;

public class Ticket2319 extends LegacyApplication {

    @Override
    public void init() {
        LegacyWindow mainw = new LegacyWindow();
        setMainWindow(mainw);

        mainw.addComponent(new Label(
                "This test has somewhat invalid layouts in it to detect analyzy layout function in debug dialog"));

        HorizontalLayout hl = new HorizontalLayout();
        Panel panel = buildPanel("p1");
        Panel panel2 = buildPanel("p2");
        hl.addComponent(panel);
        hl.addComponent(panel2);

        mainw.addComponent(hl);

        hl = new HorizontalLayout();
        panel = buildPanel("p1");
        panel.setSizeUndefined();
        panel.setHeight("100%");
        panel2 = buildPanel("p2");
        panel2.setSizeUndefined();
        panel2.setHeight("100%");

        hl.addComponent(panel);
        hl.addComponent(panel2);
        mainw.addComponent(hl);

        HorizontalSplitPanel sp = new HorizontalSplitPanel();

        VerticalLayout first = new VerticalLayout();
        first.addComponent(new Label("first"));
        VerticalLayout second = new VerticalLayout();
        second.addComponent(new Label("second"));

        sp.setFirstComponent(first);
        sp.setSecondComponent(second);

        VerticalSplitPanel sp2 = new VerticalSplitPanel();
        Label label = new Label("first");
        label.setSizeFull();
        sp2.setFirstComponent(label);
        sp2.setSecondComponent(sp);

        sp2.setHeight("200px");

        mainw.addComponent(sp2);

        mainw.addComponent(new Button("click me to save split panel state"));
    }

    private Panel buildPanel(String caption) {
        VerticalLayout pl = new VerticalLayout();
        pl.setMargin(true);
        return new Panel(caption, pl);
    }

}
