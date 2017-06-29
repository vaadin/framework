package com.vaadin.tests.data;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;

import java.util.stream.IntStream;

public class GridChangeItems extends AbstractTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        Grid<Integer> grid = new Grid<>();
        grid.addColumn(value -> value);

        Button buttonReplace = new Button("Replace Items", event -> {
            replaceItems(grid);
        });

        Button buttonScroll = new Button("Scroll to #98", event -> {
            grid.scrollTo(98);
        });

        Button buttonDelete = new Button("Delete Item", event -> {
            ListDataProvider<Integer> dataProvider =
                    (ListDataProvider<Integer>) grid.getDataProvider();
            dataProvider.getItems().remove(dataProvider.getItems().stream().skip(100).findFirst().get());
            dataProvider.refreshAll();
        });
        replaceItems(grid);

        addComponents(buttonReplace, buttonScroll,buttonDelete, grid);
    }

    private void replaceItems(Grid<Integer> grid) {
        grid.setItems(IntStream.range(0, 500).mapToObj(Integer::valueOf));
    }
}