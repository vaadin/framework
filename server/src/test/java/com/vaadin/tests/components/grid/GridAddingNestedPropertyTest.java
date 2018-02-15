package com.vaadin.tests.components.grid;

import com.vaadin.ui.Grid;
import org.junit.Test;

public class GridAddingNestedPropertyTest {

    /**
     * Test adding the same nested property twice
     */
    @Test
    public void testIssue10243() {
        Grid<Person> grid1 = new Grid<>(Person.class);
        grid1.addColumn("address.street");
        Grid<Person> grid2 = new Grid<>(Person.class);
        grid2.addColumn("address.street");
    }

    class Person {
        public Address address;

        public Address getAddress() {
            return address;
        }
    }

    class Address {
        public String street;

        public String getStreet() {
            return street;
        }
    }
}
