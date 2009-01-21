package com.itmill.toolkit.tests.layouts;

import com.itmill.toolkit.tests.components.TestBase;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.TextField;

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
        OrderedLayout l = new OrderedLayout(
                OrderedLayout.ORIENTATION_HORIZONTAL);
        l.setMargin(true);
        l.setSpacing(true);
        l.addComponent(new TextField("abc"));
        l.addComponent(new TextField("def"));

        addComponent(l);

    }

}
