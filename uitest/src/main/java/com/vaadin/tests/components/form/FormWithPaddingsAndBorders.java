package com.vaadin.tests.components.form;

import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.TestUtils;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

public class FormWithPaddingsAndBorders extends TestBase {

    @Override
    protected void setup() {
        getLayout().setSizeFull();

        TestUtils.injectCSS(getMainWindow(),
                ".v-form{ border: 10px solid red; padding:10px;}");

        final Form f = new Form();
        addComponent(f);
        f.setSizeUndefined();
        f.getLayout().setSizeUndefined();

        f.addField("foo", new TextField("Foo"));
        f.addField("bar", new TextField("A bit longer field caption"));

        HorizontalLayout hl = new HorizontalLayout();
        hl.setWidth("100%");
        Button b = new Button("right aligned");
        hl.addComponent(b);
        hl.setComponentAlignment(b, Alignment.TOP_RIGHT);
        f.setFooter(hl);
    }

    @Override
    protected String getDescription() {
        return "Ensure that paddings set on form is considered in width calculations";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3710;
    }

}
