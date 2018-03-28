package com.vaadin.tests.components.listselect;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.ListSelect;

// FIXME this test should be updated once the provider supports CRUD operations #77
public class ListSelectAddRemoveItems extends AbstractTestUIWithLog {

    private ListDataProvider<String> dataProvider = new ListDataProvider<>(
            Collections.emptyList());
    private ListSelect<String> listSelect;

    @Override
    protected void setup(VaadinRequest request) {
        listSelect = new ListSelect<>("ListSelect", dataProvider);
        listSelect.setWidth("100px");
        listSelect.setRows(10);

        resetContainer();
        logContainer();

        addComponent(listSelect);
        addComponent(new Button("Reset", event -> {
            resetContainer();
            log.clear();
            logContainer();
        }));

        addComponent(new Button("Add first", event -> {
            List<String> list = dataProvider.fetch(new Query<>())
                    .collect(Collectors.toList());
            list.add(0, "first");
            dataProvider = new ListDataProvider<>(list);
            listSelect.setDataProvider(dataProvider);
            logContainer();
        }));

        addComponent(new Button("Add middle", event -> {
            List<String> list = dataProvider.fetch(new Query<>())
                    .collect(Collectors.toList());
            list.add(list.size() / 2, "middle");
            dataProvider = new ListDataProvider<>(list);
            listSelect.setDataProvider(dataProvider);
            logContainer();
        }));

        addComponent(new Button("Add last", event -> {
            List<String> list = dataProvider.fetch(new Query<>())
                    .collect(Collectors.toList());
            list.add("last");
            dataProvider = new ListDataProvider<>(list);
            listSelect.setDataProvider(dataProvider);
            logContainer();
        }));

        addComponent(new Button("Swap", event -> {
            List<String> list = dataProvider.fetch(new Query<>())
                    .collect(Collectors.toList());
            Collections.swap(list, 0, list.size() - 1);
            dataProvider = new ListDataProvider<>(list);
            listSelect.setDataProvider(dataProvider);

            logContainer();
        }));

        addComponent(new Button("Remove first", event -> {
            List<String> list = dataProvider.fetch(new Query<>())
                    .collect(Collectors.toList());
            list.remove(0);

            dataProvider = new ListDataProvider<>(list);
            listSelect.setDataProvider(dataProvider);

            logContainer();
        }));

        addComponent(new Button("Remove middle", event -> {
            List<String> list = dataProvider.fetch(new Query<>())
                    .collect(Collectors.toList());
            list.remove(list.size() / 2);
            dataProvider = new ListDataProvider<>(list);
            listSelect.setDataProvider(dataProvider);
            logContainer();
        }));

        addComponent(new Button("Remove last", event -> {
            List<String> list = dataProvider.fetch(new Query<>())
                    .collect(Collectors.toList());
            list.remove(list.size() - 1);

            dataProvider = new ListDataProvider<>(list);
            listSelect.setDataProvider(dataProvider);

            logContainer();
        }));

    }

    private void logContainer() {
        StringBuilder b = new StringBuilder();
        List<String> list = dataProvider.fetch(new Query<>())
                .collect(Collectors.toList());
        for (int i = 0; i < list.size(); i++) {
            Object id = list.get(i);
            if (i != 0) {
                b.append(", ");
            }
            b.append(id);
        }

        log(b.toString());
    }

    public void resetContainer() {
        dataProvider = new ListDataProvider<>(Arrays.asList("a", "b", "c"));
        listSelect.setDataProvider(dataProvider);
    }

    @Override
    protected String getTestDescription() {
        return "Test for verifying that items are added to and removed from the correct locations";
    }

}
