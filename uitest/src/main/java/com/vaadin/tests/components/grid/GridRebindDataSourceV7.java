package com.vaadin.tests.components.grid;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Grid;

@Widgetset("com.vaadin.v7.Vaadin7WidgetSet")
public class GridRebindDataSourceV7 extends AbstractTestUI {
    private Grid grid;
    private IndexedContainer container = new IndexedContainer();

    @Override
    protected void setup(VaadinRequest request) {
        container.addContainerProperty("name", String.class, null);
        container.addItem("test").getItemProperty("name").setValue("test");
        grid = new Grid();
        grid.setContainerDataSource(container);
        grid.setEditorEnabled(true);
        addComponent(grid);

        Button button = new Button("Change container",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        IndexedContainer container = new IndexedContainer();
                        container.addContainerProperty("age", Integer.class,
                                null);
                        container.addItem("first").getItemProperty("age")
                                .setValue(45);
                        grid.removeAllColumns();
                        grid.setContainerDataSource(container);
                    }
                });
        button.setId("changeContainer");
        addComponent(button);
    }

}
