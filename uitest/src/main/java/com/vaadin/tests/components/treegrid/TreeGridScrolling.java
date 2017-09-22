package com.vaadin.tests.components.treegrid;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.data.bean.HierarchicalTestBean;
import com.vaadin.ui.TreeGrid;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class TreeGridScrolling extends AbstractTestUI {

    public static final int DEFAULT_NODES = 20;
    public static final int DEFAULT_DEPTH = 3;
    public static final String NODES_PARAMETER = "nodes";
    public static final String DEPTH_PARAMETER = "depth";

    @Override
    protected void setup(VaadinRequest request) {
        int depth = DEFAULT_DEPTH;
        if (request.getParameter(DEPTH_PARAMETER) != null) {
            depth = Integer.parseInt(request.getParameter(DEPTH_PARAMETER));
        }
        int nodes = DEFAULT_NODES;
        if (request.getParameter(NODES_PARAMETER) != null) {
            nodes = Integer.parseInt(request.getParameter(NODES_PARAMETER));
        }

        TreeGrid<HierarchicalTestBean> grid = new TreeGrid<>();
        grid.setSizeFull();
        grid.addColumn(HierarchicalTestBean::toString).setCaption("String")
                .setId("string");
        grid.addColumn(HierarchicalTestBean::getDepth).setCaption("Depth")
                .setId(DEPTH_PARAMETER);
        grid.addColumn(HierarchicalTestBean::getIndex)
                .setCaption("Index on this depth").setId("index");
        grid.setHierarchyColumn("string");
        grid.setDataProvider(new LazyHierarchicalDataProvider(nodes, depth));

        addComponent(grid);
    }

}
