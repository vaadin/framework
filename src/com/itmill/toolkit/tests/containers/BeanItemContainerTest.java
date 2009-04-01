package com.itmill.toolkit.tests.containers;

import java.util.Collection;
import java.util.LinkedList;

import com.itmill.toolkit.data.util.BeanItemContainer;

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

        c = new BeanItemContainer<Hello>(col);

        System.out.print(c + " contains " + c.size() + " objects");

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

}
