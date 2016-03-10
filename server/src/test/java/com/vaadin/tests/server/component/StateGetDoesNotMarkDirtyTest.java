package com.vaadin.tests.server.component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import junit.framework.TestCase;

import org.mockito.Mockito;

import com.vaadin.tests.VaadinClasses;
import com.vaadin.ui.Component;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

public class StateGetDoesNotMarkDirtyTest extends TestCase {

    private Set<String> excludedMethods = new HashSet<String>();

    @Override
    protected void setUp() throws Exception {
        excludedMethods.add(Label.class.getName() + "getDataSourceValue");
        excludedMethods.add("getConnectorId");
    }

    public void testGetDoesntMarkStateDirty() throws Exception {
        for (Class<? extends Component> c : VaadinClasses.getComponents()) {
            Component newInstance = construct(c);
            prepareMockUI(newInstance);

            Set<Method> methods = new HashSet<Method>();
            methods.addAll(Arrays.asList(c.getMethods()));
            methods.addAll(Arrays.asList(c.getDeclaredMethods()));
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
                        if (excludedMethods.contains(c.getName()
                                + method.getName())) {
                            // blacklisted method for specific classes
                            continue;
                        }
                        if (excludedMethods.contains(method.getName())) {
                            // blacklisted method for all classes
                            continue;
                        }
                        // just to make sure we can invoke it
                        method.setAccessible(true);
                        method.invoke(newInstance);
                    }
                } catch (Exception e) {
                    System.err.println("problem with method " + c.getName()
                            + "# " + method.getName());
                    e.printStackTrace();
                    throw e;
                }
            }
        }

    }

    private void prepareMockUI(Component newInstance) {
        UI ui = Mockito.mock(UI.class);
        Mockito.when(ui.getLocale()).thenReturn(Locale.ENGLISH);
        ConnectorTracker connectorTracker = Mockito
                .mock(ConnectorTracker.class);
        Mockito.when(ui.getConnectorTracker()).thenReturn(connectorTracker);
        Mockito.doThrow(new RuntimeException("getState(true) called in getter"))
                .when(connectorTracker).markDirty(newInstance);

        newInstance.setParent(ui);
    }

    private Component construct(Class<? extends Component> c) {
        try {
            try {
                Constructor<? extends Component> declaredConstructor = c
                        .getDeclaredConstructor();
                declaredConstructor.setAccessible(true);
                return declaredConstructor.newInstance();
            } catch (NoSuchMethodException e) {
                return c.newInstance();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
