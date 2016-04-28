package com.vaadin.tests.tooltip;

import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.LoremIpsum;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.TextField;

public class LongTooltip extends TestBase {
    private int tooltipCount = 0;

    @Override
    public void setup() {

        GridLayout gl = new GridLayout(2, 2);
        gl.setSizeFull();
        TextField f1 = createField();
        TextField f2 = createField();
        TextField f3 = createField();
        TextField f4 = createField();
        gl.addComponent(f1);
        gl.addComponent(f2);
        gl.addComponent(f3);
        gl.addComponent(f4);

        gl.setComponentAlignment(f1, Alignment.TOP_LEFT);
        gl.setComponentAlignment(f2, Alignment.TOP_RIGHT);
        gl.setComponentAlignment(f3, Alignment.BOTTOM_LEFT);
        gl.setComponentAlignment(f4, Alignment.BOTTOM_RIGHT);

        getLayout().setSizeFull();
        getLayout().addComponent(gl);

    }

    private TextField createField() {
        final TextField field = new TextField();
        field.setDescription("Tooltip " + Integer.toString(tooltipCount++)
                + ": " + LoremIpsum.get(1000));
        return field;
    }

    @Override
    protected String getDescription() {
        return "For a given cursor position the tooltip should always appear with the same size in the same position. The tooltip should also always be completely on screen and not cause any scrollbars to appear.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7100;
    }
}
