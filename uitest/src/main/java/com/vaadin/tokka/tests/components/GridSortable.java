package com.vaadin.tokka.tests.components;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tokka.server.communication.data.DataSource;
import com.vaadin.tokka.server.communication.data.InMemoryDataSource;
import com.vaadin.tokka.ui.components.grid.Grid;
import com.vaadin.ui.Button;

public class GridSortable extends AbstractTestUI {

    private static class CustomDataSource extends InMemoryDataSource<Bean> {

        private static List<Bean> list = Arrays.asList(new Bean("Foo", 100),
                new Bean("Bar", 200), new Bean("Bar", 100));
        private static Map<String, Comparator<Bean>> comparators = new HashMap<>();

        static {
            comparators.put("value", Comparator.comparing(Bean::getValue));
            comparators.put("intVal", Comparator.comparing(Bean::getIntVal));
        }

        public CustomDataSource() {
            super(
                    query -> {
                        Stream<Bean> beans = list.stream();

                        Comparator<Bean> comparator = query
                                .getSortOrders()
                                .stream()
                                .map(s -> {
                                    Comparator<Bean> c = comparators.get(s
                                            .getSorted());
                                    if (c == null) {
                                        throw new IllegalArgumentException(
                                                "Could not sort by '"
                                                        + s.getSorted() + "'");
                                    }
                                    return s.getDirection() == SortDirection.ASCENDING ? c
                                            : c.reversed();
                                })
                                .reduce((x, y) -> 0, Comparator::thenComparing);

                        return beans.sorted(comparator);
                    });
        }

        @Override
        public boolean isInMemory() {
            return false;
        }
    }

    private InMemoryDataSource<Bean> dataSource;

    @Override
    protected void setup(VaadinRequest request) {
        Grid<Bean> grid = new Grid<Bean>();
        grid.addColumn("Sortable String", Bean::getValue).setSortProperty(
                "value");
        grid.addColumn("Sortable Integer", Bean::getIntVal).setSortProperty(
                "intVal");
        grid.addColumn("Not sortable toString()", Bean::toString)
                .setSortable(false).setSortProperty("Not Sortable.");

        dataSource = DataSource.create(new Bean("Foo", 0), new Bean("Bar", 1),
                new Bean("Bar", 0));
        grid.setDataSource(dataSource);

        addComponent(grid);

        addComponent(new Button("Change DataSource", e -> {
            if (grid.getDataSource() != dataSource) {
                grid.setDataSource(dataSource);
            } else {
                grid.setDataSource(new CustomDataSource());
            }
        }));
    }
}
