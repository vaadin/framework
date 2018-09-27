package com.vaadin.tests.components.grid;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.ItemClick;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.DetailsGenerator;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.components.grid.ItemClickListener;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridInDetailsRow extends SimpleGridUI {

    int index = 0;

    @Override
    protected void setup(VaadinRequest request) {
        getLayout().addComponent(createGrid());
    }

    @Override
    protected Grid<Person> createGrid() {
        Grid<Person> grid = super.createGrid();
        grid.setId("grid" + index);
        ++index;
        grid.setSizeFull();
        grid.setHeightUndefined();
        grid.setHeightMode(HeightMode.UNDEFINED);

        HeaderRow hr0 = grid.addHeaderRowAt(0);
        hr0.getCell(grid.getColumns().get(0)).setText("Name");
        hr0.getCell(grid.getColumns().get(1)).setText("Age");
        HeaderRow hr1 = grid.getDefaultHeaderRow();
        hr1.getCell(grid.getColumns().get(0)).setText("Foo");
        hr1.getCell(grid.getColumns().get(1)).setText("Bar");
        grid.getColumns().get(1).setHidable(true);

        grid.setDetailsGenerator(new DetailsGenerator<Person>() {
            @Override
            public Component apply(Person t) {
                VerticalLayout layout = new VerticalLayout();
                layout.setMargin(true);
                Grid<Person> gd = createGrid();
                layout.addComponent(gd);
                return layout;
            }
        });

        grid.addItemClickListener(new ItemClickListener<Person>() {

            @Override
            public void itemClick(ItemClick<Person> event) {
                if (event.getMouseEventDetails().isDoubleClick()) {
                    Person item = event.getItem();
                    grid.setDetailsVisible(item, !grid.isDetailsVisible(item));
                }
            }
        });
        return grid;
    }

    @Override
    protected String getTestDescription() {
        return "A nested Grid with multirow header should display all headers and "
                + "the body rows shouldn't get stuck to default row height.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7674;
    }
}
