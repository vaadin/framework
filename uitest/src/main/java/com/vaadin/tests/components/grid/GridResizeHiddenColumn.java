package com.vaadin.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.util.Person;
import com.vaadin.ui.Grid;

@SuppressWarnings("serial")
public class GridResizeHiddenColumn extends GridEditorUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<Person> grid = createGrid();
        grid.setItems(createTestData());
        addComponent(grid);

        grid.setColumns("firstName", "phone", "lastName", "zip");
        grid.getColumn("firstName").setHidable(true);
        grid.getColumn("phone").setHidable(true).setHidden(true);
        grid.getColumn("lastName").setHidable(true).setHidden(true);
        grid.getColumn("zip").setHidable(true);

        addComponent(grid);

        grid.addColumnResizeListener(
                event -> log(String.format("Column resized: id=%s, width=%s",
                        event.getColumn().getId(),
                        event.getColumn().getWidth())));
    }

    @Override
    protected String getTestDescription() {
        return "Resize columns and then make hidden column visible. The originally hidden column should have an extended width.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 19826;
    }
}
