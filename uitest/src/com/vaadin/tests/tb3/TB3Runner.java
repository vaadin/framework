/*
 * Copyright 2000-2013 Vaadin Ltd.
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
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.sass.testcases.scss.SassTestRunner.FactoryTest;
import com.vaadin.sass.testcases.scss.SassTestRunner.TestFactory;

/**
 * Test runner that executes methods annotated with @{@link FactoryTest} with
 * all the values returned by a method annotated with @{@link TestFactory} as
 * their parameters parameter.
 * 
 * This runner is loosely based on FactoryTestRunner by Ted Young
 * (http://tedyoung.me/2011/01/23/junit-runtime-tests-custom-runners/). The
 * generated test names give information about the parameters used (unlike
 * {@link Parameterized}).
 * 
 * @since 7.1
 */
public class TB3Runner extends BlockJUnit4ClassRunner {

    public TB3Runner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        List<FrameworkMethod> tests = new LinkedList<FrameworkMethod>();

        // Final all methods in our test class marked with @Parameters.
        for (FrameworkMethod method : getTestClass().getAnnotatedMethods(
                Parameters.class)) {
            // Make sure the Parameters method is static
            if (!Modifier.isStatic(method.getMethod().getModifiers())) {
                throw new IllegalArgumentException("@Parameters " + method
                        + " must be static.");
            }

            // Execute the method (statically)
            Object params;
            try {
                params = method.getMethod().invoke(
                        getTestClass().getJavaClass());
            } catch (Throwable t) {
                throw new RuntimeException("Could not run test factory method "
                        + method.getName(), t);
            }

            // Did the factory return an array? If so, make it a list.
            if (params.getClass().isArray()) {
                params = Arrays.asList((Object[]) params);
            }

            // Did the factory return a scalar object? If so, put it in a list.
            if (!(params instanceof Iterable<?>)) {
                params = Collections.singletonList(params);
            }

            // For each object returned by the factory.
            for (Object param : (Iterable<?>) params) {
                if (!(param instanceof DesiredCapabilities)) {
                    throw new RuntimeException("Unexpected parameter type "
                            + param.getClass().getName()
                            + " when expecting DesiredCapabilities");
                }
                DesiredCapabilities capabilities = (DesiredCapabilities) param;
                // Find any methods marked with @Test.
                for (FrameworkMethod m : getTestClass().getAnnotatedMethods(
                        Test.class)) {
                    tests.add(new TB3Method(m.getMethod(), capabilities));
                }
            }
        }

        return tests;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.junit.runners.BlockJUnit4ClassRunner#withBefores(org.junit.runners
     * .model.FrameworkMethod, java.lang.Object,
     * org.junit.runners.model.Statement)
     */
    @Override
    protected Statement withBefores(final FrameworkMethod method,
            final Object target, Statement statement) {
        if (!(method instanceof TB3Method)) {
            throw new RuntimeException("Unexpected method type "
                    + method.getClass().getName() + ", expected TB3Method");
        }
        final TB3Method tb3method = (TB3Method) method;

        // setDesiredCapabilities before running the real @Befores (which use
        // capabilities)

        final Statement realBefores = super.withBefores(method, target,
                statement);
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                ((AbstractTB3Test) target)
                        .setDesiredCapabilities(tb3method.capabilities);
                realBefores.evaluate();
            }
        };
    }

    private static class TB3Method extends FrameworkMethod {
        private DesiredCapabilities capabilities;

        public TB3Method(Method method, DesiredCapabilities capabilities) {
            super(method);
            this.capabilities = capabilities;
        }

        @Override
        public Object invokeExplosively(final Object target, Object... params)
                throws Throwable {
            // Executes the test method with the supplied parameters (returned
            // by the
            // TestFactory) and not the instance generated by FrameworkMethod.
            return super.invokeExplosively(target);
        }

        @Override
        public String getName() {
            return String.format("%s[%s]", getMethod().getName(),
                    format(capabilities));
        }

        private String format(DesiredCapabilities capabilities) {
            String browserName = capabilities.getBrowserName();
            if (BrowserType.IE.equals(browserName)) {
                browserName = "IE";
            } else if (BrowserType.FIREFOX.equals(browserName)) {
                browserName = "Firefox";
            } else if (BrowserType.CHROME.equals(browserName)) {
                browserName = "Chrome";
            } else if (BrowserType.SAFARI.equals(browserName)) {
                browserName = "Safari";
            } else if (BrowserType.OPERA.equals(browserName)) {
                browserName = "Opera";
            }

            return browserName + capabilities.getVersion();
        }
    }
}
