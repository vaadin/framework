/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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
