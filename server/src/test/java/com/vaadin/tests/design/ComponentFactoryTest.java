package com.vaadin.tests.design;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.Design.ComponentFactory;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignException;

public class ComponentFactoryTest {

    private static final ComponentFactory defaultFactory = Design
            .getComponentFactory();

    private static final ThreadLocal<ComponentFactory> currentComponentFactory = new ThreadLocal<>();

    // Set static component factory that delegate to a thread local factory
    static {
        Design.setComponentFactory(
                (String fullyQualifiedClassName, DesignContext context) -> {
                    ComponentFactory componentFactory = currentComponentFactory
                            .get();
                    if (componentFactory == null) {
                        componentFactory = defaultFactory;
                    }
                    return componentFactory
                            .createComponent(fullyQualifiedClassName, context);
                });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNullComponentFactory() {
        Design.setComponentFactory(null);
    }

    @Test
    public void testComponentFactoryLogging() {
        final List<String> messages = new ArrayList<>();
        currentComponentFactory
                .set((ComponentFactory) (String fullyQualifiedClassName,
                        DesignContext context) -> {
                    messages.add("Requested class " + fullyQualifiedClassName);
                    return defaultFactory
                            .createComponent(fullyQualifiedClassName, context);
                });

        Design.read(new ByteArrayInputStream("<vaadin-label />".getBytes()));

        assertEquals("There should be one message logged", 1, messages.size());
        assertEquals("Requested class " + Label.class.getCanonicalName(),
                messages.get(0));
    }

    @Test(expected = DesignException.class)
    public void testComponentFactoryReturningNull() {
        currentComponentFactory
                .set((ComponentFactory) (String fullyQualifiedClassName,
                        DesignContext context) -> null);

        Design.read(new ByteArrayInputStream("<vaadin-label />".getBytes()));
    }

    @Test(expected = DesignException.class)
    public void testComponentFactoryThrowingStuff() {
        currentComponentFactory.set((ComponentFactory) (
                String fullyQualifiedClassName,
                // Will throw because class is not found
                DesignContext context) -> defaultFactory.createComponent(
                        "foobar." + fullyQualifiedClassName, context));

        Design.read(new ByteArrayInputStream("<vaadin-label />".getBytes()));
    }

    @Test
    public void testGetDefaultInstanceUsesComponentFactory() {
        final List<String> classes = new ArrayList<>();
        currentComponentFactory
                .set((ComponentFactory) (String fullyQualifiedClassName,
                        DesignContext context) -> {
                    classes.add(fullyQualifiedClassName);
                    return defaultFactory
                            .createComponent(fullyQualifiedClassName, context);
                });

        DesignContext designContext = new DesignContext();
        designContext.getDefaultInstance(new DefaultInstanceTestComponent());

        assertEquals("There should be one class requests", 1, classes.size());
        assertEquals("First class should be DefaultInstanceTestComponent",
                DefaultInstanceTestComponent.class.getName(), classes.get(0));
    }

    @After
    public void cleanup() {
        currentComponentFactory.remove();
    }

    public static class DefaultInstanceTestComponent extends AbstractComponent {
    }
}
