package com.vaadin.tests.components.treegrid;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.grid.DropMode;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.data.bean.HierarchicalTestBean;
import com.vaadin.ui.TreeGrid;
import com.vaadin.ui.components.grid.TreeGridDragSource;
import com.vaadin.ui.components.grid.TreeGridDropTarget;

@Theme("valo")
@Widgetset("com.vaadin.DefaultWidgetSet")
public class TreeGridDragAndDrop extends AbstractTestUIWithLog {
    @Override
    protected void setup(VaadinRequest request) {
        getUI().setMobileHtml5DndEnabled(true);

        TreeGrid<HierarchicalTestBean> grid;
        grid = new TreeGrid<>();
        grid.setSizeFull();
        grid.addColumn(HierarchicalTestBean::toString).setCaption("String")
                .setId("string");
        grid.addColumn(HierarchicalTestBean::getDepth).setCaption("Depth")
                .setId("depth").setDescriptionGenerator(
                t -> "Hierarchy depth: " + t.getDepth());
        grid.addColumn(HierarchicalTestBean::getIndex)
                .setCaption("Index on this depth").setId("index");
        grid.setHierarchyColumn("string");
        grid.setDataProvider(new LazyHierarchicalDataProvider(3, 2));

        grid.setId("testComponent");

        TreeGridDragSource<HierarchicalTestBean> dragSource = new TreeGridDragSource<>(
                grid);
        TreeGridDropTarget<HierarchicalTestBean> dropTarget = new TreeGridDropTarget<>(
                grid, DropMode.ON_TOP_OR_BETWEEN);

        dropTarget.addTreeGridDropListener(event -> {
            log("depth=" + event.getDropTargetRowDepth().orElse(null)
                    + ", collapsed=" + event.isDropTargetRowCollapsed()
                    .orElse(null));
        });

        addComponent(grid);
    }
}
