package com.vaadin.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Helper class for performing serialization. Most of the methods are here are
 * workarounds for problems in Google App Engine.
 * 
 */
public class SerializerHelper {

    public static void writeClass(ObjectOutputStream out, Class<?> cls)
            throws IOException {
        if (cls == null) {
            out.writeObject(null);
        } else {
            out.writeObject(cls.getName());
        }

    }

    public static void writeClassArray(ObjectOutputStream out,
            Class<?>[] classes) throws IOException {
        if (classes == null) {
            out.writeObject(null);
        } else {
            String[] classNames = new String[classes.length];
            for (int i = 0; i < classes.length; i++) {
                classNames[i] = classes[i].getName();
            }
            out.writeObject(classNames);
        }
    }

    public static Class<?>[] readClassArray(ObjectInputStream in)
            throws ClassNotFoundException, IOException {
        String[] classNames = (String[]) in.readObject();
        if (classNames == null) {
            return null;
        }
        Class<?>[] classes = new Class<?>[classNames.length];
        for (int i = 0; i < classNames.length; i++) {
            classes[i] = resolveClass(classNames[i]);
        }

        return classes;
    }

    private static Class<?>[] primitiveClasses = new Class<?>[] { byte.class,
            short.class, int.class, long.class, float.class, double.class,
            boolean.class, char.class };

    public static Class<?> resolveClass(String className)
            throws ClassNotFoundException {
        for (Class<?> c : primitiveClasses) {
            if (className.equals(c.getName())) {
                return c;
            }
        }

        return Class.forName(className);
    }

    public static Class<?> readClass(ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        String className = (String) in.readObject();
        if (className == null) {
            return null;
        } else {
            return resolveClass(className);

        }

    }

}
