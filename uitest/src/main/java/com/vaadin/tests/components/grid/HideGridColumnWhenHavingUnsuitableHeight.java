package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.ButtonRenderer;

@Theme("valo")
@Widgetset("com.vaadin.DefaultWidgetSet")
public class HideGridColumnWhenHavingUnsuitableHeight extends AbstractTestUI {

    private Grid<SampleBean> grid;

    public static class SampleBean {

        private String col1;
        private String col2;

        public SampleBean() {
        }

        public String getCol1() {
            return col1;
        }

        public void setCol1(String col1) {
            this.col1 = col1;
        }

        public String getCol2() {
            return col2;
        }

        public void setCol2(String col2) {
            this.col2 = col2;
        }
    }

    @Override
    protected void setup(VaadinRequest vaadinRequest) {
        grid = new Grid<>();
        grid.setItems(generateData(50));

        grid.addColumn(SampleBean::getCol1).setWidth(1600);
        grid.addColumn(SampleBean::getCol2);
        grid.addColumn(t -> "Button1").setRenderer(new ButtonRenderer<>());

        grid.getColumns().forEach(c -> c.setHidable(true));

        grid.setWidth("100%");
        grid.setHeight("425px");

        addComponent(grid);
    }

    private List<SampleBean> generateData(int rows) {
        List<SampleBean> list = new ArrayList<>();
        for (int y = 0; y < rows; ++y) {
            SampleBean sampleBean = new SampleBean();
            sampleBean.setCol1("Row " + y + " Column 1");
            sampleBean.setCol2("Row " + y + " Column 2");
            list.add(sampleBean);
        }
        return list;
    }

}
