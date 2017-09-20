package com.vaadin.tests.components.grid;

import java.util.stream.Stream;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.DataProviderListener;
import com.vaadin.data.provider.Query;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.Registration;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;

public class DataCommunicatorInfiniteLoop extends AbstractTestUI {

    private static class CustomDataProvider
            implements DataProvider<String, Void> {

        private boolean sendRealCount = false;

        @Override
        public boolean isInMemory() {
            return false;
        }

        @Override
        public int size(Query<String, Void> query) {
            if (sendRealCount) {
                return 1;
            } else {
                sendRealCount = true;
                return 2;
            }
        }

        @Override
        public Stream<String> fetch(Query<String, Void> query) {
            return Stream.of("one item").skip(query.getOffset());
        }

        @Override
        public void refreshItem(String item) {
        }

        @Override
        public void refreshAll() {
        }

        @Override
        public Registration addDataProviderListener(
                DataProviderListener<String> listener) {
            return () -> {
            };
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        Grid<String> grid = new Grid<>(new CustomDataProvider());
        grid.addColumn(string -> string);
        addComponent(grid);
    }
}
