/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Helper class for performing serialization. Most of the methods are here are
 * workarounds for problems in Google App Engine. Used internally by Vaadin and
 * should not be used by application developers. Subject to change at any time.
 * 
 * @since 6.0
 */
public class SerializerHelper {

    /**
     * Serializes the class reference so {@link #readClass(ObjectInputStream)}
     * can deserialize it. Supports null class references.
     * 
     * @param out
     *            The {@link ObjectOutputStream} to serialize to.
     * @param cls
     *            A class or null.
     * @throws IOException
     *             Rethrows any IOExceptions from the ObjectOutputStream
     */
    public static void writeClass(ObjectOutputStream out, Class<?> cls)
            throws IOException {
        if (cls == null) {
            out.writeObject(null);
        } else {
            out.writeObject(cls.getName());
        }

    }

    /**
     * Serializes the class references so
     * {@link #readClassArray(ObjectInputStream)} can deserialize it. Supports
     * null class arrays.
     * 
     * @param out
     *            The {@link ObjectOutputStream} to serialize to.
     * @param classes
     *            An array containing class references or null.
     * @throws IOException
     *             Rethrows any IOExceptions from the ObjectOutputStream
     */
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

    /**
     * Deserializes a class references serialized by
     * {@link #writeClassArray(ObjectOutputStream, Class[])}. Supports null
     * class arrays.
     * 
     * @param in
     *            {@link ObjectInputStream} to read from.
     * @return Class array with the class references or null.
     * @throws ClassNotFoundException
     *             If one of the classes could not be resolved.
     * @throws IOException
     *             Rethrows IOExceptions from the ObjectInputStream
     */
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

    /**
     * List of primitive classes. Google App Engine has problems
     * serializing/deserializing these (#3064).
     */
    private static Class<?>[] primitiveClasses = new Class<?>[] { byte.class,
            short.class, int.class, long.class, float.class, double.class,
            boolean.class, char.class };

    /**
     * Resolves the class given by {@code className}.
     * 
     * @param className
     *            The fully qualified class name.
     * @return A {@code Class} reference.
     * @throws ClassNotFoundException
     *             If the class could not be resolved.
     */
    public static Class<?> resolveClass(String className)
            throws ClassNotFoundException {
        for (Class<?> c : primitiveClasses) {
            if (className.equals(c.getName())) {
                return c;
            }
        }

        return Class.forName(className);
    }

    /**
     * Deserializes a class reference serialized by
     * {@link #writeClass(ObjectOutputStream, Class)}. Supports null class
     * references.
     * 
     * @param in
     *            {@code ObjectInputStream} to read from.
     * @return Class reference to the resolved class
     * @throws ClassNotFoundException
     *             If the class could not be resolved.
     * @throws IOException
     *             Rethrows IOExceptions from the ObjectInputStream
     */
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
