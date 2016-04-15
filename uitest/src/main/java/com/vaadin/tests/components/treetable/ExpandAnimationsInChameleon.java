package com.vaadin.tests.components.treetable;

import com.vaadin.data.Container.Hierarchical;
import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TreeTable;

public class ExpandAnimationsInChameleon extends TestBase {

    @Override
    protected void setup() {
        Layout grid = getGridLayout();

        TreeTable t = getTreeTable(null);
        grid.addComponent(t);

        t = getTreeTable("small");
        grid.addComponent(t);

        t = getTreeTable("big");
        grid.addComponent(t);

        t = getTreeTable("striped");
        grid.addComponent(t);

        t = getTreeTable("small striped");
        grid.addComponent(t);

        t = getTreeTable("big striped");
        grid.addComponent(t);

        t = getTreeTable("strong");
        grid.addComponent(t);

        t = getTreeTable("small strong");
        grid.addComponent(t);

        t = getTreeTable("big strong");
        grid.addComponent(t);

        t = getTreeTable("borderless");
        grid.addComponent(t);

        t = getTreeTable("striped");
        t.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_HIDDEN);
        t.setCaption(t.getCaption() + ", hidden headers");
        grid.addComponent(t);

        addComponent(grid);
    }

    GridLayout getGridLayout() {
        GridLayout grid = new GridLayout(3, 1) {
            @Override
            public void addComponent(Component c) {
                super.addComponent(c);
                setComponentAlignment(c, Alignment.MIDDLE_CENTER);
                if (c.getStyleName() != "") {
                    ((AbstractComponent) c).setDescription(c.getClass()
                            .getSimpleName()
                            + ".addStyleName(\""
                            + c.getStyleName() + "\")");
                } else {
                    ((AbstractComponent) c).setDescription("new "
                            + c.getClass().getSimpleName() + "()");
                }
            }
        };
        grid.setWidth("100%");
        grid.setSpacing(true);
        grid.setMargin(true);
        grid.setStyleName("preview-grid");
        return grid;
    }

    public TreeTable getTreeTable(String style) {
        TreeTable t = new TreeTable();
        t.setAnimationsEnabled(true);
        t.setWidth("250px");
        t.setPageLength(5);
        t.setSelectable(true);
        t.setColumnCollapsingAllowed(true);
        t.setColumnReorderingAllowed(true);

        if (style != null) {
            t.setStyleName(style);
            t.setCaption("Table.addStyleName(\"" + style + "\")");
        }

        t.addContainerProperty("First", String.class, null);
        t.addContainerProperty("Second", String.class, null);
        t.addContainerProperty("Third", String.class, null);

        for (int j = 1; j < 100; j++) {
            t.addItem(new Object[] { "Foo " + j, "Bar " + j, "Lorem " + j }, j);
        }
        Hierarchical hc = t.getContainerDataSource();
        hc.setChildrenAllowed(2, true);
        for (int j = 4; j < 100; j++) {
            hc.setParent(j, 2);
        }

        t.setColumnIcon("Third", new ThemeResource(
                "../runo/icons/16/document.png"));
        t.select(1);

        return t;
    }

    @Override
    protected String getDescription() {
        return "Colors should be correct while animating expands/collapses";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6723;
    }

}
