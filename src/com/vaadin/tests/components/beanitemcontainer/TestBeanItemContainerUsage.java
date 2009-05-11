package com.vaadin.tests.components.beanitemcontainer;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Table;

public class TestBeanItemContainerUsage extends TestBase {

    @Override
    protected String getDescription() {
        return "A test for the BeanItemContainer. The table should contain three persons and show their first and last names and their age.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 1061;
    }

    @Override
    protected void setup() {
        Table t = new Table("Table containing Persons");
        t.setPageLength(5);
        t.setWidth("100%");
        List<Person> persons = new ArrayList<Person>();
        persons.add(new Person("Jones", "Birchman", 35));
        persons.add(new Person("Marc", "Smith", 30));
        persons.add(new Person("Greg", "Sandman", 75));

        BeanItemContainer<Person> bic = new BeanItemContainer<Person>(persons);
        t.setContainerDataSource(bic);

        addComponent(t);
    }

    public static class Person {
        private String firstName;
        private String lastName;
        private int age;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public Person(String firstName, String lastName, int age) {
            super();
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
        }

    }
}
