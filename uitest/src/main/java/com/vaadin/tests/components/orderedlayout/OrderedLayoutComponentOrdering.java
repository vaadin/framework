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
        addComponent(hl);

        Button addFirst = new Button("add first");
        addFirst.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                hl.addComponent(new Button(String.valueOf(++counter)), 0);
                hl.addComponent(new Button(String.valueOf(++counter)), 1);
            }
        });
        addComponent(addFirst);

        Button add = new Button("add second");
        add.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                hl.addComponent(new Button(String.valueOf(++counter)), 1);
                hl.addComponent(new Button(String.valueOf(++counter)), 2);
            }
        });
        addComponent(add);

        Button addThird = new Button("add third");
        addThird.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                hl.addComponent(new Button(String.valueOf(++counter)), 2);
                hl.addComponent(new Button(String.valueOf(++counter)), 3);
            }
        });
        addComponent(addThird);

        Button move = new Button("move last to first");
        move.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                hl.addComponentAsFirst(hl.getComponent(hl.getComponentCount() - 1));
            }
        });
        addComponent(move);

        Button swap = new Button("move forth to second");
        swap.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                hl.addComponent(hl.getComponent(3), 1);
            }
        });
        addComponent(swap);

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
