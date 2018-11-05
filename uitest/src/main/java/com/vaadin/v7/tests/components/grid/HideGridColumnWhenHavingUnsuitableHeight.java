package com.vaadin.v7.tests.components.grid;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.GeneratedPropertyContainer;
import com.vaadin.v7.data.util.PropertyValueGenerator;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.Column;
import com.vaadin.v7.ui.renderers.ButtonRenderer;

@Theme("valo")
public class HideGridColumnWhenHavingUnsuitableHeight extends AbstractTestUI {

    private Grid grid;

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

    @SuppressWarnings("serial")
    @Override
    protected void setup(VaadinRequest vaadinRequest) {
        grid = new Grid();

        BeanItemContainer<SampleBean> container = generateData(50);
        GeneratedPropertyContainer gpc = new GeneratedPropertyContainer(
                container);
        grid.setContainerDataSource(gpc);

        gpc.addGeneratedProperty("Button1",
                new PropertyValueGenerator<String>() {
                    @Override
                    public String getValue(Item item, Object itemId,
                            Object propertyId) {
                        return "Button 1";
                    }

                    @Override
                    public Class<String> getType() {
                        return String.class;
                    }
                });
        grid.getColumn("Button1").setRenderer(new ButtonRenderer());
        grid.getColumn("col1").setWidth(1600);
        for (Column gridCol : grid.getColumns()) {
            gridCol.setHidable(true);
        }
        grid.setWidth("100%");
        grid.setHeight("425px");

        grid.setColumns("col1", "col2", "Button1");

        addComponent(grid);
    }

    private BeanItemContainer<SampleBean> generateData(int rows) {
        BeanItemContainer<SampleBean> container = new BeanItemContainer<SampleBean>(
                SampleBean.class);
        for (int y = 0; y < rows; ++y) {
            SampleBean sampleBean = new SampleBean();
            sampleBean.setCol1("Row " + y + " Column 1");
            sampleBean.setCol2("Row " + y + " Column 2");
            container.addBean(sampleBean);
        }
        return container;
    }

}
