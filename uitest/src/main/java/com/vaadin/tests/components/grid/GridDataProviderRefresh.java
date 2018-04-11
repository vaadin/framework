package com.vaadin.tests.components.grid;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import com.vaadin.data.provider.AbstractBackEndDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;

/**
 * Tests that Grid refresh doesn't get stuck when filtering doesn't match row
 * requests.
 *
 * @author Vaadin Ltd
 */
public class GridDataProviderRefresh extends SimpleGridUI {

    private volatile Boolean filter = false;

    private Stream<AtomicReference<String>> fetchValue(Boolean filter) {
        if (filter) {
            return Arrays
                    .asList("One", String.valueOf(System.currentTimeMillis()))
                    .stream().map(s -> new AtomicReference<>(s));
        } else {
            return Stream.empty();
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        AbstractBackEndDataProvider<AtomicReference<String>, Object> dataProvider = new AbstractBackEndDataProvider<AtomicReference<String>, Object>() {
            @Override
            protected Stream<AtomicReference<String>> fetchFromBackEnd(
                    Query<AtomicReference<String>, Object> query) {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return fetchValue(filter);
            }

            @Override
            protected int sizeInBackEnd(
                    Query<AtomicReference<String>, Object> query) {
                return (int) fetchValue(filter).count();
            }
        };

        Grid<AtomicReference<String>> grid = new Grid<>();
        grid.setDataProvider(dataProvider);
        grid.addColumn(AtomicReference::get);
        addComponent(grid);

        Button button = new Button("Refresh");
        button.addClickListener(event1 -> {
            filter = new Boolean(!filter);
            dataProvider.refreshAll();
        });
        addComponent(button);
    }

    @Override
    protected String getTestDescription() {
        return "Grid refreshAll shouldn't get stuck when filtering doesn't match row requests "
                + "(randomly triggered when button is clicked fast enough "
                + "several times in a row, especially if using old and slow Windows)";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10038;
    }
}
