package com.vaadin.tests.minitutorials.v7_4;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.FooterCell;
import com.vaadin.v7.ui.Grid.HeaderCell;
import com.vaadin.v7.ui.Grid.HeaderRow;

public class ConfiguringGridHeadersAndFooters extends UI {
    @Override
    protected void init(VaadinRequest request) {
        Grid grid = new Grid(GridExampleHelper.createContainer());
        grid.setColumnOrder("name", "amount", "count");

        grid.getDefaultHeaderRow().getCell("amount")
                .setHtml("The <u>amount</u>");
        grid.getDefaultHeaderRow().getCell("count")
                .setComponent(new Button("Button caption"));

        grid.getColumn("name").setHeaderCaption("Bean name");

        HeaderRow extraHeader = grid.prependHeaderRow();
        HeaderCell joinedCell = extraHeader.join("amount", "count");
        joinedCell.setText("Joined cell");

        FooterCell footer = grid.appendFooterRow().join("name", "amount",
                "count");
        footer.setText("Right aligned footer");

        getPage().getStyles().add(".footer-right { text-align: right }");
        footer.setStyleName("footer-right");

        setContent(grid);
    }
}
