package com.vaadin.tests.elements.gridlayout;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridLayoutUI extends AbstractTestUI {

    public static final String ONE_ROW_ONE_COL = "oneRowOneCol";
    public static final String TEN_ROWS_TEN_COLS = "tenRowsTenCols";

    @Override
    protected void setup(VaadinRequest request) {
        GridLayout oneRowZeroCols = new GridLayout(1, 1);
        oneRowZeroCols.setId(ONE_ROW_ONE_COL);
        addComponent(oneRowZeroCols);

        GridLayout tenTimesTen = new GridLayout(10, 10);
        tenTimesTen.addComponent(new Label("5-5"), 5, 5);
        tenTimesTen.addComponent(new Button("7-7 8-8"), 7, 7, 8, 8);
        tenTimesTen.setId(TEN_ROWS_TEN_COLS);
        addComponent(tenTimesTen);
    }

}
