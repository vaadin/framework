package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.HorizontalLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.SplitPanel;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Window;

public class Ticket2319 extends Application {

    @Override
    public void init() {
        Window mainw = new Window();
        setMainWindow(mainw);

        mainw
                .addComponent(new Label(
                        "This test has somewhat invalid layouts in it to detect analyzy layout function in debug dialog"));

        HorizontalLayout hl = new HorizontalLayout();
        Panel panel = new Panel("p1");
        Panel panel2 = new Panel("p2");
        hl.addComponent(panel);
        hl.addComponent(panel2);

        mainw.addComponent(hl);

        hl = new HorizontalLayout();
        panel = new Panel("p1");
        panel.setSizeUndefined();
        panel.setHeight("100%");
        panel2 = new Panel("p2");
        panel2.setSizeUndefined();
        panel2.setHeight("100%");

        hl.addComponent(panel);
        hl.addComponent(panel2);
        mainw.addComponent(hl);

        SplitPanel sp = new SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL);

        VerticalLayout first = new VerticalLayout();
        first.addComponent(new Label("first"));
        VerticalLayout second = new VerticalLayout();
        second.addComponent(new Label("second"));

        sp.setFirstComponent(first);
        sp.setSecondComponent(second);

        SplitPanel sp2 = new SplitPanel();
        Label label = new Label("first");
        label.setSizeFull();
        sp2.setFirstComponent(label);
        sp2.setSecondComponent(sp);

        sp2.setHeight("200px");

        mainw.addComponent(sp2);

        mainw.addComponent(new Button("click me to save split panel state"));
    }

}
