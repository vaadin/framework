package com.vaadin.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.util.Person;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.components.grid.FooterCell;
import com.vaadin.ui.components.grid.FooterRow;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;

public class GridHeaderStyleNames extends GridEditorUI {

    private HeaderCell nameHeaderCell;
    private HeaderCell mergedCityCountryCell;
    private FooterCell nameFooterCell;
    private HeaderRow headerRow;
    private FooterRow footerRow;

    private boolean stylesOn = true;

    @Override
    protected void setup(VaadinRequest request) {
        Grid<Person> grid = createGrid();
        grid.setItems(createTestData());
        grid.setSelectionMode(SelectionMode.MULTI);

        nameHeaderCell = grid.getDefaultHeaderRow().getCell("firstName");
        grid.getDefaultHeaderRow().setStyleName("foo");
        headerRow = grid.prependHeaderRow();
        mergedCityCountryCell = headerRow.join("city", "street");
        mergedCityCountryCell.setText("Merged cell");

        grid.setColumns("email", "firstName", "city", "street", "lastName",
                "zip");
        addComponent(grid);

        footerRow = grid.appendFooterRow();
        nameFooterCell = footerRow.getCell("firstName");

        getPage().getStyles().add(
                ".name {background-image: linear-gradient(to bottom,green 2%, #efefef 98%) !important;}");
        getPage().getStyles().add(
                ".valo .v-grid-header .v-grid-cell.city-country {background-image: linear-gradient(to bottom,yellow 2%, #efefef 98%) !important;}");
        getPage().getStyles().add(
                ".valo .v-grid-footer .v-grid-cell.name-footer {background-image: linear-gradient(to bottom,blue 2%, #efefef 98%) !important;}");
        getPage().getStyles().add(
                ".valo .v-grid .v-grid-row.custom-row > * {background-image: linear-gradient(to bottom,purple 2%, #efefef 98%);}");

        setCellStyles(true);
        setRowStyles(true);

        Button button = new Button("Toggle styles");
        button.addClickListener(event -> {
            setCellStyles(!stylesOn);
            setRowStyles(!stylesOn);
            stylesOn = !stylesOn;
        });
        addComponent(button);
    }

    protected void setCellStyles(boolean set) {
        if (set) {
            nameHeaderCell.setStyleName("name");
            nameFooterCell.setStyleName("name-footer");
            mergedCityCountryCell.setStyleName("city-country");
        } else {
            nameHeaderCell.setStyleName(null);
            nameFooterCell.setStyleName(null);
            mergedCityCountryCell.setStyleName(null);
        }

    }

    protected void setRowStyles(boolean set) {
        if (set) {
            headerRow.setStyleName("custom-row");
            footerRow.setStyleName("custom-row");
        } else {
            headerRow.setStyleName(null);
            footerRow.setStyleName(null);
        }

    }
}
