package com.vaadin.server;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.tests.VaadinClasses;

public class RemoveListenersDeprecatedTest {

    @Test
    public void allRemoveListenerMethodsMarkedAsDeprecated() {
        Pattern methodPattern = Pattern.compile("remove.*Listener");
        for (Class<? extends Object> serverClass : VaadinClasses
                .getComponents()) {
            for (Method method : serverClass.getMethods()) {
                if (methodPattern.matcher(method.getName()).matches()) {
                    Assert.assertNotNull(
                            "Method " + method.getName() + " in class "
                                    + serverClass.getSimpleName()
                                    + " has not been marked as deprecated.",
                            method.getAnnotation(Deprecated.class));
                }
            }
        }
    }
}
