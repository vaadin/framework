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
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Parameterized;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.tests.tb3.AbstractTB3Test.BrowserUtil;

/**
 * This runner is loosely based on FactoryTestRunner by Ted Young
 * (http://tedyoung.me/2011/01/23/junit-runtime-tests-custom-runners/). The
 * generated test names give information about the parameters used (unlike
 * {@link Parameterized}).
 * 
 * @since 7.1
 */
public class TB3Runner extends BlockJUnit4ClassRunner {

    /**
     * This is the total limit of actual JUnit test instances run in parallel
     */
    private static final int MAX_CONCURRENT_TESTS = 50;

    /**
     * This is static so it is shared by all tests running concurrently on the
     * same machine and thus can limit the number of threads in use.
     */
    private static final ExecutorService service = Executors
            .newFixedThreadPool(MAX_CONCURRENT_TESTS);

    public TB3Runner(Class<?> klass) throws InitializationError {
        super(klass);
        setScheduler(new ParallelScheduler(service));
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        List<FrameworkMethod> tests = new LinkedList<FrameworkMethod>();

        if (!AbstractTB3Test.class.isAssignableFrom(getTestClass()
                .getJavaClass())) {
            throw new RuntimeException(getClass().getName() + " only supports "
                    + AbstractTB3Test.class.getName());
        }

        try {
            AbstractTB3Test testClassInstance = (AbstractTB3Test) getTestClass()
                    .getOnlyConstructor().newInstance();
            for (DesiredCapabilities capabilities : testClassInstance
                    .getBrowsersToTest()) {

                // Find any methods marked with @Test.
                for (FrameworkMethod m : getTestClass().getAnnotatedMethods(
                        Test.class)) {
                    tests.add(new TB3Method(m.getMethod(), capabilities));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving browsers to run on", e);
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
                try {
                    realBefores.evaluate();
                } catch (Throwable t) {
                    // Give the test a chance to e.g. produce an error
                    // screenshot before failing the test by re-throwing the
                    // exception
                    ((AbstractTB3Test) target).onUncaughtException(t);
                    throw t;
                }
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
            // Executes the test method with the supplied parameters
            return super.invokeExplosively(target);
        }

        @Override
        public String getName() {
            return String.format("%s[%s]", getMethod().getName(),
                    BrowserUtil.getUniqueIdentifier(capabilities));
        }

    }
}
