package com.vaadin.tests.dnd;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.grid.DropMode;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.GridDragSource;
import com.vaadin.ui.components.grid.GridDropTarget;
import com.vaadin.ui.dnd.DragSourceExtension;
import com.vaadin.ui.dnd.DropTargetExtension;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class DragSourcesInTabSheet extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        TabSheet tabSheet = new TabSheet();
        addComponent(tabSheet);

        tabSheet.addTab(new VerticalLayout(createLabels()))
                .setCaption("Labels");

        Button dsButton = new Button("DragSource");
        new DragSourceExtension<>(dsButton)
                .addDragEndListener(event -> log("drag end button"));
        Button dtButton = new Button("DropTarget");
        new DropTargetExtension<>(dtButton).addDropListener(event -> log("drop "
                + event.getDragSourceComponent().orElse(null) + " on button"));
        tabSheet.addTab(new VerticalLayout(dsButton, dtButton))
                .setCaption("Buttons");

        tabSheet.addTab(new VerticalLayout(createGrids())).setCaption("Grids");

        addComponent(new Button("Open window", event -> openWindow()));
    }

    private Label[] createLabels() {
        Label dragSource = new Label("DragSource");
        new DragSourceExtension<>(dragSource)
                .addDragEndListener(event -> log("drag end label"));
        Label dropTarget = new Label("DropTarget");
        new DropTargetExtension<>(dropTarget).addDropListener(event -> log(
                "drop " + event.getDragSourceComponent().orElse(null)
                        + " on label"));
        return new Label[] { dragSource, dropTarget };
    }

    private Grid[] createGrids() {
        Grid<Person> dsGrid = new Grid<>(Person.class);
        dsGrid.setItems(Person.createTestPerson1(), Person.createTestPerson2());
        new GridDragSource<>(dsGrid).addGridDragEndListener(event -> log(
                "drag end " + event.getDraggedItems().iterator().next()));
        Grid<Person> dtGrid = new Grid<>(Person.class);
        dtGrid.setItems(Person.createTestPerson1(), Person.createTestPerson2());
        new GridDropTarget<>(dtGrid, DropMode.BETWEEN)
                .addGridDropListener(event -> log("drop on grid row "
                        + event.getDropTargetRow().orElse(null) + " "
                        + event.getDragData().orElse(null)));
        return new Grid[] { dsGrid, dtGrid };
    }

    private void openWindow() {
        Window window = new Window("Window with drag sources");
        VerticalLayout layout = new VerticalLayout();
        layout.addComponents(createLabels());
        layout.addComponents(createGrids());
        window.setContent(layout);
        addWindow(window);
    }

    @Override
    protected String getTestDescription() {
        return "Verify that removing drag source and drop target components in a tabsheet/window works";
    }

}
