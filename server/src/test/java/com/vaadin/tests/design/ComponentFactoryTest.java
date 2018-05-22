package com.vaadin.tests.design;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.TextField;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.Design.ComponentFactory;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignException;

public class ComponentFactoryTest {

    private static final ComponentFactory defaultFactory = Design
            .getComponentFactory();

    private static final ThreadLocal<ComponentFactory> currentComponentFactory = new ThreadLocal<ComponentFactory>();

    // Set static component factory that delegate to a thread local factory
    static {
        Design.setComponentFactory(new ComponentFactory() {
            @Override
            public Component createComponent(String fullyQualifiedClassName,
                    DesignContext context) {
                ComponentFactory componentFactory = currentComponentFactory
                        .get();
                if (componentFactory == null) {
                    componentFactory = defaultFactory;
                }
                return componentFactory.createComponent(fullyQualifiedClassName,
                        context);
            }
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNullComponentFactory() {
        Design.setComponentFactory(null);
    }

    @Test
    public void testComponentFactoryLogging() {
        final List<String> messages = new ArrayList<String>();
        currentComponentFactory.set(new ComponentFactory() {
            @Override
            public Component createComponent(String fullyQualifiedClassName,
                    DesignContext context) {
                messages.add("Requested class " + fullyQualifiedClassName);
                return defaultFactory.createComponent(fullyQualifiedClassName,
                        context);
            }
        });

        Design.read(new ByteArrayInputStream("<vaadin-label />".getBytes()));

        Assert.assertEquals("There should be one message logged", 1,
                messages.size());
        Assert.assertEquals("Requested class " + Label.class.getCanonicalName(),
                messages.get(0));
    }

    @Test(expected = DesignException.class)
    public void testComponentFactoryReturningNull() {
        currentComponentFactory.set(new ComponentFactory() {
            @Override
            public Component createComponent(String fullyQualifiedClassName,
                    DesignContext context) {
                return null;
            }
        });

        Design.read(new ByteArrayInputStream("<vaadin-label />".getBytes()));
    }

    @Test(expected = DesignException.class)
    public void testComponentFactoryThrowingStuff() {
        currentComponentFactory.set(new ComponentFactory() {
            @Override
            public Component createComponent(String fullyQualifiedClassName,
                    DesignContext context) {
                // Will throw because class is not found
                return defaultFactory.createComponent(
                        "foobar." + fullyQualifiedClassName, context);
            }
        });

        Design.read(new ByteArrayInputStream("<vaadin-label />".getBytes()));
    }

    @Test
    public void testGetDefaultInstanceUsesComponentFactory() {
        final List<String> classes = new ArrayList<String>();
        currentComponentFactory.set(new ComponentFactory() {
            @Override
            public Component createComponent(String fullyQualifiedClassName,
                    DesignContext context) {
                classes.add(fullyQualifiedClassName);
                return defaultFactory.createComponent(fullyQualifiedClassName,
                        context);
            }
        });

        DesignContext designContext = new DesignContext();
        designContext.getDefaultInstance(new DefaultInstanceTestComponent());

        Assert.assertEquals("There should be one class requests", 1,
                classes.size());
        Assert.assertEquals(
                "First class should be DefaultInstanceTestComponent",
                DefaultInstanceTestComponent.class.getName(), classes.get(0));
    }

    @After
    public void cleanup() {
        currentComponentFactory.remove();
    }

    public static class DefaultInstanceTestComponent extends AbstractComponent {
    }
}
