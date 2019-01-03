package com.vaadin.tests.components.grid;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.grid.DropLocation;
import com.vaadin.shared.ui.grid.DropMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.components.grid.GridDragSource;
import com.vaadin.ui.components.grid.GridDropTarget;

import java.util.ArrayList;
import java.util.List;

@Theme("valo")
@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridDropCriteriaScript extends AbstractTestUI {

    class GridItem {
        public final String caption;
        public final DropLocation dropLocation;

        public GridItem(String caption, DropLocation dropLocation) {
            this.caption = caption;
            this.dropLocation = dropLocation;
        }

        public String getCaption() {
            return caption;
        }

        public DropLocation getDropLocation() {
            return dropLocation;
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        getUI().setMobileHtml5DndEnabled(true);

        final Label label = new Label("<h1>Test for existance of targetElement "
                + "and dropLocation in criteriaScript</h1>"
                + "<p>Drag one of the grid items.</p>"
                + "<p>While dragging, hints in form of lines show "
                + "where the item is allowed to be dropped.</p>"
                + "<p>Test passed:" + "<ul>"
                + "<li>ABOVE: Only a line above the item is displayed while dragging over the item</li>"
                + "<li>BELOW: Only a line below the item is displayed while dragging over the item</li>"
                + "<li>ON_TOP: Only a border around the item is displayed while dragging over the item</li>"
                + "</ul>" + "</p>" + "<p>Test failed:" + "<ul>"
                + "<li>otherwise</li>" + "</ul>" + "</p>", ContentMode.HTML);

        final Grid<GridItem> grid = new Grid<>();
        grid.addColumn(GridItem::getCaption);
        grid.setStyleGenerator(
                gridItem -> "dropLocation-" + gridItem.getDropLocation());

        new GridDragSource<>(grid);

        final GridDropTarget<GridItem> dropTarget = new GridDropTarget<>(grid,
                DropMode.ON_TOP_OR_BETWEEN);
        dropTarget.setDropCriteriaScript(
                "return targetElement.classList.contains('dropLocation-' + dropLocation);");

        grid.setItems(createItems());

        addComponents(label, grid);
    }

    private List<GridItem> createItems() {
        final ArrayList<GridItem> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            for (DropLocation dropLocation : new DropLocation[] {
                    DropLocation.ON_TOP, DropLocation.ABOVE,
                    DropLocation.BELOW }) {
                list.add(new GridItem(i + ": " + dropLocation.name(),
                        dropLocation));
            }
        }
        return list;
    }

}
