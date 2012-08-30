package com.vaadin.tests.components.absolutelayout;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.VerticalLayout;

public class MoveComponentsFromAbsoluteLayoutToInnerLayout extends TestBase {

    protected Button testButton;
    private AbsoluteLayout al;
    protected ComponentContainer vl;

    @Override
    protected void setup() {
        al = new AbsoluteLayout();
        al.setWidth("200px");
        al.setHeight("200px");

        testButton = new Button("Click to move to inner layout",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        vl.addComponent(testButton);
                    }
                });

        al.addComponent(testButton);

        vl = new VerticalLayout();
        al.addComponent(vl, "top: 100px");

        addComponent(al);

        Button b = new Button("Repaint inner layout",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        vl.markAsDirty();
                    }
                });

        addComponent(b);
    }

    @Override
    protected String getDescription() {
        return "Click the first button to move it from an outer layout to an inner. Then click the second button to repaint the inner layout.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6061;
    }

}
