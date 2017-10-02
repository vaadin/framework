package com.vaadin.tests.components.treegrid;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.data.bean.HierarchicalTestBean;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TreeGrid;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class TreeGridDoubleClick extends AbstractTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        TreeGrid<HierarchicalTestBean> grid = new TreeGrid<>();
        grid.addColumn(HierarchicalTestBean::toString).setCaption("String")
                .setId("string");
        grid.addColumn(HierarchicalTestBean::getDepth).setCaption("Depth")
                .setId("depth");
        grid.addColumn(HierarchicalTestBean::getIndex)
                .setCaption("Index on this depth").setId("index");
        grid.setHierarchyColumn("string");
        grid.setDataProvider(new LazyHierarchicalDataProvider(3, 3));

        grid.addItemClickListener(event -> {
            if (event.getMouseEventDetails().isDoubleClick()) {
                Notification.show("Double click");
            }
        });

        addComponent(grid);
    }
}
