package com.vaadin.tests.components.window;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Window;
import com.vaadin.v7.ui.Grid;

public class GridInWindow extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        final Grid grid = new Grid();

        grid.addColumn("Hidable column").setHidable(true);
        grid.addRow("Close and reopen and it vanishes");

        Button popupButton = new Button("Open PopUp", event -> {
            Window subWindow = new Window("Sub-window");
            subWindow.setContent(grid);
            subWindow.setWidth(600, Unit.PIXELS);
            subWindow.setWidth(400, Unit.PIXELS);
            getUI().addWindow(subWindow);
        });

        addComponent(popupButton);

    }

}
