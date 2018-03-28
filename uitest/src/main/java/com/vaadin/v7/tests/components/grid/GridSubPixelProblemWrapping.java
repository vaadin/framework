package com.vaadin.v7.tests.components.grid;

import java.util.Random;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.SelectionMode;

public class GridSubPixelProblemWrapping extends AbstractTestUI {

    Random r = new Random();

    public static class DataObject {
        String foo;
        String Bar;

        public DataObject(Random r) {
            foo = r.nextInt() + "";
            Bar = r.nextInt() + "";
        }

        public DataObject(String foo, String bar) {
            this.foo = foo;
            Bar = bar;
        }

        public String getFoo() {
            return foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }

        public String getBar() {
            return Bar;
        }

        public void setBar(String bar) {
            Bar = bar;
        }

    }

    Button button = new Button("Click", new ClickListener() {
        @Override
        public void buttonClick(ClickEvent event) {
            addDAO();
        }
    });

    private BeanItemContainer<DataObject> container;
    private int counter = 0;

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(button);
        container = new BeanItemContainer<>(DataObject.class);
        container.addBean(new DataObject("Foo", "Bar"));
        Grid grid = new Grid(container);
        grid.getColumn("foo").setWidth(248.525);
        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.setEditorEnabled(true);
        grid.setWidth("500px");

        addComponent(grid);
    }

    private void addDAO() {
        counter++;
        container.addBean(new DataObject("Foo" + counter, "Bar" + counter));

    }
}
