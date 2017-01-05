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
package com.vaadin.tests.design;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;

import com.vaadin.shared.Connector;
import com.vaadin.ui.Component;
import com.vaadin.ui.Flash;

public abstract class DeclarativeTestBase<T extends Component>
        extends DeclarativeTestBaseBase<T> {

    private static final boolean debug = false;

    private final Map<Class<?>, EqualsAsserter<?>> comparators = new HashMap<>();
    private static final EqualsAsserter standardEqualsComparator = (EqualsAsserter<Object>) Assert::assertEquals;

    public class IntrospectorEqualsAsserter<C> implements EqualsAsserter<C> {

        private final Class<C> clazz;

        public IntrospectorEqualsAsserter(Class<C> clazz) {
            this.clazz = clazz;
        }

        @Override
        public void assertObjectEquals(C object1, C object2) {
            try {
                BeanInfo bi = Introspector.getBeanInfo(clazz);
                for (PropertyDescriptor pd : bi.getPropertyDescriptors()) {
                    Method readMethod = pd.getReadMethod();
                    Method writeMethod = pd.getWriteMethod();

                    if (acceptProperty(clazz, readMethod, writeMethod)) {
                        Object property1 = readMethod.invoke(object1);
                        Object property2 = readMethod.invoke(object2);
                        assertEquals(pd.getDisplayName(), property1, property2);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    {
        comparators.put(Flash.class,
                new IntrospectorEqualsAsserter<Flash>(Flash.class) {
                    @Override
                    public void assertObjectEquals(Flash o1, Flash o2) {
                        super.assertObjectEquals(o1, o2);
                        assertEquals("parameterNames", o1.getParameterNames(),
                                o2.getParameterNames());
                        for (String name : o1.getParameterNames()) {
                            assertEquals("Parameter " + name,
                                    o1.getParameter(name),
                                    o2.getParameter(name));
                        }
                    }
                });
    }

    protected boolean acceptProperty(Class<?> clazz, Method readMethod,
            Method writeMethod) {
        if (readMethod == null || writeMethod == null) {
            return false;
        }
        // Needed to access public properties inherited from a
        // nonpublic superclass, see #17425
        readMethod.setAccessible(true);
        writeMethod.setAccessible(true);
        if (Connector.class.isAssignableFrom(clazz)
                && readMethod.getName().equals("getParent")) {
            // Hack to break cycles in the connector hierarchy
            return false;
        }
        try {
            clazz.getDeclaredMethod(readMethod.getName());
        } catch (Exception e) {
            // Not declared in this class, will be tested by parent
            // class tester
            if (debug) {
                System.out.println("Skipped " + clazz.getSimpleName() + "."
                        + readMethod.getName());
            }
            return false;
        }

        if (debug) {
            System.out.println("Testing " + clazz.getSimpleName() + "."
                    + readMethod.getName());
        }
        return true;
    }

    @Override
    protected EqualsAsserter getComparator(Class c) {
        com.vaadin.tests.design.DeclarativeTestBaseBase.EqualsAsserter<?> comp = comparators
                .get(c);
        if (comp == null) {
            if (c.isEnum()) {
                return standardEqualsComparator;
            }
            if (debug) {
                System.out.println("No comparator found for " + c.getName()
                        + ". Using introspector.");
            }
            return new IntrospectorEqualsAsserter<>(c);
        }
        return comp;
    }

}
