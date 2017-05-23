package com.vaadin.tests.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Stream;

import com.vaadin.data.provider.AbstractDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;

public class DataProviderRefresh extends AbstractTestUI {

    public static class Bean implements Serializable {

        private String value;
        private final int id;

        public Bean(String value, int id) {
            this.value = value;
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public int getId() {
            return id;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "{ " + value + ", " + id + " }";
        }
    }

    /**
     * A dummy data provider for testing item replacement and stale elements.
     */
    public class ReplaceListDataProvider
            extends AbstractDataProvider<Bean, Void> {

        private final List<Bean> backend;

        public ReplaceListDataProvider(List<Bean> items) {
            backend = items;
        }

        @Override
        public void refreshItem(Bean item) {
            if (replaceItem(item)) {
                super.refreshItem(item);
            }
        }

        private boolean replaceItem(Bean item) {
            for (int i = 0; i < backend.size(); ++i) {
                if (getId(backend.get(i)).equals(getId(item))) {
                    if (backend.get(i).equals(item)) {
                        return false;
                    }
                    backend.set(i, item);
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean isInMemory() {
            return true;
        }

        @Override
        public int size(Query<Bean, Void> t) {
            return backend.size();
        }

        @Override
        public Stream<Bean> fetch(Query<Bean, Void> query) {
            return backend.stream().skip(query.getOffset())
                    .limit(query.getLimit());
        }

        @Override
        public Object getId(Bean item) {
            return item.getId();
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        Grid<Bean> grid = new Grid<>();
        ArrayList<Bean> arrayList = new ArrayList<>();
        Bean foo = new Bean("Foo", 10);
        arrayList.add(foo);
        arrayList.add(new Bean("Baz", 11));
        ReplaceListDataProvider dataProvider = new ReplaceListDataProvider(
                arrayList);
        grid.setDataProvider(dataProvider);
        grid.addColumn(Object::toString).setCaption("toString");
        addComponent(grid);
        addComponent(new Button("Replace item",
                e -> dataProvider.refreshItem(new Bean("Bar", 10))));
        addComponent(new Button("Select old", e -> grid.select(foo)));
    }

}
