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
package com.vaadin.tests.tb3;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

/**
 * TestBench test runner which supports static @Parameters annotated methods
 * providing parameters for the corresponding setter.
 * <p>
 * {@code @Parameters public static Collection<String> getThemes() } creates one
 * permutation for each value returned by {@code getThemes()}. The value is
 * automatically assigned to the test instance using {@code setTheme(String)}
 * before invoking the test method
 *
 * @author Vaadin Ltd
 */
public class ParameterizedTB3Runner extends TB3Runner {

    public ParameterizedTB3Runner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        List<FrameworkMethod> methods = super.computeTestMethods();

        Map<Method, Collection<String>> parameters = new LinkedHashMap<>();

        // Find all @Parameters methods and invoke them to find out permutations

        for (Method m : getTestClass().getJavaClass().getMethods()) {
            Parameters p = m.getAnnotation(Parameters.class);
            if (p == null) {
                continue;
            }

            if (!m.getName().startsWith("get") || !m.getName().endsWith("s")) {
                throw new IllegalStateException("Method " + m.getName()
                        + " is annotated with @Parameter but is not named getSomeThings() as it should");
            }

            if (m.getParameterTypes().length != 0) {
                throw new IllegalStateException("Method " + m.getName()
                        + " annotated with @Parameter should not have any arguments");
            }

            if (!Modifier.isStatic(m.getModifiers())) {
                throw new IllegalStateException("Method " + m.getName()
                        + " annotated with @Parameter must be static");
            }

            // getThemes -> setTheme
            String setter = "set" + m.getName().substring("get".length());
            setter = setter.substring(0, setter.length() - 1);
            // property = property.substring(0, 1).toLowerCase()
            // + property.substring(1);

            Method setterMethod;
            try {
                setterMethod = getTestClass().getJavaClass().getMethod(setter,
                        String.class);
            } catch (Exception e) {
                throw new IllegalStateException(
                        "No setter " + setter + " found in "
                                + getTestClass().getJavaClass().getName(),
                        e);
            }

            Collection<String> values;
            try {
                values = (Collection<String>) m.invoke(null);
                if (!values.isEmpty()) {
                    // Ignore any empty collections to allow e.g. integration
                    // tests to use "/demo" path by default without adding that
                    // to the screenshot name
                    parameters.put(setterMethod, values);
                }
            } catch (Exception e) {
                throw new IllegalStateException(
                        "The setter " + m.getName() + " could not be invoked",
                        e);
            }
        }

        // Add method permutations for all @Parameters
        for (Method setter : parameters.keySet()) {
            List<FrameworkMethod> newMethods = new ArrayList<>();
            for (FrameworkMethod m : methods) {

                if (!(m instanceof TBMethod)) {
                    System.err.println(
                            "Unknown method type: " + m.getClass().getName());
                    newMethods.add(m);
                    continue;
                }

                // testFoo
                // testBar
                // ->
                // testFoo[valo]
                // testFoo[runo]
                // testBar[valo]
                // testBar[runo]

                for (final String value : parameters.get(setter)) {
                    newMethods.add(new TBMethodWithBefore((TBMethod) m, setter,
                            value));
                }
            }
            // Update methods so next parameters will use all expanded methods
            methods = newMethods;
        }
        return methods;
    }

    public static class TBMethodWithBefore extends TBMethod {

        private Method setter;
        private String value;
        private TBMethod parent;

        public TBMethodWithBefore(TBMethod m, Method setter, String value) {
            super(m.getMethod(), m.getCapabilities());
            parent = m;
            this.setter = setter;
            this.value = value;
        }

        @Override
        public Object invokeExplosively(Object target, Object... params)
                throws Throwable {
            setter.invoke(target, value);
            return parent.invokeExplosively(target, params);
        }

        @Override
        public String getName() {
            return parent.getName() + "[" + value + "]";
        };

        @Override
        public boolean equals(Object obj) {
            if (!TBMethodWithBefore.class.isInstance(obj)) {
                return false;
            }

            TBMethodWithBefore otherTbMethod = (TBMethodWithBefore) obj;

            return super.equals(obj)
                    && Objects.equals(otherTbMethod.parent, parent)
                    && Objects.equals(otherTbMethod.setter, setter)
                    && Objects.equals(otherTbMethod.value, value);
        }

    }
}
