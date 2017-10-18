package com.vaadin.tests.components.grid;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.HtmlRenderer;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridSvgInCell extends AbstractTestUI {

    private static class DataObject {
        private String svg;

        public String getSvg() {
            return svg;
        }

        public void setSvg(String svg) {
            this.svg = svg;
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        Grid<DataObject> grid = new Grid<>();
        grid.addColumn(DataObject::getSvg).setCaption("SVG")
                .setRenderer(new HtmlRenderer(""));

        DataObject data = new DataObject();
        data.setSvg(
            "<svg width=\"100%\" height=\"20px\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">"
                + "<polygon id=\"bar_background_blue\" stroke=\"gray\" fill=\"#D6D6D6\" points=\"0 0,59 0,66 7,59 14,0 14\"></polygon>"
                + "<rect id=\"bar_blue\" x=\"1\" y=\"1\" width=\"0px\" height=\"13\" fill=\"#7298C0\"></rect>"
                + "</svg>");
        grid.setItems(data);

        addComponent(grid);
    }
}
