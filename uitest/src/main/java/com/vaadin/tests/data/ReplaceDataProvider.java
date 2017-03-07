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
package com.vaadin.tests.data;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;

public class ReplaceDataProvider extends AbstractTestUI {

    private static class TestClass {
        public String someField;
        public int hash;

        public TestClass(int hash) {
            this.hash = hash;
            someField = "a";
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        Grid<TestClass> grid = new Grid<>();
        grid.addColumn(item -> item.someField);

        List<TestClass> listOfClasses = IntStream.range(0, 10)
                .mapToObj(TestClass::new).collect(Collectors.toList());
        for (int i = 0; i < 10; i++) {
            listOfClasses.add(new TestClass(10));
        }

        grid.setItems(listOfClasses);

        Button replaceBtn = new Button("replace data provider");
        replaceBtn.addClickListener(clickEvent -> {
            List<TestClass> newList = IntStream.range(0, 10)
                    .mapToObj(TestClass::new).collect(Collectors.toList());
            newList.get(0).someField = "b";
            grid.setItems(newList);
        });

        Button replaceAndSelectBtn = new Button(
                "replace data provider and select second");
        replaceAndSelectBtn.addClickListener(clickEvent -> {
            List<TestClass> newList = IntStream.range(0, 10)
                    .mapToObj(TestClass::new).collect(Collectors.toList());
            newList.get(0).someField = "b";
            grid.setItems(newList);
            grid.getSelectionModel().select(newList.get(1));
        });

        addComponents(replaceBtn, replaceAndSelectBtn, grid);
    }
}
