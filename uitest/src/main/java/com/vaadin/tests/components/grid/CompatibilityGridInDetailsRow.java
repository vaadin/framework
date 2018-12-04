package com.vaadin.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Component;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.v7.event.ItemClickEvent.ItemClickListener;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.RowReference;

@SuppressWarnings("deprecation")
public class CompatibilityGridInDetailsRow extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid fg = new Grid();
        fg.setId("grid1");
        fg.setSizeFull();
        fg.addColumn("col1", String.class);
        fg.addColumn("col2", String.class);
        fg.addRow("Temp 1", "Temp 2");
        fg.addRow("Temp 3", "Temp 4");
        fg.setDetailsGenerator(new Grid.DetailsGenerator() {
            @Override
            public Component getDetails(RowReference rowReference) {
                Grid gd = new Grid();
                gd.setId("grid2");
                gd.setSizeFull();
                gd.addHeaderRowAt(0);
                gd.addColumn("Column 1", String.class);
                gd.addColumn("Column 2", String.class);
                gd.getColumn("Column 2").setHidable(true);
                gd.addColumn("Column 3", String.class);
                gd.addColumn("Column 4", String.class);
                gd.addColumn("id", Integer.class);
                gd.addRow("Nicolaus Copernicus", "Nicolaus Copernicus",
                        "Nicolaus Copernicus", "Nicolaus Copernicus", 1543);
                gd.addRow("Nicolaus Copernicus", "Nicolaus Copernicus",
                        "Nicolaus Copernicus", "Nicolaus Copernicus", 1543);
                gd.addRow("Nicolaus Copernicus", "Nicolaus Copernicus",
                        "Nicolaus Copernicus", "Nicolaus Copernicus", 1543);
                gd.addRow("Nicolaus Copernicus", "Nicolaus Copernicus",
                        "Nicolaus Copernicus", "Nicolaus Copernicus", 1543);

                return gd;
            }
        });

        fg.addItemClickListener(new ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.isDoubleClick()) {
                    Object itemId = event.getItemId();
                    fg.setDetailsVisible(itemId, !fg.isDetailsVisible(itemId));
                }
            }
        });

        getLayout().addComponent(fg);
    }

    @Override
    protected String getTestDescription() {
        return "A nested Grid with multirow header should display all headers and "
                + "opening the details row shouldn't cause a client-side exception "
                + "when the nested Grid has hideable rows.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7674;
    }

}
