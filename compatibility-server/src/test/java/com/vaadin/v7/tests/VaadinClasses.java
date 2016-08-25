package com.vaadin.v7.tests;

import java.io.IOException;
import java.util.List;

import com.vaadin.v7.ui.Field;

@SuppressWarnings("deprecation")
public class VaadinClasses {

    public static List<Class<? extends Field>> getFields() {
        try {
            return com.vaadin.tests.VaadinClasses.findClasses(Field.class,
                    "com.vaadin.ui");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
