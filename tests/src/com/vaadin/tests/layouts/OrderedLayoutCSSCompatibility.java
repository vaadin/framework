package com.vaadin.tests.layouts;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

public class OrderedLayoutCSSCompatibility extends TestBase {

    @Override
    protected String getDescription() {
        return "This test is to make sure that spacing/margins in OrderedLayout is still backwards compatible";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2463;
    }

    @Override
    protected void setup() {
        HorizontalLayout l = new HorizontalLayout();
        l.setMargin(true);
        l.setSpacing(true);
        l.addComponent(new TextField("abc"));
        l.addComponent(new TextField("def"));

        addComponent(l);

    }

}
