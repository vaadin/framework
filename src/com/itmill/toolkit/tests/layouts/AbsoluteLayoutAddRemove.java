package com.itmill.toolkit.tests.layouts;

import com.itmill.toolkit.tests.components.TestBase;
import com.itmill.toolkit.ui.AbsoluteLayout;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Layout;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class AbsoluteLayoutAddRemove extends TestBase {

    @Override
    protected String getDescription() {
        return "Tests that addComponent() and removeComponent() works";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2915;
    }

    @Override
    protected void setup() {
        Layout main = getLayout();

        final Label l = new Label("A Label");
        final AbsoluteLayout al = new AbsoluteLayout();
        al.setWidth("300px");
        al.setHeight("200px");
        main.addComponent(al);

        final Button b = new Button("Add", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                if (l.getParent() == null) {
                    al.addComponent(l);
                    event.getButton().setCaption("Remove");
                } else {
                    al.removeComponent(l);
                    event.getButton().setCaption("Add");
                }

            }

        });
        main.addComponent(b);

    }

}
