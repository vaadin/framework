package com.vaadin.tests.components.table;

import java.util.Arrays;
import java.util.Collection;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class MultiSelectWithRemovedRow extends TestBase {
    public static class Person {
        private final String name;

        public Person(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @Override
    protected void setup() {
        final Log log = new Log(5);
        addComponent(log);

        final BeanItemContainer<Person> container = new BeanItemContainer<Person>(
                Person.class, Arrays.asList(new Person("Joe"), new Person(
                        "William"), new Person("Jack"), new Person("Averell"),
                        new Person("Bob"), new Person("Grat"), new Person(
                                "Bill"), new Person("Emmett")));
        final Table table = new Table("Table", container);
        table.setSelectable(true);
        table.setMultiSelect(true);
        table.setImmediate(true);
        addComponent(table);

        Button showButton = new Button("Show selection");
        showButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                Collection<?> selection = (Collection<?>) table.getValue();
                log.log("Selection: " + selection);
            }
        });
        addComponent(showButton);

        Button removeButton = new Button("Remove selection");
        removeButton.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                Collection<?> selection = (Collection<?>) table.getValue();
                for (Object selected : selection) {
                    container.removeItem(selected);
                }
            }
        });
        addComponent(removeButton);

        addComponent(new Button("Remove first selected row",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        Collection<?> selection = (Collection<?>) table
                                .getValue();
                        if (!selection.isEmpty()) {
                            Object firstSelected = selection.iterator().next();
                            container.removeItem(firstSelected);
                        }
                    }
                }));
    }

    @Override
    protected String getDescription() {
        return "Multi select using shift should work after removing the currently selected row";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(8584);
    }
}
