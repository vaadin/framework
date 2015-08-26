package com.vaadin.tests.themes.valo;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;

@Theme("valo")
public class GridDisabled extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Grid grid = new Grid();

        grid.addColumn("foo", String.class);
        grid.addRow("Foo");
        grid.select(grid.addRow("Bar"));

        addComponent(grid);

        addButton("Disable", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                grid.setEnabled(!grid.isEnabled());
            }
        });
    }
}
