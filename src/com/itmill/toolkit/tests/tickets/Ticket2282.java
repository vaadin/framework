package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.FormLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class Ticket2282 extends Application {

    private FormLayout layout1;
    private FormLayout layout2;

    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        setTheme("tests-tickets");
        w.getLayout().setSizeUndefined();

        layout1 = new FormLayout();
        layout1.setSizeUndefined();
        layout1.setStyleName("borders");
        Label label = new Label(
                "This should not be wider than this label + reserved error space");
        label.setCaption("A caption");
        layout1.addComponent(label);
        w.addComponent(layout1);

        layout2 = new FormLayout();
        layout2.setWidth("500px");
        layout2.setStyleName("borders");
        label = new Label("This should be 500px wide");
        label.setCaption("A caption");
        layout2.addComponent(label);
        w.addComponent(layout2);

        Button b = new Button("Swap", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                if (layout1.getWidth() < 0.0) {
                    layout1.setWidth("500px");
                    layout2.setWidth(null);
                } else {
                    layout1.setWidth(null);
                    layout2.setWidth("500px");
                }
            }

        });
        w.addComponent(b);
    }

}
