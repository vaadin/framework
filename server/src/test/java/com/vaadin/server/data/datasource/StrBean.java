package com.vaadin.server.data.datasource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class StrBean implements Serializable {

    private static String[] values = new String[] { "Foo", "Bar", "Baz" };

    private String value;
    private final int id;
    private final int randomNumber;

    public StrBean(String value, int id, int randomNumber) {
        this.value = value;
        this.id = id;
        this.randomNumber = randomNumber;
    }

    public String getValue() {
        return value;
    }

    public int getId() {
        return id;
    }

    public int getRandomNumber() {
        return randomNumber;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static List<StrBean> generateRandomBeans(int max) {
        List<StrBean> data = new ArrayList<>();
        Random r = new Random(13337);
        data.add(new StrBean("Xyz", 10, max));
        for (int i = 0; i < max - 1; ++i) {
            data.add(new StrBean(values[r.nextInt(values.length)], i,
                    r.nextInt(10)));
        }
        return data;
    }

    @Override
    public String toString() {
        return "{ " + value + ", " + randomNumber + ", " + id + " }";
    }
}