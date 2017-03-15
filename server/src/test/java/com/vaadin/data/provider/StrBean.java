package com.vaadin.data.provider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class StrBean implements Serializable {

    private static final String[] values = new String[] { "Foo", "Bar", "Baz" };

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
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof StrBean) {
            StrBean that = (StrBean) obj;
            return that.id == this.id && that.randomNumber == this.randomNumber
                    && Objects.equals(this.value, that.value);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, randomNumber, value);
    }

    @Override
    public String toString() {
        return "{ " + value + ", " + randomNumber + ", " + id + " }";
    }
}
