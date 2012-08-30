package com.vaadin.tests.server.component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.junit.Assert;

import com.vaadin.tests.VaadinClasses;
import com.vaadin.ui.Component;

public abstract class AbstractListenerMethodsTest extends TestCase {

    public static void main(String[] args) {
        findAllListenerMethods();
    }

    private static void findAllListenerMethods() {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        for (Class<?> c : VaadinClasses.getAllServerSideClasses()) {
            while (c != null && c.getName().startsWith("com.vaadin.")) {
                classes.add(c);
                c = c.getSuperclass();
            }
        }

        for (Class<?> c : classes) {
            boolean found = false;
            for (Method m : c.getDeclaredMethods()) {
                if (m.getName().equals("addListener")) {
                    if (m.getParameterTypes().length != 1) {
                        continue;
                    }
                    String packageName = "com.vaadin.tests.server";
                    if (Component.class.isAssignableFrom(c)) {
                        packageName += ".component."
                                + c.getSimpleName().toLowerCase();
                        continue;
                    }

                    if (!found) {
                        found = true;
                        System.out.println("package " + packageName + ";");

                        System.out.println("import "
                                + AbstractListenerMethodsTest.class.getName()
                                + ";");
                        System.out.println("import " + c.getName() + ";");
                        System.out.println("public class "
                                + c.getSimpleName()
                                + "Listeners extends "
                                + AbstractListenerMethodsTest.class
                                        .getSimpleName() + " {");
                    }

                    String listenerClassName = m.getParameterTypes()[0]
                            .getSimpleName();
                    String eventClassName = listenerClassName.replaceFirst(
                            "Listener$", "Event");
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
        addListener(c, mockListener1, listenerClass);
        verifyListeners(c, eventClass, mockListener1);

        // Add another listener and verify
        addListener(c, mockListener2, listenerClass);
        verifyListeners(c, eventClass, mockListener1, mockListener2);

        // Ensure we can fetch using parent class also
        if (eventClass.getSuperclass() != null) {
            verifyListeners(c, eventClass.getSuperclass(), mockListener1,
                    mockListener2);
        }

        // Remove the first and verify
        removeListener(c, mockListener1, listenerClass);
        verifyListeners(c, eventClass, mockListener2);

        // Remove the remaining and verify
        removeListener(c, mockListener2, listenerClass);
        verifyListeners(c, eventClass);

    }

    private void removeListener(Object c, Object listener,
            Class<?> listenerClass) throws IllegalArgumentException,
            IllegalAccessException, InvocationTargetException,
            SecurityException, NoSuchMethodException {
        Method method = getRemoveListenerMethod(c.getClass(), listenerClass);
        method.invoke(c, listener);

    }

    private void addListener(Object c, Object listener1, Class<?> listenerClass)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException, SecurityException, NoSuchMethodException {
        Method method = getAddListenerMethod(c.getClass(), listenerClass);
        method.invoke(c, listener1);
    }

    private Collection<?> getListeners(Object c, Class<?> eventType)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException, SecurityException, NoSuchMethodException {
        Method method = getGetListenersMethod(c.getClass());
        return (Collection<?>) method.invoke(c, eventType);
    }

    private Method getGetListenersMethod(Class<? extends Object> cls)
            throws SecurityException, NoSuchMethodException {
        return cls.getMethod("getListeners", Class.class);
    }

    private Method getAddListenerMethod(Class<?> cls, Class<?> listenerClass)
            throws SecurityException, NoSuchMethodException {
        return cls.getMethod("addListener", listenerClass);

    }

    private Method getRemoveListenerMethod(Class<?> cls, Class<?> listenerClass)
            throws SecurityException, NoSuchMethodException {
        return cls.getMethod("removeListener", listenerClass);

    }

    private void verifyListeners(Object c, Class<?> eventClass,
            Object... expectedListeners) throws IllegalArgumentException,
            SecurityException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        Collection<?> registeredListeners = getListeners(c, eventClass);
        assertEquals("Number of listeners", expectedListeners.length,
                registeredListeners.size());

        Assert.assertArrayEquals(expectedListeners,
                registeredListeners.toArray());

    }
}
