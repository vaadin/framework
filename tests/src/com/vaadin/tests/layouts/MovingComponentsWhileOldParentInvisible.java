package com.vaadin.tests.layouts;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class MovingComponentsWhileOldParentInvisible extends TestBase {

    @Override
    protected void setup() {
        final VerticalLayout vl = new VerticalLayout();
        final Label lab = new Label("Label in VL");
        vl.addComponent(lab);

        final GridLayout gl = new GridLayout(1, 1);
        final Label lab2 = new Label("Label in GL");
        gl.addComponent(lab2);

        final CssLayout cl = new CssLayout();
        cl.setWidth("100%");
        final Label lab3 = new Label("Label in CL");
        cl.addComponent(lab3);

        Button but1 = new Button("But 1", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                vl.setVisible(!vl.isVisible());
                gl.setVisible(!gl.isVisible());
                cl.setVisible(!cl.isVisible());
                if (!vl.isVisible()) {
                    getLayout().addComponent(lab);
                    getLayout().addComponent(lab2);
                    getLayout().addComponent(lab3);
                } else {
                    vl.addComponent(lab);
                    gl.addComponent(lab2);
                    cl.addComponent(lab3);
                }
            }
        });

        getLayout().addComponent(vl);
        getLayout().addComponent(gl);
        getLayout().addComponent(cl);
        getLayout().addComponent(but1);
    }

    @Override
    protected String getDescription() {
        return "Client side layouts can easily have a bug where its internal data structures gets messed up when child components from it are moved forth and back when it is invisible (registered, but renders are ignored until becomes visible again). Things are especially easy to mess up when the layout uses wrapper widget over each component (like VOrderedLayout and VGridLayout does). This tests Vertical (Ordered), Grid and CssLayout.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5372;
    }

}
