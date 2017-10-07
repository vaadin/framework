package com.vaadin.tests.fonticon;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridLayoutOnFontLoad extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        GridLayout gl = new GridLayout(2, 3);

        // grid of components
        addComponent(gl);

        // Basic components, caption icon only
        gl.addComponents(new Button("Button"), new CheckBox("CheckBox"),
                new Label("Label"), new TextArea("TextArea"),
                new Label("Label"));

        // it should not be necessary to click a grid row to make the GridLayout
        // have correct sizes
        Grid<String> grid = new Grid<>("Grid");
        grid.setItems("item 1", "item 2", "item 3");
        grid.addColumn(string -> string).setCaption("column 1");
        // vaadin/framework#8207
        grid.setHeightByRows(3);
        // grid.setHeight("150px");
        gl.addComponent(grid);

    }

    @Override
    protected String getTestDescription() {
        return "GridLayout should have the correct size and no overlapping components from the beginning";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
