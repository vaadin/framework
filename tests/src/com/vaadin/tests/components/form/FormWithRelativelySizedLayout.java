package com.vaadin.tests.components.form;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Form;
import com.vaadin.ui.Label;

public class FormWithRelativelySizedLayout extends TestBase {

    @Override
    protected String getDescription() {
        return "Forms mainlayouts relative height should be everyting left out from footer and possible borders/paddings. ";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3488;
    }

    @Override
    protected void setup() {

        Form f = new Form();
        f.setCaption("Form, full size");

        f.setWidth("100%");
        f.setHeight("100%");

        Label l = new Label(
                "This green label should consume all available space, pushing ok button to bottom of the view");
        l.setSizeFull();

        CssLayout lo = new CssLayout() {
            @Override
            protected String getCss(Component c) {
                return "background: green;color:red;";
            }
        };
        lo.setSizeFull();

        f.setLayout(lo);
        lo.addComponent(l);

        f.getFooter().addComponent(new Button("OK button"));

        getLayout().setSizeFull();
        getLayout().addComponent(f);
    }

}
