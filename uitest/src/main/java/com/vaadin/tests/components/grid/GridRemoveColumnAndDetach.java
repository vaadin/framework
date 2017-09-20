package com.vaadin.tests.components.grid;

import java.util.stream.Collectors;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridRemoveColumnAndDetach extends AbstractTestUIWithLog {

    private Grid<Person> grid;

    @Override
    protected void setup(VaadinRequest vaadinRequest) {
        grid = new Grid<>();
        grid.addColumn(Person::getFirstName).setCaption("First").setWidth(200);
        grid.addColumn(Person::getLastName).setCaption("Last").setHidden(true)
                .setWidth(200);
        grid.addColumn(Person::getEmail).setCaption("Email").setWidth(200);
        grid.addColumn(Person::getAge).setCaption("foobar").setWidth(400);

        grid.setItems(new Person("1", "2", "3", 4, Sex.FEMALE, null));
        grid.setFrozenColumnCount(3);
        logFrozenColumns();
        addComponent(grid);

        Button detachButton = new Button("Detach grid",
                e -> removeComponent(grid));
        detachButton.setId("detach");
        addComponent(detachButton);
        for (int i = 0; i < 4; i++) {
            final int idx = i;
            Button button = new Button("Remove col " + i, e -> {
                grid.removeColumn(grid.getColumns().get(idx));
                logFrozenColumns();
            });
            button.setId("remove" + i);
            addComponent(button);
        }
    }

    private void logFrozenColumns() {
        String msg = "Server side frozen columns: ";
        msg += grid.getColumns().stream().limit(grid.getFrozenColumnCount())
                .map(column -> {
                    String caption = column.getCaption();
                    if (column.isHidden()) {
                        caption += " (hidden)";
                    }
                    return caption;
                }).collect(Collectors.joining(", "));

        log(msg);
    }

}
