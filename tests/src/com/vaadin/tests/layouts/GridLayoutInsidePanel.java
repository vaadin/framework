package com.vaadin.tests.layouts;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

public class GridLayoutInsidePanel extends TestBase {

    @Override
    protected String getDescription() {
        return "The first Panel contains a VerticalLayout, which contains a GridLayout, which contains a Label. The second panel directly contains a GridLayout, which contains a Label. Both should be rendered in the same way.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2652;
    }

    @Override
    protected void setup() {
        {
            GridLayout gl = new GridLayout(1, 1);
            gl.setSizeUndefined();
            gl.addComponent(new Label(
                    "A label which defines the size of the GL"));

            Panel p = new Panel("Panel 1");
            p.getLayout().setMargin(false);
            p.setSizeUndefined();
            p.getLayout().setSizeUndefined();

            p.addComponent(gl);
            addComponent(p);
        }
        {
            GridLayout gl = new GridLayout(1, 1);
            gl.setSizeUndefined();
            gl.addComponent(new Label(
                    "A label which defines the size of the GL"));

            Panel p = new Panel("Panel 2", gl);
            p.getLayout().setMargin(false);
            p.setSizeUndefined();
            p.getLayout().setSizeUndefined();

            addComponent(p);
        }
    }

}
