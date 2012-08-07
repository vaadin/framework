package com.vaadin.tests.layouts;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class GridLayoutNPE extends TestBase {

    @Override
    protected void setup() {
        final VerticalLayout lo = new VerticalLayout();

        final GridLayout gl = new GridLayout(2, 1);
        gl.setSpacing(true);

        final Label toRemove = new Label("First");
        gl.addComponent(toRemove);
        final Label toEdit = new Label("Second");
        gl.addComponent(toEdit);

        final Button b = new Button("remove 'First'");
        final Button b2 = new Button("edit 'Second'");
        b2.setVisible(false);

        lo.addComponent(gl);
        lo.addComponent(b);
        lo.addComponent(b2);

        b.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                gl.removeComponent(toRemove);

                // move another component to where the first was removed
                // before rendering to the client
                gl.removeComponent(toEdit);
                // this could also be the result of removeAllComponents()
                // followed by a loop of addComponent(c)
                gl.addComponent(toEdit, 0, 0);

                b.setVisible(false);
                b2.setVisible(true);

            }

        });

        b2.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                toEdit.setValue("Second (edited)");
            }

        });

        addComponent(lo);
    }

    @Override
    protected String getDescription() {
        return "VGridLayout throws an NPE, causing client side to crash";
    }

    @Override
    protected Integer getTicketNumber() {
        return 4019;
    }

}
