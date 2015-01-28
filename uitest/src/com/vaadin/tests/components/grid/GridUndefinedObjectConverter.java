package com.vaadin.tests.components.grid;

import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;

public class GridUndefinedObjectConverter extends AbstractTestUI {
    private static class Pojo {
        private final String content;

        public Pojo(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return "Pojo:" + content;
        }
    }

    @Override
    @SuppressWarnings("all")
    protected void setup(VaadinRequest request) {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("pojo", Pojo.class, new Pojo("foo"));
        container.addContainerProperty("pojo object ", Object.class, new Pojo(
                "bar"));
        container.addContainerProperty("int", Integer.class, 1);
        container.addContainerProperty("int object", Object.class, 2);
        container.addContainerProperty("string", String.class, "foo");
        container.addContainerProperty("string object", Object.class, "bar");
        container.addItem();

        addComponent(new Grid(container));
    }
}
