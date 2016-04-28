package com.vaadin.tests.components.table;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.tests.util.Person;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Table;

public class ScrollCausesRequestLoop extends AbstractTestCase {

    @Override
    public void init() {
        setMainWindow(new LegacyWindow("", new TestView()));
    }

    @Override
    protected String getDescription() {
        return "Scrolling a table causes an infinite loop of UIDL requests in some cases";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8040;
    }

    private static class TestView extends HorizontalLayout {

        TestView() {
            Table table = new Table();
            List<Person> data = createData();
            BeanItemContainer<Person> container = new BeanItemContainer<Person>(
                    Person.class, data) {

                @Override
                public Person getIdByIndex(int index) {
                    try {
                        // Simulate some loading delay with some exaggeration
                        // to make easier to reproduce
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                    return super.getIdByIndex(index);
                }
            };
            table.setContainerDataSource(container);
            addComponent(table);
        }
    }

    private static List<Person> createData() {
        int count = 500;
        List<Person> data = new ArrayList<Person>(count);
        for (int i = 0; i < count; i++) {
            data.add(new Person("Person", "" + i, "Email", "Phone", "Street",
                    12345, "City"));
        }
        return data;
    }
}
