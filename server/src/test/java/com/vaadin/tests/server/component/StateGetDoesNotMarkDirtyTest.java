package com.vaadin.tests.server.component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.server.VaadinSession;
import com.vaadin.tests.VaadinClasses;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentRootSetter;
import com.vaadin.ui.Composite;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

public class StateGetDoesNotMarkDirtyTest {

    private final Set<String> excludedMethods = new HashSet<>();

    @Before
    public void setUp() {
        excludedMethods.add(Label.class.getName() + "getDataProviderValue");
        excludedMethods.add("getConnectorId");
        excludedMethods.add("getContent");
        excludedMethods.add("com.vaadin.ui.Grid:getSelectAllCheckBoxVisible");
        excludedMethods.add("com.vaadin.ui.TreeGrid:getDataProvider");
    }

    @Test
    public void testGetDoesntMarkStateDirty() throws Exception {
        int count = 0;
        for (Class<? extends Component> clazz : VaadinClasses.getComponents()) {
            if (clazz.isInterface()
                    || Modifier.isAbstract(clazz.getModifiers())) {
                continue;
            }
            Component newInstance = construct(clazz);
            if (newInstance == null) {
                continue;
            }
            count++;
            prepareMockUI(newInstance);

            Set<Method> methods = new HashSet<>();
            methods.addAll(Arrays.asList(clazz.getMethods()));
            methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
            for (Method method : methods) {
                try {
                    if (method.getName().startsWith("is")
                            || method.getName().startsWith("get")) {
                        if (method.getName().startsWith("getState")) {
                            continue;
                        }
                        if (method.getParameterTypes().length > 0) {
                            // usually getters do not have params, if they have
                            // we still wouldnt know what to put into
                            continue;
                        }
                        if (excludedMethods.contains(
                                clazz.getName() + ":" + method.getName())) {
                            // blacklisted method for specific classes
                            continue;
                        }
                        if (excludedMethods.contains(method.getName())) {
                            // blacklisted method for all classes
                            continue;
                        }
                        // just to make sure we can invoke it
                        method.setAccessible(true);
                        try {
                            method.invoke(newInstance);
                        } catch (InvocationTargetException e) {
                            if (e.getCause() instanceof UnsupportedOperationException) {
                                // Overridden getter which is not supposed to be
                                // called
                            } else {
                                throw e;
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("problem with method " + clazz.getName()
                            + "# " + method.getName());
                    e.printStackTrace();
                    throw e;
                }
            }
        }
        Assert.assertTrue(count > 0);
    }

    private void prepareMockUI(Component newInstance) {
        UI ui = mockUI();
        ConnectorTracker connectorTracker = ui.getConnectorTracker();
        Mockito.doThrow(new RuntimeException("getState(true) called in getter"))
                .when(connectorTracker).markDirty(newInstance);
        newInstance.setParent(null);
        newInstance.setParent(ui);
    }

    private UI mockUI() {
        UI ui = Mockito.mock(UI.class);
        Mockito.when(ui.getLocale()).thenReturn(Locale.ENGLISH);
        ConnectorTracker connectorTracker = Mockito
                .mock(ConnectorTracker.class);
        Mockito.when(ui.getConnectorTracker()).thenReturn(connectorTracker);
        return ui;
    }

    private Component construct(Class<? extends Component> clazz) {
        try {
            Constructor<? extends Component> declaredConstructor = clazz
                    .getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            Component component = declaredConstructor.newInstance();

            if (component instanceof UI) {
                return component;
            }
            if (component.getClass().equals(Composite.class)) {
                // Plain Composite needs a root.
                ComponentRootSetter.setRoot(component, new Label());
            }
            emulateAttach(component);
            return component;
        } catch (NoSuchMethodException e) {
            // no default CTOR, skip
            return null;
        } catch (InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void emulateAttach(Component component) {
        UI ui = mockUI();
        VaadinSession session = Mockito.mock(VaadinSession.class);
        Mockito.when(session.hasLock()).thenReturn(true);
        Mockito.when(ui.getSession()).thenReturn(session);
        component.setParent(ui);

        component.attach();
    }

}
