package com.vaadin.tests.components.treegrid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.TreeGrid;

public class TreeGridBeanTest {

    @Test
    public void testBeanTypeConstructor() {
        TreeGrid<Person> treeGrid = new TreeGrid<>(Person.class);
        assertEquals(Person.class, treeGrid.getBeanType());
    }

}
