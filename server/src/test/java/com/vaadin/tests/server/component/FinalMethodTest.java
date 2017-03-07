/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.server.component;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.tests.VaadinClasses;
import com.vaadin.ui.Component;

public class FinalMethodTest {

    // public void testThatContainersHaveNoFinalMethods() {
    // HashSet<Class<?>> tested = new HashSet<Class<?>>();
    // for (Class<?> c : VaadinClasses.getAllServerSideClasses()) {
    // if (Container.class.isAssignableFrom(c)) {
    // ensureNoFinalMethods(c, tested);
    // }
    // }
    // }

    @Test
    public void testThatComponentsHaveNoFinalMethods() {
        HashSet<Class<?>> tested = new HashSet<>();
        int count = 0;
        for (Class<? extends Component> c : VaadinClasses.getComponents()) {
            ensureNoFinalMethods(c, tested);
            count++;
        }
        Assert.assertTrue(count > 0);
    }

    private void ensureNoFinalMethods(Class<?> clazz,
            HashSet<Class<?>> tested) {
        if (tested.contains(clazz)) {
            return;
        }

        tested.add(clazz);

        if (clazz == null || clazz.equals(Object.class)) {
            return;
        }
        System.out.println("Checking " + clazz.getName());
        for (Method m : clazz.getDeclaredMethods()) {
            if (isPrivate(m)) {
                continue;
            }
            if (isFinal(m)) {
                String error = "Class " + clazz.getName() + " contains a "
                        + (isPublic(m) ? "public" : "non-public")
                        + " final method: " + m.getName();
                // System.err.println(error);
                throw new RuntimeException(error);
            }
        }
        ensureNoFinalMethods(clazz.getSuperclass(), tested);

    }

    private boolean isFinal(Method m) {
        return Modifier.isFinal(m.getModifiers());
    }

    private boolean isPrivate(Method m) {
        return Modifier.isPrivate(m.getModifiers());
    }

    private boolean isPublic(Method m) {
        return Modifier.isPublic(m.getModifiers());
    }
}
