package com.vaadin.tests.components.grid;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.components.grid.FooterRow;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridHeaderFooterTooltip extends AbstractTestUI {

    private static final long serialVersionUID = -2787771187365766027L;

    private HeaderRow row;

    @Override
    protected void setup(VaadinRequest request) {
        Grid<Person> grid = new Grid<>(Person.class);
        grid.setWidth("600px");

        grid.setSelectionMode(SelectionMode.SINGLE);
        addComponent(grid);

        grid.getDefaultHeaderRow().getCell("firstName").setDescription(
                "Text: Header tooltip for <b>first</b> name", ContentMode.TEXT);
        grid.getDefaultHeaderRow().getCell("lastName").setDescription(
                "HTML: Header tooltip for <b>last</b> name", ContentMode.HTML);
        grid.getDefaultHeaderRow().getCell("deceased").setDescription(
                "PRE\nHeader tooltip for\n<b>deceased</b>",
                ContentMode.PREFORMATTED);
        grid.setDescription("Tooltip for the whole grid");

        FooterRow footer = grid.addFooterRowAt(0);
        footer.getCell("firstName").setDescription(
                "Text: Footer tooltip for <b>first</b> name", ContentMode.TEXT);
        footer.getCell("lastName").setDescription(
                "HTML: Footer tooltip for <b>last</b> name", ContentMode.HTML);
        footer.getCell("deceased").setDescription(
                "PRE\nFooter tooltip for\n<b>deceased</b>",
                ContentMode.PREFORMATTED);

        grid.setItems(Person.createTestPerson1(), Person.createTestPerson2());

        Button showHide = new Button("Hide firstName", event -> {
            Column<Person, ?> column = grid.getColumn("firstName");
            if (grid.getColumn("firstName") != null) {
                grid.removeColumn(column);
                event.getButton().setCaption("Show firstName");
            } else {
                grid.addColumn(Person::getFirstName).setId("firstName");
                grid.setColumnOrder(grid.getColumn("firstName"),
                        grid.getColumn("lastName"),
                        grid.getColumn("streetAddress"), grid.getColumn("zip"),
                        grid.getColumn("city"));

                event.getButton().setCaption("Hide firstName");
            }
        });
        showHide.setId("show_hide");

        Button join = new Button("Add Join header column", event -> {
            if (row == null) {
                row = grid.prependHeaderRow();
                HeaderCell joinedCell = row.join(
                        grid.getDefaultHeaderRow()
                                .getCell(grid.getColumn("firstName")),
                        grid.getDefaultHeaderRow()
                                .getCell(grid.getColumn("lastName")));
                joinedCell.setText("Full Name");
                joinedCell.setDescription("Full name tooltip");
            } else {
                grid.removeHeaderRow(row);
                row = null;
            }
        });
        join.setId("join");
        addComponent(new HorizontalLayout(showHide, join));
    }

    @Override
    protected String getTestDescription() {
        return "Grid for testing header re-rendering.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 17131;
    }

}
