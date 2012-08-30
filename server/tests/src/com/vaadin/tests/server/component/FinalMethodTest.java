package com.vaadin.tests.server.component;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;

import junit.framework.TestCase;

import com.vaadin.tests.VaadinClasses;
import com.vaadin.ui.Component;

public class FinalMethodTest extends TestCase {

    // public void testThatContainersHaveNoFinalMethods() {
    // HashSet<Class<?>> tested = new HashSet<Class<?>>();
    // for (Class<?> c : VaadinClasses.getAllServerSideClasses()) {
    // if (Container.class.isAssignableFrom(c)) {
    // ensureNoFinalMethods(c, tested);
    // }
    // }
    // }

    public void testThatComponentsHaveNoFinalMethods() {
        HashSet<Class<?>> tested = new HashSet<Class<?>>();
        for (Class<? extends Component> c : VaadinClasses.getComponents()) {
            ensureNoFinalMethods(c, tested);
        }
    }

    private void ensureNoFinalMethods(Class<?> c, HashSet<Class<?>> tested) {
        if (tested.contains(c)) {
            return;
        }

        tested.add(c);

        if (c == Object.class) {
            return;
        }
        System.out.println("Checking " + c.getName());
        for (Method m : c.getDeclaredMethods()) {
            if (isPrivate(m)) {
                continue;
            }
            if (isFinal(m)) {
                String error = "Class " + c.getName() + " contains a "
                        + (isPublic(m) ? "public" : "non-public")
                        + " final method: " + m.getName();
                // System.err.println(error);
                throw new RuntimeException(error);
            }
        }
        ensureNoFinalMethods(c.getSuperclass(), tested);

    }

    private boolean isFinal(Method m) {
        return Modifier.isFinal(m.getModifiers());
    }

    private boolean isPrivate(Method m) {
        return Modifier.isPrivate(m.getModifiers());
    }

    private boolean isPublic(Method m) {
        return Modifier.isPublic(m.getModifiers());
    }
}
