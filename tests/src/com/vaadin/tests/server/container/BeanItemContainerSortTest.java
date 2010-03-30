package com.vaadin.tests.server.container;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.vaadin.data.util.BeanItemContainer;

public class BeanItemContainerSortTest {
    public class Person {
        private String name;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    String[] names = new String[] { "Antti", "Ville", "Sirkka", "Jaakko" };

    public BeanItemContainer<Person> getContainer() {
        BeanItemContainer<Person> bc = new BeanItemContainer<Person>(
                Person.class);
        for (String name : names) {
            Person p = new Person();
            p.setName(name);
            bc.addBean(p);
        }
        return bc;

    }

    @Test
    public void testSort() {
        testSort(true);
    }

    public void testSort(boolean b) {
        BeanItemContainer<Person> container = getContainer();
        container.sort(new Object[] { "name" }, new boolean[] { b });

        List<String> asList = Arrays.asList(names);
        Collections.sort(asList);
        if (!b) {
            Collections.reverse(asList);
        }

        int i = 0;
        for (String string : asList) {
            Person idByIndex = container.getIdByIndex(i++);
            Assert.assertEquals(string, idByIndex.getName());
        }
    }

    @Test
    public void testReverseSort() {
        testSort(false);
    }

}
