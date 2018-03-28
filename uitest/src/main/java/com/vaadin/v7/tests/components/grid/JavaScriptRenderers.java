package com.vaadin.v7.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Grid;

public class JavaScriptRenderers extends AbstractReindeerTestUI {

    public static class MyBean {
        private int integer;
        private String string;

        public MyBean(int integer, String string) {
            super();
            this.integer = integer;
            this.string = string;
        }

        public int getInteger() {
            return integer;
        }

        public void setInteger(int integer) {
            this.integer = integer;
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("id", Integer.class, Integer.valueOf(0));
        container.addContainerProperty("bean", MyBean.class, null);
        container.addContainerProperty("string", String.class, "");

        for (int i = 0; i < 1000; i++) {
            Integer itemId = Integer.valueOf(i);
            Item item = container.addItem(itemId);
            item.getItemProperty("id").setValue(itemId);
            item.getItemProperty("bean")
                    .setValue(new MyBean(i + 1, Integer.toString(i - 1)));
            item.getItemProperty("string").setValue("string" + i);
        }

        Grid grid = new Grid(container);

        grid.getColumn("bean").setRenderer(new MyBeanJSRenderer());
        grid.getColumn("bean").setWidth(250);

        grid.getColumn("string").setRenderer(new JavaScriptStringRenderer());

        addComponent(grid);
    }

}
