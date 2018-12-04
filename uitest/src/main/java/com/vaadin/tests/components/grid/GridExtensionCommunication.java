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
