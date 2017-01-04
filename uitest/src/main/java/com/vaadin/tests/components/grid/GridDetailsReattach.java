package com.vaadin.tests.components.grid;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import org.apache.tools.ant.taskdefs.Java;

public class GridDetailsReattach extends UI {

    @Override
    protected void init(VaadinRequest request) {

        final HorizontalLayout root = new HorizontalLayout();
        setContent(root);

        final VerticalLayout verticalMain = new VerticalLayout();

        final VerticalLayout layoutWithGrid = new VerticalLayout();

        final IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("foo", String.class, "foo");
        container.addItem("bar");

        Grid grid = new Grid("Grid");
        grid.setHeight("150px");
        grid.setContainerDataSource(container);
        grid.setDetailsGenerator(new Grid.DetailsGenerator() {
            @Override
            public Component getDetails(Grid.RowReference rowReference) {
                return new Label("AnyDetails");
            }
        });
        grid.setDetailsVisible(container.getItemIds().iterator().next(), true);
        layoutWithGrid.addComponent(grid);

        Button addCaptionToLayoutWithGridButton = new Button("Add caption to 'layoutWithGrid' layout");
        addCaptionToLayoutWithGridButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                layoutWithGrid.setCaption("Caption added to 'layoutWithGrid' layout");
            }
        });
        layoutWithGrid.addComponent(addCaptionToLayoutWithGridButton);

        verticalMain.addComponent(layoutWithGrid);

        root.addComponent(verticalMain);
    }
}
