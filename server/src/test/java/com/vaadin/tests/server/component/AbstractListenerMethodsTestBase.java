package com.vaadin.tests.server.component;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.easymock.EasyMock;

import com.vaadin.shared.Registration;
import com.vaadin.tests.VaadinClasses;
import com.vaadin.ui.Component;

public abstract class AbstractListenerMethodsTestBase {

    public static void main(String[] args) {
        findAllListenerMethods();
    }

    private static void findAllListenerMethods() {
        Set<Class<?>> classes = new HashSet<>();
        for (Class<?> c : VaadinClasses.getAllServerSideClasses()) {
            while (c != null && c.getName().startsWith("com.vaadin.")) {
                classes.add(c);
                c = c.getSuperclass();
            }
        }

        for (Class<?> c : classes) {
            boolean found = false;
            for (Method m : c.getDeclaredMethods()) {
                String methodName = m.getName();
                if (methodName.startsWith("add")
                        && methodName.endsWith("Listener")
                        && !"addListener".equals(methodName)) {
                    if (m.getParameterTypes().length != 1) {
                        continue;
                    }
                    String packageName = "com.vaadin.tests.server";
                    if (Component.class.isAssignableFrom(c)) {
                        packageName += ".component."
                                + c.getSimpleName().toLowerCase(Locale.ROOT);
                        continue;
                    }

                    if (!found) {
                        found = true;
                        System.out.println("package " + packageName + ";");

                        System.out.println("import "
                                + AbstractListenerMethodsTestBase.class
                                        .getName()
                                + ";");
                        System.out.println("import " + c.getName() + ";");
                        System.out.println("public class " + c.getSimpleName()
                                + "Listeners extends "
                                + AbstractListenerMethodsTestBase.class
                                        .getSimpleName()
                                + " {");
                    }

                    String listenerClassName = m.getParameterTypes()[0]
                            .getSimpleName();
                    String eventClassName = listenerClassName
                            .replaceFirst("Listener$", "Event");
                    System.out.println("public void test" + listenerClassName
                            + "() throws Exception {");
                    System.out.println("    testListener(" + c.getSimpleName()
                            + ".class, " + eventClassName + ".class, "
                            + listenerClassName + ".class);");
                    System.out.println("}");
                }
            }
            if (found) {
                System.out.println("}");
                System.out.println();
            }
        }
    }

    protected void testListenerAddGetRemove(Class<?> testClass,
            Class<?> eventClass, Class<?> listenerClass) throws Exception {
        // Create a component for testing
        Object c = testClass.newInstance();
        testListenerAddGetRemove(testClass, eventClass, listenerClass, c);

    }

    protected void testListenerAddGetRemove(Class<?> cls, Class<?> eventClass,
            Class<?> listenerClass, Object c) throws Exception {

        Object mockListener1 = EasyMock.createMock(listenerClass);
        Object mockListener2 = EasyMock.createMock(listenerClass);

        // Verify we start from no listeners
        verifyListeners(c, eventClass);

        // Add one listener and verify
        Registration listener1Registration = addListener(c, mockListener1,
                listenerClass);
        verifyListeners(c, eventClass, mockListener1);

        // Add another listener and verify
        Registration listener2Registration = addListener(c, mockListener2,
                listenerClass);
        verifyListeners(c, eventClass, mockListener1, mockListener2);

        // Ensure we can fetch using parent class also
        if (eventClass.getSuperclass() != null) {
            verifyListeners(c, eventClass.getSuperclass(), mockListener1,
                    mockListener2);
        }

        // Remove the first and verify
        listener1Registration.remove();
        verifyListeners(c, eventClass, mockListener2);

        // Remove the remaining and verify
        listener2Registration.remove();
        verifyListeners(c, eventClass);

    }

    private Registration addListener(Object c, Object listener1,
            Class<?> listenerClass) throws IllegalArgumentException,
            IllegalAccessException, InvocationTargetException,
            SecurityException, NoSuchMethodException {
        Method method = getAddListenerMethod(c.getClass(), listenerClass);
        return (Registration) method.invoke(c, listener1);
    }

    private Collection<?> getListeners(Object c, Class<?> eventType)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException, SecurityException,
            NoSuchMethodException {
        Method method = getGetListenersMethod(c.getClass());
        return (Collection<?>) method.invoke(c, eventType);
    }

    private Method getGetListenersMethod(Class<? extends Object> cls)
            throws SecurityException, NoSuchMethodException {
        return cls.getMethod("getListeners", Class.class);
    }

    private Method getAddListenerMethod(Class<?> cls, Class<?> listenerClass)
            throws SecurityException, NoSuchMethodException {
        Method addListenerMethod = cls.getMethod(
                "add" + listenerClass.getSimpleName(), listenerClass);
        if (addListenerMethod.getReturnType() != Registration.class) {
            throw new NoSuchMethodException(
                    cls.getSimpleName() + ".add" + listenerClass.getSimpleName()
                            + " has wrong return type, expected Registration");
        }
        return addListenerMethod;
    }

    private void verifyListeners(Object c, Class<?> eventClass,
            Object... expectedListeners) throws IllegalArgumentException,
            SecurityException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        Collection<?> registeredListeners = getListeners(c, eventClass);
        assertEquals("Number of listeners", expectedListeners.length,
                registeredListeners.size());

        assertArrayEquals(expectedListeners, registeredListeners.toArray());

    }
}
