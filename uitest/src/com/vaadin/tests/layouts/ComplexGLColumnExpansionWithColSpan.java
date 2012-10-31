package com.vaadin.tests.layouts;

import com.vaadin.server.Sizeable;
import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class ComplexGLColumnExpansionWithColSpan extends AbstractTestCase {
    private int cols;

    @Override
    protected String getDescription() {
        return "Buttons should stay stacked on left when clicking new button";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5227;
    }

    @Override
    public void init() {
        final VerticalLayout mainLayout = new VerticalLayout();

        mainLayout.setSpacing(true);
        mainLayout.setMargin(true);
        mainLayout.setHeight(100, Sizeable.UNITS_PERCENTAGE);
        mainLayout.setWidth(100, Sizeable.UNITS_PERCENTAGE);
        setMainWindow(new LegacyWindow("Vaadin Test", mainLayout));

        cols = 1;
        final GridLayout gl = new GridLayout(cols, 3);
        gl.setWidth("1000px");
        // textfield spreads across all cols
        final TextField textfield = new TextField();
        textfield.setWidth(100, Sizeable.UNITS_PERCENTAGE);
        Button b1 = new Button("new button");
        Button b2 = new Button("nothing");
        gl.addComponent(textfield, 0, 0);
        gl.addComponent(b1, 0, 1);
        gl.addComponent(b2, 0, 2);
        b1.setWidth(270, Sizeable.UNITS_PIXELS);
        b2.setWidth(270, Sizeable.UNITS_PIXELS);
        b1.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                cols++;
                gl.setColumns(cols);
                Button b1 = new Button("new button" + cols);
                Button b2 = new Button("nothing" + cols);
                gl.addComponent(b1, cols - 1, 1);
                gl.addComponent(b2, cols - 1, 2);
                b1.setWidth(270, Sizeable.UNITS_PIXELS);
                b2.setWidth(270, Sizeable.UNITS_PIXELS);
                // adjust expand ratios...
                if (cols > 0) {
                    // next to last colum 0, last column 100
                    gl.setColumnExpandRatio(cols - 2, 0);
                    gl.setColumnExpandRatio(cols - 1, 100);
                }
                gl.removeComponent(textfield);
                gl.addComponent(textfield, 0, 0, cols - 1, 0);
            }
        });
        gl.setSizeFull();
        mainLayout.addComponent(gl);
        mainLayout.setExpandRatio(gl, 100);
        Button restart = new Button("restart");
        mainLayout.addComponent(restart);
        restart.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                close();
            }
        });

    }

}
