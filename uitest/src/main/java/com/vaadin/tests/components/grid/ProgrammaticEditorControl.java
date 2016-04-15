package com.vaadin.tests.components.grid;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid;

@SuppressWarnings("serial")
// @Push
public class ProgrammaticEditorControl extends AbstractTestUIWithLog {

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

        Button button = new Button("Edit", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                grid.editItem("test");
            }
        });
        addComponent(button);
        Button button2 = new Button("Cancel", new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                grid.cancelEditor();
            }
        });
        addComponent(button2);

    }

}