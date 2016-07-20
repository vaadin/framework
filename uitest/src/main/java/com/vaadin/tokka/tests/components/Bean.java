package com.vaadin.tokka.tests.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Bean {

    static Random r = new Random(13337);

    private String value;
    private Integer intVal;

    public Bean(String value, Integer intVal) {
        this.value = value;
        this.intVal = intVal;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getIntVal() {
        return intVal;
    }

    public void setIntVal(Integer intVal) {
        this.intVal = intVal;
    }

    @Override
    public String toString() {
        return "Bean { value: " + value + ", intVal: " + intVal + " }";
    }

    public static List<Bean> generateRandomBeans() {
        String[] values = new String[] { "Foo", "Bar", "Baz" };

        List<Bean> beans = new ArrayList<Bean>();
        for (int i = 0; i < 100; ++i) {
            beans.add(new Bean(values[r.nextInt(values.length)], r.nextInt(100)));
        }
        return beans;
    }
}