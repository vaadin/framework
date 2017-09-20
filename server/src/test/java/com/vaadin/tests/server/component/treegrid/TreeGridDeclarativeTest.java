package com.vaadin.tests.server.component.treegrid;

import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;

import com.vaadin.data.TreeData;
import com.vaadin.data.provider.HierarchicalQuery;
import com.vaadin.data.provider.TreeDataProvider;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.server.component.abstractlisting.AbstractListingDeclarativeTest;
import com.vaadin.ui.TreeGrid;

public class TreeGridDeclarativeTest
        extends AbstractListingDeclarativeTest<TreeGrid> {

    @SuppressWarnings("unchecked")
    @Override
    public void dataSerialization() throws InstantiationException,
            IllegalAccessException, InvocationTargetException {
        TreeGrid<Person> grid = new TreeGrid<>();

        Person person1 = createPerson("a", "last-name");
        Person person2 = createPerson("aa", "last-name");
        Person person3 = createPerson("ab", "last-name");
        Person person4 = createPerson("b", "last-name");
        Person person5 = createPerson("c", "last-name");
        Person person6 = createPerson("ca", "last-name");
        Person person7 = createPerson("caa", "last-name");

        TreeData<Person> data = new TreeData<>();
        data.addItems(null, person1, person4, person5);
        data.addItems(person1, person2, person3);
        data.addItem(person5, person6);
        data.addItem(person6, person7);

        grid.addColumn(Person::getFirstName).setCaption("First Name");
        grid.addColumn(Person::getLastName).setId("id").setCaption("Id");

        grid.setHierarchyColumn("id");
        grid.setDataProvider(new TreeDataProvider<>(data));

        String design = String.format(
                "<%s hierarchy-column='id'><table><colgroup>"
                        + "<col column-id='column0' sortable>"
                        + "<col column-id='id' sortable></colgroup><thead>"
                        + "<tr default><th plain-text column-ids='column0'>First Name</th>"
                        + "<th plain-text column-ids='id'>Id</th></tr>"
                        + "</thead><tbody>"
                        + "<tr item='%s'><td>%s</td><td>%s</td></tr>"
                        + "<tr item='%s' parent='%s'><td>%s</td><td>%s</td></tr>"
                        + "<tr item='%s' parent='%s'><td>%s</td><td>%s</td></tr>"
                        + "<tr item='%s'><td>%s</td><td>%s</td></tr>"
                        + "<tr item='%s'><td>%s</td><td>%s</td></tr>"
                        + "<tr item='%s' parent='%s'><td>%s</td><td>%s</td></tr>"
                        + "<tr item='%s' parent='%s'><td>%s</td><td>%s</td></tr>"
                        + "</tbody></table></%s>",
                getComponentTag(), person1.toString(), person1.getFirstName(),
                person1.getLastName(), person2.toString(), person1.toString(),
                person2.getFirstName(), person2.getLastName(),
                person3.toString(), person1.toString(), person3.getFirstName(),
                person3.getLastName(), person4.toString(),
                person4.getFirstName(), person4.getLastName(),
                person5.toString(), person5.getFirstName(),
                person5.getLastName(), person6.toString(), person5.toString(),
                person6.getFirstName(), person6.getLastName(),
                person7.toString(), person6.toString(), person7.getFirstName(),
                person7.getLastName(), getComponentTag());

        TreeGrid<String> readGrid = testRead(design, grid);
        Assert.assertEquals(3, readGrid.getDataProvider()
                .size(new HierarchicalQuery<>(null, null)));
        Assert.assertEquals(2, readGrid.getDataProvider()
                .size(new HierarchicalQuery<>(null, person1.toString())));
        Assert.assertEquals(1, readGrid.getDataProvider()
                .size(new HierarchicalQuery<>(null, person5.toString())));
        Assert.assertEquals(1, readGrid.getDataProvider()
                .size(new HierarchicalQuery<>(null, person6.toString())));
        testWrite(design, grid, true);
    }
    

    @Override
    public void valueSerialization() throws InstantiationException,
            IllegalAccessException, InvocationTargetException {
        // Tested by GridDeclarativeTest
    }

    @Override
    public void readOnlySelection() throws InstantiationException,
            IllegalAccessException, InvocationTargetException {
        // Tested by GridDeclarativeTest
    }

    @Override
    protected String getComponentTag() {
        return "vaadin-tree-grid";
    }

    @Override
    protected Class<? extends TreeGrid> getComponentClass() {
        return TreeGrid.class;
    }

    private Person createPerson(String name, String lastName) {
        Person person = new Person() {
            @Override
            public String toString() {
                return getFirstName() + " " + getLastName();
            }
        };
        person.setFirstName(name);
        person.setLastName(lastName);
        return person;
    }
}
