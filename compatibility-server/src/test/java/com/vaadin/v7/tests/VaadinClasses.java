package com.vaadin.v7.tests;

import java.io.IOException;
import java.util.List;

import com.vaadin.ui.Component;
import com.vaadin.v7.ui.Field;

@SuppressWarnings("deprecation")
public class VaadinClasses {

    public static List<Class<? extends Field>> getFields() {
        try {
            return com.vaadin.tests.VaadinClasses.findClasses(Field.class,
                    "com.vaadin.v7.ui");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Class<? extends Component>> getComponents() {
        try {
            return com.vaadin.tests.VaadinClasses.findClasses(Component.class,
                    "com.vaadin.v7.ui");
        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not find all Vaadin component classes", e);
        }
    }
}
