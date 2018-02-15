package com.vaadin.tests.data;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;

public class NestedPropertyGridCreation extends AbstractTestUI {
    public static boolean INIT = true;

    @Override
    protected void setup(VaadinRequest vaadinRequest) {
        Grid<Person> grid = new Grid<>(Person.class);
        grid.addColumn("address.street");
        addComponent(grid);
    }

    private class Person {
        private String name;
        private Address address;

        public Person(String name, Address adr) {
            this.name = name;
            this.address = adr;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

    }

    private class Address {
        private String street;

        public Address(String str) {
            this.street = str;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

    }

}