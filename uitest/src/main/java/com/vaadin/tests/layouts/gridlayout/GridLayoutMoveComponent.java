package com.vaadin.tests.layouts.gridlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

public class GridLayoutMoveComponent extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final GridLayout grid = new GridLayout(2, 3);
        grid.setCaption("Fixed size grid");
        grid.setWidth("300px");
        grid.setHeight("100px");
        addComponent(grid);

        final Label l = new Label("100% label");
        final Button b = new Button("100px button");
        b.setWidth("100px");
        final TextField tf = new TextField("Undef textfield");

        // Adding component to grid
        grid.addComponent(l, 0, 0);
        grid.addComponent(b, 0, 1);
        grid.addComponent(tf, 0, 2);

        addComponent(new Button("Shift label right",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        // Moving component from 0,0 -> 1,0
                        grid.removeComponent(l);
                        grid.addComponent(l, 1, 0);
                    }
                }));

        addComponent(new Button("Shift button right",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        grid.removeComponent(b);
                        grid.addComponent(b, 1, 1);
                    }
                }));

        addComponent(new Button("Shift text field right",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        grid.removeComponent(tf);
                        grid.addComponent(new Label("I'm on left"), 0, 2);
                        grid.addComponent(tf, 1, 2);
                    }
                }));
    }

    @Override
    protected String getTestDescription() {
        return "Click the buttons below the GridLayout to move the components to the right. Should definitely work no matter what.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5525;
    }

}
