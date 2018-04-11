package com.vaadin.tests.components.gridlayout;

import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.TextField;

/**
 * Test for grid required indicator location within slots.
 */
public class GridLayoutRequiredIndicatorLocation
        extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Page.getCurrent().getStyles()
                .add(".allow-overflow { overflow: visible; }");
        Page.getCurrent().getStyles()
                .add(".colored { background: lime; overflow: visible; }");
        Page.getCurrent().getStyles()
                .add(".pink { background: pink; overflow: visible; }");
        Page.getCurrent().getStyles()
                .add(".v-gridlayout-slot { border: 1px solid red; }");

        GridLayout rootLayout = new GridLayout(2, 2);
        rootLayout.addStyleName("allow-overflow");
        rootLayout.setSpacing(true);
        addComponent(rootLayout);

        GridLayout gridLayout = createGridLayout(false);
        gridLayout.addStyleName("allow-overflow");
        gridLayout.addStyleName("colored");
        rootLayout.addComponent(gridLayout);

        // for reference, VerticalLayout does it right
        VerticalLayout vl = createVerticalLayout(false);
        vl.addStyleName("allow-overflow");
        vl.addStyleName("colored");
        rootLayout.addComponent(vl);

        GridLayout gridLayout2 = createGridLayout(true);
        gridLayout2.addStyleName("allow-overflow");
        gridLayout2.addStyleName("colored");
        rootLayout.addComponent(gridLayout2);

        VerticalLayout vl2 = createVerticalLayout(true);
        vl2.addStyleName("allow-overflow");
        vl2.addStyleName("colored");
        rootLayout.addComponent(vl2);
    }

    private VerticalLayout createVerticalLayout(boolean useCaption) {
        VerticalLayout vl = new VerticalLayout();
        vl.setMargin(false);
        vl.setSpacing(false);
        vl.setWidth("320px");

        addLabel(vl, "200px", Alignment.MIDDLE_LEFT, useCaption);
        addLabel(vl, "40%", Alignment.MIDDLE_LEFT, useCaption);
        addLabel(vl, "100%", Alignment.MIDDLE_LEFT, useCaption);
        addLabel(vl, "200px", Alignment.MIDDLE_CENTER, useCaption);
        addLabel(vl, "30%", Alignment.MIDDLE_CENTER, useCaption);
        addLabel(vl, "100%", Alignment.MIDDLE_CENTER, useCaption);
        addLabel(vl, "200px", Alignment.MIDDLE_RIGHT, useCaption);
        addLabel(vl, "50%", Alignment.MIDDLE_RIGHT, useCaption);
        addLabel(vl, "100%", Alignment.MIDDLE_RIGHT, useCaption);
        return vl;
    }

    private GridLayout createGridLayout(boolean useCaption) {
        GridLayout gridLayout = new GridLayout();
        gridLayout.setColumns(2);
        gridLayout.setWidth("500px");
        gridLayout.setColumnExpandRatio(0, 0);
        gridLayout.setColumnExpandRatio(1, 1);

        addLabel(gridLayout, "200px", Alignment.MIDDLE_LEFT, useCaption);
        addLabel(gridLayout, "40%", Alignment.MIDDLE_LEFT, useCaption);
        addLabel(gridLayout, "100%", Alignment.MIDDLE_LEFT, useCaption);
        addLabel(gridLayout, "200px", Alignment.MIDDLE_CENTER, useCaption);
        addLabel(gridLayout, "30%", Alignment.MIDDLE_CENTER, useCaption);
        addLabel(gridLayout, "100%", Alignment.MIDDLE_CENTER, useCaption);
        addLabel(gridLayout, "200px", Alignment.MIDDLE_RIGHT, useCaption);
        addLabel(gridLayout, "50%", Alignment.MIDDLE_RIGHT, useCaption);
        addLabel(gridLayout, "100%", Alignment.MIDDLE_RIGHT, useCaption);
        return gridLayout;
    }

    private void addLabel(GridLayout layout, String width, Alignment alignment,
            boolean useCaption) {
        Label label = new Label("Align " + alignment.getHorizontalAlignment()
                + " width " + width);
        label.setWidth("180px");
        label.addStyleName("pink");
        layout.addComponent(label);

        // TODO also test with captions
        TextField field = new TextField(useCaption ? "caption" : null);
        field.setRequired(true);
        field.setWidth(width);
        layout.addComponent(field);
        layout.setComponentAlignment(field, alignment);
    }

    private void addLabel(VerticalLayout layout, String width,
            Alignment alignment, boolean useCaption) {
        TextField field = new TextField(useCaption ? "caption" : null);
        field.setRequired(true);
        field.setWidth(width);
        layout.addComponent(field);
        layout.setComponentAlignment(field, alignment);
    }

    @Override
    protected Integer getTicketNumber() {
        return 18418;
    }

    @Override
    protected String getTestDescription() {
        return "If a GridLayout slot has a size smaller than 100%, the required indicators should be at the end of each field";
    }

}
