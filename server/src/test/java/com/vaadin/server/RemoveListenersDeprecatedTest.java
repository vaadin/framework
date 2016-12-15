package com.vaadin.server;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.provider.AbstractDataProvider;
import com.vaadin.event.EventRouter;
import com.vaadin.event.MethodEventSource;
import com.vaadin.shared.Registration;
import com.vaadin.tests.VaadinClasses;

public class RemoveListenersDeprecatedTest {

    private static final List<Predicate<Method>> ALLOW_REMOVE_LISTENER = new ArrayList<>();

    static {
        ALLOW_REMOVE_LISTENER.add(
                RemoveListenersDeprecatedTest::acceptAbstarctClientConnectorRemoveMethods);
        ALLOW_REMOVE_LISTENER
                .add(RemoveListenersDeprecatedTest::acceptAbstractDataProvider);
        ALLOW_REMOVE_LISTENER
                .add(RemoveListenersDeprecatedTest::acceptMethodEventSource);
    }

    @Test
    public void allRemoveListenerMethodsMarkedAsDeprecated() {
        Pattern removePattern = Pattern.compile("remove.*Listener");
        Pattern addPattern = Pattern.compile("add.*Listener");
        int count = 0;
        for (Class<? extends Object> serverClass : VaadinClasses
                .getAllServerSideClasses()) {
            count++;
            if (serverClass.equals(EventRouter.class)) {
                continue;
            }
            for (Method method : serverClass.getDeclaredMethods()) {
                if (Modifier.isPrivate(method.getModifiers())) {
                    continue;
                }
                if (addPattern.matcher(method.getName()).matches()
                        && method.getAnnotation(Deprecated.class) == null) {
                    Class<?> returnType = method.getReturnType();
                    Assert.assertEquals(
                            "Method " + method.getName()
                                    + " is not deprectated in class "
                                    + serverClass.getName()
                                    + " and doesn't return a Registration object",
                            Registration.class, returnType);
                }
                if (ALLOW_REMOVE_LISTENER.stream()
                        .anyMatch(predicate -> predicate.test(method))) {
                    continue;
                }

                if (removePattern.matcher(method.getName()).matches()) {
                    Assert.assertNotNull(
                            "Method " + method.getName() + " in class "
                                    + serverClass.getName()
                                    + " has not been marked as deprecated.",
                            method.getAnnotation(Deprecated.class));
                }
            }
        }
        Assert.assertTrue(count > 0);
    }

    private static boolean acceptMethodEventSource(Method method) {
        return method.getDeclaringClass().equals(MethodEventSource.class)
                && method.getParameterCount() == 2;
    }

    private static boolean acceptAbstarctClientConnectorRemoveMethods(
            Method method) {
        if (method.getDeclaringClass().equals(AbstractClientConnector.class)) {
            if (method.getParameterCount() == 2) {
                return true;
            } else if (method.getParameterCount() == 0) {
                return false;
            } else {
                return method.getParameterTypes()[0].equals(String.class);
            }
        }
        return false;
    }

    private static boolean acceptAbstractDataProvider(Method method) {
        return method.getDeclaringClass().equals(AbstractDataProvider.class)
                && method.getParameterCount() == 2;
    }
}
