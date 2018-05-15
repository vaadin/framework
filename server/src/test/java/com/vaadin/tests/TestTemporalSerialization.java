package com.vaadin.tests;

import javax.swing.text.DateFormatter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.SerializationUtils;
import org.junit.Test;

import com.vaadin.server.SerializableSupplier;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.LocalDateRenderer;
import com.vaadin.ui.renderers.LocalDateTimeRenderer;

import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;
import static junit.framework.TestCase.assertNotNull;

public class TestTemporalSerialization {

    @Test
    public void smokeTestRendererSerialization()
            throws IOException, ClassNotFoundException {
        Grid<Object> grid = new Grid<>();
        grid.addColumn(
                o -> new Date(o.hashCode()).toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDate(),
                new LocalDateRenderer());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                outputStream);
        objectOutputStream.writeObject(grid);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(
                outputStream.toByteArray());
        Grid readGrid = (Grid) new ObjectInputStream(inputStream).readObject();
        assertNotNull(readGrid.getColumns().get(0));
    }

    @Test
    public void testLocalDateRenderer() throws IllegalAccessException,
            InstantiationException, InvocationTargetException {
        testSerialization(LocalDateRenderer.class);
    }

    @Test
    public void testLocalDateTimeRenderer() throws IllegalAccessException,
            InstantiationException, InvocationTargetException {
        testSerialization(LocalDateTimeRenderer.class);
    }

    @Test(expected = AssertionError.class)
    public void testAssertionFail() {
        new LocalDateRenderer(new NonSerializableThing());
    }

    private static class NonSerializableThing
            implements SerializableSupplier<DateTimeFormatter> {
        public NonSerializableThing() {
        }

        private DateTimeFormatter useless = DateTimeFormatter.ofPattern("Y");

        @Override
        public DateTimeFormatter get() {
            return useless;
        }
    }

    private void testSerialization(Class<?> rendererClass)
            throws IllegalAccessException, InvocationTargetException,
            InstantiationException {
        for (Constructor<?> constructor : rendererClass.getConstructors()) {
            if (!isPublic(constructor.getModifiers()))
                continue;
            Object[] params = simulateParams(constructor);
            if (params != null) {
                Object o = constructor.newInstance(params);
                checkSerialization(constructor, o);
            }
        }
        for (Method method : rendererClass.getMethods()) {
            if (!isPublic(method.getModifiers())
                    || !isStatic(method.getModifiers())) {
                continue;
            }
            if (!method.getReturnType().isAssignableFrom(rendererClass)) {
                continue;
            }
            Object[] params = simulateParams(method);
            if (params != null) {
                Object o = method.invoke(simulateParams(method));
                checkSerialization(method, o);
            }
        }
    }

    private Object[] simulateParams(Executable executable) {
        Parameter[] parameters = executable.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> type = parameter.getType();
            if (type.isAssignableFrom(String.class)) {
                args[i] = "";
            } else if (type.isAssignableFrom(Locale.class)) {
                args[i] = Locale.US;
            } else if (type.isAssignableFrom(SerializableSupplier.class)) {
                Type genericType = ((ParameterizedType) parameter
                        .getParameterizedType()).getActualTypeArguments()[0];
                args[i] = (SerializableSupplier) () -> {
                    try {
                        return ((Class) genericType).newInstance();
                    } catch (Exception e) {
                        throw new AssertionError(e);
                    }
                };
            } else if (type.isAssignableFrom(DateFormatter.class)
                    || type.isAssignableFrom(DateTimeFormatter.class)) {
                assertNotNull(
                        "Non-deprecated code has non-serializable parameter: "
                                + executable.toGenericString(),
                        executable.getAnnotation(Deprecated.class));
                return null;
            } else {
                throw new IllegalArgumentException(
                        "Unsupported parameter type: " + type.getName());
            }
        }
        return args;
    }

    private void checkSerialization(Executable method, Object o) {
        try {
            byte[] serialize = SerializationUtils.serialize((Serializable) o);
            SerializationUtils.deserialize(serialize);
        } catch (Throwable e) {
            throw new AssertionError(method.toGenericString(), e);
        }
    }
}
