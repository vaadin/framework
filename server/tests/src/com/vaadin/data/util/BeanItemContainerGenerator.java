package com.vaadin.data.util;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

public class BeanItemContainerGenerator {

    public static class PortableRandom {
        private final static long multiplier = 0x5DEECE66DL;
        private final static long addend = 0xBL;
        private final static long mask = (1L << 48) - 1;
        private AtomicLong seed;

        public PortableRandom(long seed) {
            this.seed = new AtomicLong(0L);
            setSeed(seed);
        }

        synchronized public void setSeed(long seed) {
            seed = (seed ^ multiplier) & mask;
            this.seed.set(seed);
        }

        public int nextInt(int n) {
            if (n <= 0) {
                throw new IllegalArgumentException("n must be positive");
            }

            if ((n & -n) == n) {
                return (int) ((n * (long) next(31)) >> 31);
            }

            int bits, val;
            do {
                bits = next(31);
                val = bits % n;
            } while (bits - val + (n - 1) < 0);
            return val;
        }

        protected int next(int bits) {
            long oldseed, nextseed;
            AtomicLong seed = this.seed;
            do {
                oldseed = seed.get();
                nextseed = (oldseed * multiplier + addend) & mask;
            } while (!seed.compareAndSet(oldseed, nextseed));
            return (int) (nextseed >>> (48 - bits));
        }

        public boolean nextBoolean() {
            return next(1) != 0;
        }

    }

    public static BeanItemContainer<TestBean> createContainer(int size) {
        return createContainer(size, new Date().getTime());
    }

    public static BeanItemContainer<TestBean> createContainer(int size,
            long seed) {

        BeanItemContainer<TestBean> container = new BeanItemContainer<TestBean>(
                TestBean.class);
        PortableRandom r = new PortableRandom(seed);
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

        public TestBean(PortableRandom r) {
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

    public static String createRandomString(PortableRandom r, int len) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < len; i++) {
            b.append((char) (r.nextInt('z' - 'a') + 'a'));
        }

        return b.toString();
    }

}
