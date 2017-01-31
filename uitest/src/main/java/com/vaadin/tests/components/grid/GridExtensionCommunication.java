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
package com.vaadin.tests.components.grid;

import java.util.stream.IntStream;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.tests.widgetset.client.grid.GridClickExtensionConnector;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.AbstractGridExtension;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;

import elemental.json.JsonObject;

@Widgetset(TestingWidgetSet.NAME)
public class GridExtensionCommunication extends AbstractTestUIWithLog {

    public class GridClickExtension extends AbstractGridExtension<Person> {

        public GridClickExtension(Grid<Person> grid) {
            extend(grid);
            registerRpc(new GridClickExtensionConnector.GridClickServerRpc() {

                @Override
                public void click(String row, String column,
                        MouseEventDetails click) {
                    Person person = getData(row);
                    Column<Person, ?> col = grid.getColumns()
                            .get(Integer.parseInt(column));

                    log("Click on Person " + person.getFirstName() + " "
                            + person.getLastName() + "  on column "
                            + col.getId());
                    log("MouseEventDetails: " + click.getButtonName() + " ("
                            + click.getClientX() + ", " + click.getClientY()
                            + ")");
                }
            });
        }

        @Override
        public void generateData(Person item, JsonObject jsonObject) {
        }

    }

    @Override
    protected void setup(VaadinRequest request) {
        Grid<Person> grid = new Grid<>();
        grid.addColumn(Person::getFirstName).setId("first").setCaption("first");
        grid.addColumn(Person::getLastName).setId("second")
                .setCaption("second");
        grid.setItems(IntStream.range(1, 51).mapToObj(this::createPerson));
        grid.setSelectionMode(SelectionMode.NONE);
        new GridClickExtension(grid);
        addComponent(grid);
    }

    private Person createPerson(int index) {
        Person person = new Person();
        person.setFirstName("first name " + index);
        person.setLastName("last name " + index);
        return person;
    }
}
