package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.ExpandLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Button.ClickListener;

public class Ticket2021 extends Application {

    private ExpandLayout layout;

    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);

        Panel p = new Panel();
        p.setCaption("ExpandLayout");
        p.setWidth(500);
        p.setHeight(500);
        p.getLayout().setSizeFull();
        layout = new ExpandLayout();
        p.getLayout().addComponent(layout);
        w.getLayout().addComponent(p);

        createUI(layout);
    }

    private void createUI(ExpandLayout layout) {
        Label l = new Label("Label");
        Button b = new Button("Enable/disable caption and watch button move",
                new ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        System.out.println("Enable caption");

                        if (Ticket2021.this.layout.getCaption() == null) {
                            Ticket2021.this.layout
                                    .setCaption("Expand layout caption");
                        } else {
                            Ticket2021.this.layout.setCaption(null);
                        }

                    }

                });
        Label l2 = new Label("This should always be visible");

        layout.addComponent(l);
        layout.addComponent(b);
        layout.addComponent(l2);

        layout.expand(l);
    }
}
