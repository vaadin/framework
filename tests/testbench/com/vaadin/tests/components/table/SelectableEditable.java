package com.vaadin.tests.components.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Table;

public class SelectableEditable extends TestBase {

    @Override
    protected void setup() {
        // TODO Auto-generated method stub

        final Table table = new Table();
        table.setWidth("500px");
        table.setSelectable(true);
        table.setEditable(true);

        table.addContainerProperty("name", String.class, null);
        table.addContainerProperty("alive", Boolean.class, false);
        for (int i = 0; i < 10; ++i) {
            table.addItem(new Object[] { "Person " + i, false }, i);
        }

        addComponent(table);
    }

    @Override
    protected String getDescription() {
        // TODO Auto-generated method stub
        return "It is difficult to select rows of an editable Table, especially columns with checkboxes.";
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return 9064;
    }
}

class PersonGenerator {

    private static final String[] fNames = { "Peter", "Alice", "Joshua",
            "Mike", "Olivia", "Nina", "Alex", "Rita", "Dan", "Umberto",
            "Henrik", "Rene", "Lisa", "Marge" };
    private static final String[] lNames = { "Smith", "Gordon", "Simpson",
            "Brown", "Clavel", "Simons", "Verne", "Scott", "Allison", "Gates",
            "Rowling", "Barks", "Ross", "Schneider", "Tate" };

    private PersonGenerator() {
    }

    /**
     * Generates a dummy list of {@link Person}s with randomly selected
     * elements.
     * 
     * @param count
     *            the number of {@link Person}s to generate
     * @return a list of {@link Person}s.
     */
    public static List<Person> generateDummyPersons(int count) {
        final List<Person> persons = new ArrayList<Person>();
        final Random randomGenerator = new Random();
        for (int i = 0; i < count; i++) {
            final Person person = generatePerson(randomGenerator);
            persons.add(person);
        }
        return persons;
    }

    private static Person generatePerson(Random randomGenerator) {
        final String firstName = fNames[randomGenerator.nextInt(fNames.length)];
        final String lastName = lNames[randomGenerator.nextInt(lNames.length)];
        final boolean alive = randomGenerator.nextBoolean();
        return new Person(firstName, lastName, alive);
    }

}
