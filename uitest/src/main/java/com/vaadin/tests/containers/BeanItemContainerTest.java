package com.vaadin.tests.containers;

import java.util.Collection;
import java.util.LinkedList;

import com.vaadin.data.util.BeanItemContainer;

public class BeanItemContainerTest {

    /**
     * Test class for BeanItemContainer
     * 
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static void main(String[] args) throws InstantiationException,
            IllegalAccessException {
        BeanItemContainer<Hello> c = new BeanItemContainer<Hello>(Hello.class);
        c.addItem(new Hello());

        Collection<Hello> col = new LinkedList<Hello>();
        for (int i = 0; i < 100; i++) {
            col.add(new Hello());
        }
        col.add(new Hello2());

        c = new BeanItemContainer<Hello>(col);

        System.out.println(c + " contains " + c.size() + " objects");

        // test that subclass properties are handled correctly
        System.out.println(c + " item 0 second = "
                + c.getContainerProperty(c.getIdByIndex(0), "second"));
        System.out.println(c + " item 100 second = "
                + c.getContainerProperty(c.getIdByIndex(100), "second"));

    }

    public static class Hello {

        public String first;
        public String second;

        public Hello() {
            first = "f";
            second = "l";
        }

        public String getFirst() {
            return first;
        }

        public void setFirst(String first) {
            this.first = first;
        }

        public String getSecond() {
            return second;
        }

        public void setSecond(String second) {
            this.second = second;
        }

    }

    public static class Hello2 extends Hello {

        @Override
        public String getSecond() {
            return "second";
        }

    }
}
