package com.vaadin.tests.components.orderedlayout;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;

public class OrderedLayoutComponentOrdering extends TestBase {

    int counter = 0;

    @Override
    protected void setup() {

        // Initially horizontal layout has a,b
        Button a = new Button(String.valueOf(++counter));
        Button b = new Button(String.valueOf(++counter));
        final HorizontalLayout hl = new HorizontalLayout(a, b);
        hl.setCaption("Horizontal layout");
        hl.setSpacing(true);

        /*
         * Button adds c and d so the order becomes a,c,d,b
         */
        Button add = new Button("add");
        add.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                hl.addComponent(new Button(String.valueOf(++counter)), 1);
                hl.addComponent(new Button(String.valueOf(++counter)), 2);
            }
        });
        addComponent(hl);
        addComponent(add);
    }

    @Override
    protected String getDescription() {
        return "The order should be 1,3,4,2";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10154;
    }

}
