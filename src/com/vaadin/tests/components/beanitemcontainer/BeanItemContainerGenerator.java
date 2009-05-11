package com.vaadin.tests.components.beanitemcontainer;

import java.util.Date;
import java.util.Random;

import com.vaadin.data.util.BeanItemContainer;

public class BeanItemContainerGenerator {

    public static BeanItemContainer<TestBean> createContainer(int size) {

        BeanItemContainer<TestBean> container = new BeanItemContainer<TestBean>(
                TestBean.class);
        Random r = new Random(new Date().getTime());
        for (int i = 0; i < size; i++) {
            container.addBean(new TestBean(r));
        }

        return container;

    }

    public static class TestBean {
        private String name, address, city, country;
        private int age, shoesize;

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public int getShoesize() {
            return shoesize;
        }

        public void setShoesize(int shoesize) {
            this.shoesize = shoesize;
        }

        public TestBean(Random r) {
            age = r.nextInt(100) + 5;
            shoesize = r.nextInt(10) + 35;
            name = createRandomString(r, r.nextInt(5) + 5);
            address = createRandomString(r, r.nextInt(15) + 5) + " "
                    + r.nextInt(100) + 1;
            city = createRandomString(r, r.nextInt(7) + 3);
            if (r.nextBoolean()) {
                country = createRandomString(r, r.nextInt(4) + 4);
            }
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

    }

    public static String createRandomString(Random r, int len) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < len; i++) {
            b.append((char) (r.nextInt('z' - 'a') + 'a'));
        }

        return b.toString();
    }

}
