package com.vaadin.tests.layouts;

import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.Layout;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;

public class TreeWithBordersInLayout extends AbstractTestCase {

    private static final Object CAPTION = "caption";

    @Override
    public void init() {
        Layout mainLayout = new VerticalLayout();
        mainLayout.setSizeUndefined();
        setMainWindow(new LegacyWindow("main window", mainLayout));

        setTheme("tests-tickets");

        Tree t = new Tree();
        t.addContainerProperty(CAPTION, String.class, "");
        t.setItemCaptionPropertyId(CAPTION);
        t.addItem("Item 1").getItemProperty(CAPTION).setValue("Item 1");

        t.setSizeUndefined();
        t.setStyleName("redblueborders");
        mainLayout.addComponent(t);

    }

    @Override
    protected String getDescription() {
        return "The tree consists of one node and has a 10px blue red border and a 10px red right border. The tree node should be visible between the borders.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3915;
    }

}
