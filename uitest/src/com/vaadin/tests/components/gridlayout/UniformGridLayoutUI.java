package com.vaadin.tests.components.gridlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class UniformGridLayoutUI extends UI {

    @Override
    protected void init(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);
        layout.setSpacing(true);
        boolean hide = (request.getParameter("collapse") != null);
        layout.addComponent(createGridWithoutGridBuilder(1, hide));
        layout.addComponent(createGridWithoutGridBuilder(2, hide));
        layout.addComponent(createGridWithoutGridBuilder(3, hide));
    }

    private GridLayout createGridWithoutGridBuilder(int rows, boolean collapse) {
        GridLayout grid = new GridLayout(30, 3);
        grid.setWidth("100%");
        // grid.setMargin(true);
        // grid.setSpacing(true);
        grid.setHideEmptyRowsAndColumns(collapse);

        // Row 1
        Label label1 = new Label("Row1");
        label1.setWidth(100.0F, Unit.PERCENTAGE);
        grid.addComponent(label1, 0, 0, 10, 0);
        grid.setComponentAlignment(label1, Alignment.MIDDLE_LEFT);

        TextField textField1 = new TextField();
        textField1.setWidth(100.0F, Unit.PERCENTAGE);
        grid.addComponent(textField1, 12, 0, 14, 0);
        grid.setComponentAlignment(textField1, Alignment.MIDDLE_LEFT);

        if (rows < 2) {
            return grid;
        }

        // Row 2
        Label label2 = new Label("Row2");
        label2.setWidth(100.0F, Unit.PERCENTAGE);
        grid.addComponent(label2, 0, 1, 10, 1);
        grid.setComponentAlignment(label2, Alignment.MIDDLE_LEFT);

        TextField textField2 = new TextField();
        textField2.setWidth(100.0F, Unit.PERCENTAGE);
        grid.addComponent(textField2, 12, 1, 20, 1);
        grid.setComponentAlignment(textField2, Alignment.MIDDLE_LEFT);

        if (rows < 3) {
            return grid;
        }

        // Row 3
        Label label3 = new Label("Row3");
        label3.setWidth(100.0F, Unit.PERCENTAGE);
        grid.addComponent(label3, 0, 2, 10, 2);
        grid.setComponentAlignment(label3, Alignment.MIDDLE_LEFT);

        TextField textField3 = new TextField();
        textField3.setWidth(100.0F, Unit.PERCENTAGE);
        grid.addComponent(textField3, 12, 2, 29, 2);
        grid.setComponentAlignment(textField3, Alignment.MIDDLE_LEFT);

        return grid;
    }

}