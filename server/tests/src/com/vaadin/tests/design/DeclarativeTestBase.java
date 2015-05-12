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

public abstract class DeclarativeTestBase<T extends Component> extends
        DeclarativeTestBaseBase<T> {

    private static boolean debug = false;

    private final Map<Class<?>, EqualsAsserter<?>> comparators = new HashMap<Class<?>, EqualsAsserter<?>>();
    private static EqualsAsserter standardEqualsComparator = new EqualsAsserter<Object>() {

        @Override
        public void assertObjectEquals(Object o1, Object o2) {
            Assert.assertEquals(o1, o2);
        }
    };

    public class IntrospectorEqualsAsserter<T> implements EqualsAsserter<T> {

        private Class<T> c;

        public IntrospectorEqualsAsserter(Class<T> c) {
            this.c = c;
        }

        @Override
        public void assertObjectEquals(T o1, T o2) {
            try {
                BeanInfo bi = Introspector.getBeanInfo(c);
                for (PropertyDescriptor pd : bi.getPropertyDescriptors()) {
                    Method readMethod = pd.getReadMethod();
                    Method writeMethod = pd.getWriteMethod();
                    if (readMethod == null || writeMethod == null) {
                        continue;
                    }
                    // Needed to access public properties inherited from a
                    // nonpublic superclass, see #17425
                    readMethod.setAccessible(true);
                    writeMethod.setAccessible(true);
                    if (Connector.class.isAssignableFrom(c)
                            && readMethod.getName().equals("getParent")) {
                        // Hack to break cycles in the connector hierarchy
                        continue;
                    }
                    try {
                        c.getDeclaredMethod(readMethod.getName());
                    } catch (Exception e) {
                        // Not declared in this class, will be tested by parent
                        // class tester
                        if (debug) {
                            System.out.println("Skipped " + c.getSimpleName()
                                    + "." + readMethod.getName());
                        }
                        continue;
                    }

                    if (debug) {
                        System.out.println("Testing " + c.getSimpleName() + "."
                                + readMethod.getName());
                    }
                    Object v1 = readMethod.invoke(o1);
                    Object v2 = readMethod.invoke(o2);
                    assertEquals(pd.getDisplayName(), v1, v2);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    {
        comparators.put(Flash.class, new IntrospectorEqualsAsserter<Flash>(
                Flash.class) {
            @Override
            public void assertObjectEquals(Flash o1, Flash o2) {
                super.assertObjectEquals(o1, o2);
                assertEquals("parameterNames", o1.getParameterNames(),
                        o2.getParameterNames());
                for (String name : o1.getParameterNames()) {
                    assertEquals("Parameter " + name, o1.getParameter(name),
                            o2.getParameter(name));
                }
            }
        });
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
            return new IntrospectorEqualsAsserter<T>(c);
        }
        return comp;
    }
}
