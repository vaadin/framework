package com.vaadin.data.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class BeanItemContainerSortTest {
    public class Person {
        private String name;

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        private int age;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public class Parent extends Person {
        private Set<Person> children = new HashSet<Person>();

        public void setChildren(Set<Person> children) {
            this.children = children;
        }

        public Set<Person> getChildren() {
            return children;
        }
    }

    String[] names = new String[] { "Antti", "Ville", "Sirkka", "Jaakko",
            "Pekka", "John" };
    int[] ages = new int[] { 10, 20, 50, 12, 64, 67 };
    String[] sortedByAge = new String[] { names[0], names[3], names[1],
            names[2], names[4], names[5] };

    public BeanItemContainer<Person> getContainer() {
        BeanItemContainer<Person> bc = new BeanItemContainer<Person>(
                Person.class);
        for (int i = 0; i < names.length; i++) {
            Person p = new Person();
            p.setName(names[i]);
            p.setAge(ages[i]);
            bc.addBean(p);
        }
        return bc;

    }

    public BeanItemContainer<Parent> getParentContainer() {
        BeanItemContainer<Parent> bc = new BeanItemContainer<Parent>(
                Parent.class);
        for (int i = 0; i < names.length; i++) {
            Parent p = new Parent();
            p.setName(names[i]);
            p.setAge(ages[i]);
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
            Assert.assertTrue(container.containsId(idByIndex));
            Assert.assertEquals(string, idByIndex.getName());
        }
    }

    @Test
    public void testReverseSort() {
        testSort(false);
    }

    @Test
    public void primitiveSorting() {
        BeanItemContainer<Person> container = getContainer();
        container.sort(new Object[] { "age" }, new boolean[] { true });

        int i = 0;
        for (String string : sortedByAge) {
            Person idByIndex = container.getIdByIndex(i++);
            Assert.assertTrue(container.containsId(idByIndex));
            Assert.assertEquals(string, idByIndex.getName());
        }
    }

    @Test
    public void customSorting() {
        BeanItemContainer<Person> container = getContainer();

        // custom sorter using the reverse order
        container.setItemSorter(new DefaultItemSorter() {
            @Override
            public int compare(Object o1, Object o2) {
                return -super.compare(o1, o2);
            }
        });

        container.sort(new Object[] { "age" }, new boolean[] { true });

        int i = container.size() - 1;
        for (String string : sortedByAge) {
            Person idByIndex = container.getIdByIndex(i--);
            Assert.assertTrue(container.containsId(idByIndex));
            Assert.assertEquals(string, idByIndex.getName());
        }
    }

    @Test
    public void testGetSortableProperties() {
        BeanItemContainer<Person> container = getContainer();

        Collection<?> sortablePropertyIds = container
                .getSortableContainerPropertyIds();
        Assert.assertEquals(2, sortablePropertyIds.size());
        Assert.assertTrue(sortablePropertyIds.contains("name"));
        Assert.assertTrue(sortablePropertyIds.contains("age"));
    }

    @Test
    public void testGetNonSortableProperties() {
        BeanItemContainer<Parent> container = getParentContainer();

        Assert.assertEquals(3, container.getContainerPropertyIds().size());

        Collection<?> sortablePropertyIds = container
                .getSortableContainerPropertyIds();
        Assert.assertEquals(2, sortablePropertyIds.size());
        Assert.assertTrue(sortablePropertyIds.contains("name"));
        Assert.assertTrue(sortablePropertyIds.contains("age"));
    }

}
