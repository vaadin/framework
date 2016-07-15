package com.vaadin.tokka.tests.components;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tokka.server.communication.data.BackEndDataSource;
import com.vaadin.tokka.server.communication.data.DataSource;
import com.vaadin.tokka.server.communication.data.InMemoryDataSource;
import com.vaadin.tokka.server.communication.data.SortOrder;
import com.vaadin.tokka.ui.components.grid.Grid;
import com.vaadin.ui.Button;

public class GridSortable extends AbstractTestUI {

    private static List<Bean> list = Arrays.asList(new Bean("Foo", 100),
            new Bean("Bar", 200), new Bean("Bar", 100));

    private static Map<String, Comparator<Bean>> comparators = new HashMap<>();
    static {
        comparators.put("value", Comparator.comparing(Bean::getValue));
        comparators.put("intVal", Comparator.comparing(Bean::getIntVal));
    }

    private InMemoryDataSource<Bean> inMemory = DataSource.create(
            new Bean("Foo", 0), new Bean("Bar", 1),
            new Bean("Bar", 0));

    private BackEndDataSource<Bean> backend = new BackEndDataSource<>(query -> {
        Optional<Comparator<Bean>> comparator = fromSortOrders(
                query.getSortOrders());
        return comparator.map(list.stream()::sorted).orElse(list.stream());
    });

    private static Optional<Comparator<Bean>> fromSortOrders(
            List<SortOrder<String>> sos) {
        return sos
                .stream()
                .map(so -> {
                    Comparator<Bean> c = comparators.get(so.getSorted());
                    return so.getDirection() == SortDirection.ASCENDING ? c
                            : c.reversed();
                })
                .reduce(Comparator::thenComparing);
    }

    @Override
    protected void setup(VaadinRequest request) {
        Grid<Bean> grid = new Grid<Bean>();
        grid.addColumn("Sortable String", Bean::getValue).setSortProperty(
                "value");
        grid.addColumn("Sortable Integer", Bean::getIntVal).setSortProperty(
                "intVal");
        grid.addColumn("Not sortable toString()", Bean::toString)
                .setSortable(false).setSortProperty("Not Sortable.");

        grid.setDataSource(inMemory);

        addComponent(grid);

        addComponent(new Button(
                "Change DataSource",
                e -> {
                    if (grid.getDataSource() != inMemory) {
                        grid.setDataSource(inMemory);
                    } else {
                        grid.setDataSource(backend);
                    }
                }));
    }
}
