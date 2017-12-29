package com.vaadin.data;

import org.junit.Test;

import com.vaadin.ui.Grid;

public class NestedPropertyNameTest {
    
    @Test
    public void nestedProperty_sameNameCanBeAdded() {
        Grid<Person> grid = new Grid<>(Person.class);
        grid.addColumn("street.name");
    }

    private class Person{
        String name;
        Street street;
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public Street getStreet() {
            return street;
        }
        
        public void setStreet(Street street) {
            this.street = street;
        }
        
    }
    
    private class Street{
        String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
