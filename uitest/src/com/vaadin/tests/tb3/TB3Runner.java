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

package com.vaadin.tests.tb3;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Parameterized;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.internal.HttpClientFactory;

import com.vaadin.tests.annotations.TestCategory;
import com.vaadin.tests.tb3.AbstractTB3Test.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest.Browser;

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
     * Socket timeout for HTTP connections to the grid hub. The connection is
     * closed after 30 minutes of inactivity to avoid builds hanging for up to
     * three hours per connection if the test client crashes/hangs.
     */
    private static final int SOCKET_TIMEOUT = 30 * 60 * 1000;

    /**
     * This is the total limit of actual JUnit test instances run in parallel
     */
    private static final int MAX_CONCURRENT_TESTS;

    /**
     * This is static so it is shared by all tests running concurrently on the
     * same machine and thus can limit the number of threads in use.
     */
    private static final ExecutorService service;

    static {
        if (localWebDriverIsUsed()) {
            MAX_CONCURRENT_TESTS = 10;
        } else {
            MAX_CONCURRENT_TESTS = 50;
        }
        service = Executors.newFixedThreadPool(MAX_CONCURRENT_TESTS);

        // reduce socket timeout to avoid tests hanging for three hours
        try {
            Field field = HttpCommandExecutor.class
                    .getDeclaredField("httpClientFactory");
            assert (Modifier.isStatic(field.getModifiers()));
            field.setAccessible(true);
            field.set(null, new HttpClientFactory() {
                @Override
                public HttpParams getHttpParams() {
                    HttpParams params = super.getHttpParams();
                    // fifteen minute timeout
                    HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT);
                    return params;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(
                    "Changing socket timeout for TestBench failed", e);
        }
    }

    protected static boolean localWebDriverIsUsed() {
        String useLocalWebDriver = System.getProperty("useLocalWebDriver");

        return useLocalWebDriver != null
                && useLocalWebDriver.toLowerCase().equals("true");
    }

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
            AbstractTB3Test testClassInstance = getTestClassInstance();
            Collection<DesiredCapabilities> desiredCapabilities = getDesiredCapabilities(testClassInstance);

            TestNameSuffix testNameSuffixProperty = findAnnotation(
                    testClassInstance.getClass(), TestNameSuffix.class);

            for (FrameworkMethod m : getTestMethods()) {
                // No browsers available for this test, so we need to
                // wrap the test method inside IgnoredTestMethod.
                // This will add @Ignore annotation to it.
                if (desiredCapabilities.size() <= 0
                        || categoryIsExcludedOrNotExcplicitlyIncluded()) {
                    tests.add(new IgnoredTestMethod(m.getMethod()));
                } else {
                    for (DesiredCapabilities capabilities : desiredCapabilities) {
                        TB3Method method = new TB3Method(m.getMethod(),
                                capabilities);
                        if (testNameSuffixProperty != null) {
                            method.setTestNameSuffix("-"
                                    + System.getProperty(testNameSuffixProperty
                                            .property()));
                        }
                        tests.add(method);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving browsers to run on", e);
        }

        return tests;
    }

    private boolean categoryIsExcludedOrNotExcplicitlyIncluded() {
        Class<?> c = getTestClass().getJavaClass();

        if (categoryIsExcluded(c)) {
            return true;
        }

        if (explicitInclusionIsUsed()) {
            return !categoryIsIncluded(c);
        }

        return false;
    }

    private boolean categoryIsIncluded(Class<?> c) {
        String include = System.getProperty("categories.include");
        if (include != null && include.trim().length() > 0) {
            return hasCategoryFor(c, include.toLowerCase().trim());
        }

        return false;
    }

    private static boolean explicitInclusionIsUsed() {
        String include = System.getProperty("categories.include");

        return include != null && include.trim().length() > 0;
    }

    private static boolean categoryIsExcluded(Class<?> c) {
        String exclude = System.getProperty("categories.exclude");
        if (exclude != null && exclude.trim().length() > 0) {
            return hasCategoryFor(c, exclude.toLowerCase().trim());
        }

        return false;
    }

    private static boolean hasCategoryFor(Class<?> c, String searchString) {
        if (hasCategory(c)) {
            return searchString.contains(getCategory(c).toLowerCase());
        }

        return false;
    }

    private static boolean hasCategory(Class<?> c) {
        return c.getAnnotation(TestCategory.class) != null;
    }

    private static String getCategory(Class<?> c) {
        return c.getAnnotation(TestCategory.class).value();
    }

    private List<FrameworkMethod> getTestMethods() {
        return getTestClass().getAnnotatedMethods(Test.class);
    }

    /*
     * Returns a list of desired browser capabilities according to browsers
     * defined in the test class, filtered by possible filter parameters. Use
     * {@code @RunLocally} annotation or com.vaadin.testbench.runLocally
     * property to override all capabilities.
     */
    private Collection<DesiredCapabilities> getDesiredCapabilities(
            AbstractTB3Test testClassInstance) {
        Collection<DesiredCapabilities> desiredCapabilites = getFilteredCapabilities(testClassInstance);

        Browser runLocallyBrowser = testClassInstance.getRunLocallyBrowser();
        if (runLocallyBrowser != null) {
            desiredCapabilites = new ArrayList<DesiredCapabilities>();
            desiredCapabilites.add(runLocallyBrowser.getDesiredCapabilities());
        }

        return desiredCapabilites;
    }

    /*
     * Takes the desired browser capabilities defined in the test class and
     * returns a list of browser capabilities filtered browsers.include and
     * browsers.exclude system properties. (if present)
     */
    private Collection<DesiredCapabilities> getFilteredCapabilities(
            AbstractTB3Test testClassInstance) {
        Collection<DesiredCapabilities> desiredCapabilites = testClassInstance
                .getBrowsersToTest();

        ArrayList<DesiredCapabilities> filteredCapabilities = new ArrayList<DesiredCapabilities>();

        String include = System.getProperty("browsers.include");
        String exclude = System.getProperty("browsers.exclude");

        for (DesiredCapabilities d : desiredCapabilites) {
            String browserName = (d.getBrowserName() + d.getVersion())
                    .toLowerCase();
            if (include != null && include.trim().length() > 0) {
                if (include.trim().toLowerCase().contains(browserName)) {
                    filteredCapabilities.add(d);
                }
            } else {
                filteredCapabilities.add(d);
            }

            if (exclude != null && exclude.trim().length() > 0) {
                if (exclude.trim().toLowerCase().contains(browserName)) {
                    filteredCapabilities.remove(d);
                }
            }

        }
        return filteredCapabilities;
    }

    private AbstractTB3Test getTestClassInstance()
            throws InstantiationException, IllegalAccessException,
            InvocationTargetException {
        AbstractTB3Test testClassInstance = (AbstractTB3Test) getTestClass()
                .getOnlyConstructor().newInstance();
        return testClassInstance;
    }

    // This is a FrameworkMethod class that will always
    // return @Ignore and @Test annotations for the wrapped method.
    private class IgnoredTestMethod extends FrameworkMethod {

        private class IgnoreTestAnnotations {

            // We use this method to easily get our hands on
            // the Annotation instances for @Ignore and @Test
            @Ignore
            @Test
            public void ignoredTest() {
            }
        }

        public IgnoredTestMethod(Method method) {
            super(method);
        }

        @Override
        public Annotation[] getAnnotations() {
            return getIgnoredTestMethod().getAnnotations();
        }

        private Method getIgnoredTestMethod() {
            try {
                return IgnoreTestAnnotations.class.getMethod("ignoredTest",
                        null);
            } catch (Exception e) {
                return null;
            }

        }

        @Override
        public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
            return getIgnoredTestMethod().getAnnotation(annotationType);
        }
    }

    /**
     * Finds the given annotation in the given class or one of its super
     * classes. Return the first found annotation
     * 
     * @param searchClass
     * @param annotationClass
     * @return
     */
    private <T extends Annotation> T findAnnotation(Class<?> searchClass,
            Class<T> annotationClass) {
        if (searchClass == Object.class) {
            return null;
        }

        if (searchClass.getAnnotation(annotationClass) != null) {
            return searchClass.getAnnotation(annotationClass);
        }

        return findAnnotation(searchClass.getSuperclass(), annotationClass);
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
        private String testNameSuffix = "";

        public TB3Method(Method method, DesiredCapabilities capabilities) {
            super(method);
            this.capabilities = capabilities;
        }

        public void setTestNameSuffix(String testNameSuffix) {
            this.testNameSuffix = testNameSuffix;
        }

        @Override
        public Object invokeExplosively(final Object target, Object... params)
                throws Throwable {
            // Executes the test method with the supplied parameters
            return super.invokeExplosively(target);
        }

        @Override
        public String getName() {
            return String.format("%s[%s]", getMethod().getName()
                    + testNameSuffix,
                    BrowserUtil.getUniqueIdentifier(capabilities));
        }

    }

}
