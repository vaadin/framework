package com.vaadin.tests.components.grid;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.PopupView;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridPopupView extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest vaadinRequest) {
        Grid<String> grid = new Grid<>();
        grid.setItems("Foo", "Bar", "Baz");

        PopupView popupView = new PopupView(
                "Show grid (click me multiple times)", grid);
        popupView.setHideOnMouseOut(false);

        grid.addColumn(str -> str).setCaption("Something");

        addComponent(popupView);
    }

}
