/**
 * Copyright (C) 2012 Vaadin Ltd
 *
 * This program is available under Commercial Vaadin Add-On License 3.0
 * (CVALv3).
 *
 * See the file licensing.txt distributed with this software for more
 * information about licensing.
 *
 * You should have received a copy of the license along with this program.
 * If not, see <http://vaadin.com/license/cval-3>.
 */

package com.vaadin.testbench.parallel;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Parameterized;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.annotations.BrowserConfiguration;
import com.vaadin.testbench.annotations.BrowserFactory;
import com.vaadin.testbench.annotations.RunLocally;

/**
 * NOTE: The compatibility of this overridden class must be checked if TestBench
 * version is ever upgraded from 5.2.0.
 * <p>
 * Initial commit contains changes to {@link TBMethod#invokeExplosively}
 * <p>
 * <br>
 * This runner is loosely based on FactoryTestRunner by Ted Young
 * (http://tedyoung.me/2011/01/23/junit-runtime-tests-custom-runners/). The
 * generated test names give information about the parameters used (unlike
 * {@link Parameterized}).
 */
public class ParallelRunner extends BlockJUnit4ClassRunner {

    private static Logger logger = Logger
            .getLogger(ParallelRunner.class.getName());

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
        MAX_CONCURRENT_TESTS = Parameters.getMaxThreads();
        service = Executors.newFixedThreadPool(MAX_CONCURRENT_TESTS);
    }

    public ParallelRunner(Class<?> klass) throws InitializationError {
        super(klass);
        setScheduler(new ParallelScheduler(service));
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        List<FrameworkMethod> tests = new LinkedList<>();

        if (!ParallelTest.class
                .isAssignableFrom(getTestClass().getJavaClass())) {
            throw new RuntimeException(getClass().getName() + " only supports "
                    + ParallelTest.class.getName());
        }

        BrowserUtil.setBrowserFactory(getBrowserFactory());
        try {
            Collection<DesiredCapabilities> desiredCapabilities = getDesiredCapabilities();

            TestNameSuffix testNameSuffixProperty = findAnnotation(
                    getTestClass().getJavaClass(), TestNameSuffix.class);

            for (FrameworkMethod m : getTestMethods()) {
                // No browsers available for this test, so we need to
                // wrap the test method inside IgnoredTestMethod.
                // This will add @Ignore annotation to it.
                if (desiredCapabilities.size() <= 0
                        || categoryIsExcludedOrNotExcplicitlyIncluded()) {
                    tests.add(new IgnoredTestMethod(m.getMethod()));
                } else {
                    for (DesiredCapabilities capabilities : desiredCapabilities) {
                        TBMethod method = new TBMethod(m.getMethod(),
                                capabilities);
                        if (testNameSuffixProperty != null) {
                            method.setTestNameSuffix("-" + System.getProperty(
                                    testNameSuffixProperty.property()));
                        }
                        tests.add(method);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving browsers to run on",
                    e);
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
    private Collection<DesiredCapabilities> getDesiredCapabilities() {
        if (testRunsLocally()) {
            Collection<DesiredCapabilities> desiredCapabilities = new ArrayList<>();
            Class<?> javaTestClass = getTestClass().getJavaClass();
            desiredCapabilities.add(BrowserUtil.getBrowserFactory().create(
                    getRunLocallyBrowserName(javaTestClass),
                    getRunLocallyBrowserVersion(javaTestClass)));
            return desiredCapabilities;
        } else {
            return getFilteredCapabilities();
        }
    }

    private boolean testRunsLocally() {
        if (Parameters.getRunLocallyBrowserName() != null) {
            return true;
        }

        RunLocally runLocally = getTestClass().getJavaClass()
                .getAnnotation(RunLocally.class);
        if (runLocally == null) {
            return false;
        }
        return true;
    }

    static Browser getRunLocallyBrowserName(Class<?> testClass) {

        String runLocallyBrowserName = Parameters.getRunLocallyBrowserName();
        if (runLocallyBrowserName != null) {
            return Browser.valueOf(runLocallyBrowserName.toUpperCase());
        }
        RunLocally runLocally = testClass.getAnnotation(RunLocally.class);
        if (runLocally == null) {
            return null;
        }
        return runLocally.value();
    }

    static String getRunLocallyBrowserVersion(Class<?> testClass) {
        String runLocallyBrowserVersion = Parameters
                .getRunLocallyBrowserVersion();
        if (runLocallyBrowserVersion != null) {
            return runLocallyBrowserVersion;
        }

        RunLocally runLocally = testClass.getAnnotation(RunLocally.class);
        if (runLocally == null) {
            return "";
        }
        return runLocally.version();
    }

    private TestBenchBrowserFactory getBrowserFactory() {
        BrowserFactory browserFactoryAnnotation = getTestClass().getJavaClass()
                .getAnnotation(BrowserFactory.class);

        try {
            if (browserFactoryAnnotation != null
                    && TestBenchBrowserFactory.class.isAssignableFrom(
                            browserFactoryAnnotation.value())) {
                return (TestBenchBrowserFactory) browserFactoryAnnotation
                        .value().newInstance();
            }
        } catch (Exception e) {
        }

        return new DefaultBrowserFactory();
    }

    /*
     * Takes the desired browser capabilities defined in the test class and
     * returns a list of browser capabilities filtered browsers.include and
     * browsers.exclude system properties. (if present)
     */
    private Collection<DesiredCapabilities> getFilteredCapabilities() {

        Collection<DesiredCapabilities> desiredCapabilites = getBrowsersConfiguration();

        ArrayList<DesiredCapabilities> filteredCapabilities = new ArrayList<>();

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

    private Collection<DesiredCapabilities> getBrowsersConfiguration() {

        Class<?> klass = getTestClass().getJavaClass();

        while (klass != null) {
            Method[] declaredMethods = klass.getDeclaredMethods();
            for (Method method : declaredMethods) {
                // TODO if already found one annotated method in class, warn
                // user?

                if (method.isAnnotationPresent(BrowserConfiguration.class)) {
                    boolean methodSignatureIsValid = validateBrowserConfigurationAnnotatedSignature(
                            method);

                    if (!methodSignatureIsValid) {
                        /*
                         * ignore this method and searches for another
                         * BrowserConfiguration annotated method in this class'
                         * superclasses
                         */
                        break;
                    }

                    try {
                        return (Collection<DesiredCapabilities>) method
                                .invoke(getTestClassInstance());
                    } catch (Exception e) {
                        // Handle possible exceptions.

                        String errMsg = String.format(
                                "Error occurred while invoking BrowserConfiguration method %s.%s(). Method was ignored, searching BrowserConfiguration method in superclasses",
                                method.getDeclaringClass().getName(),
                                method.getName());
                        logger.log(Level.INFO, errMsg, e);

                        /*
                         * ignore this method and searches for another
                         * BrowserConfiguration annotated method in this class'
                         * superclasses
                         */
                        break;
                    }
                }
            }
            klass = klass.getSuperclass();
        }

        // No valid BrowserConfiguration annotated method was found
        return ParallelTest.getDefaultCapabilities();
    }

    /**
     * Validates the signature of a BrowserConfiguration annotated method.
     *
     * @param method
     *            BrowserConfiguration annotated method
     * @return true if method signature is valid. false otherwise.
     */
    private boolean validateBrowserConfigurationAnnotatedSignature(
            Method method) {
        String genericErrorMessage = "Error occurred while invoking BrowserConfigurationMethod %s.%s()."
                + " %s. Method was ignored, searching BrowserConfiguration method in superclasses";

        if (method.getParameterTypes().length != 0) {
            String errMsg = String.format(genericErrorMessage,
                    method.getDeclaringClass().getName(), method.getName(),
                    "BrowserConfiguration annotated method must not require any arguments");
            logger.info(errMsg);
            return false;
        }
        if (!Collection.class.isAssignableFrom(method.getReturnType())) {
            /*
             * Validates if method's return type is Collection.
             * ClassCastException may still occur if method's return type is not
             * Collection<DesiredCapabilities>
             */
            String errMsg = String.format(genericErrorMessage,
                    method.getDeclaringClass().getName(), method.getName(),
                    "BrowserConfiguration annotated method must return a Collection<DesiredCapabilities>");
            logger.info(errMsg);
            return false;
        }
        return true;
    }

    private ParallelTest getTestClassInstance() throws InstantiationException,
            IllegalAccessException, InvocationTargetException {
        ParallelTest testClassInstance = (ParallelTest) getTestClass()
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

    @Override
    protected Statement withBefores(final FrameworkMethod method,
            final Object target, Statement statement) {
        if (!(method instanceof TBMethod)) {
            throw new RuntimeException("Unexpected method type "
                    + method.getClass().getName() + ", expected TBMethod");
        }
        final TBMethod tbmethod = (TBMethod) method;

        // setDesiredCapabilities before running the real @Befores (which use
        // capabilities)

        final Statement realBefores = super.withBefores(method, target,
                statement);
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                ((ParallelTest) target)
                        .setDesiredCapabilities(tbmethod.capabilities);
                realBefores.evaluate();
            }
        };
    }

    public static class TBMethod extends FrameworkMethod {
        private final DesiredCapabilities capabilities;
        private String testNameSuffix = "";

        public TBMethod(Method method, DesiredCapabilities capabilities) {
            super(method);
            this.capabilities = capabilities;
        }

        public DesiredCapabilities getCapabilities() {
            return capabilities;
        }

        public void setTestNameSuffix(String testNameSuffix) {
            this.testNameSuffix = testNameSuffix;
        }

        @Override
        public Object invokeExplosively(final Object target, Object... params)
                throws Throwable {
            // Executes the test method with the supplied parameters
            try {
                return super.invokeExplosively(target);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        @Override
        public String getName() {
            return String.format("%s[%s]",
                    getMethod().getName() + testNameSuffix,
                    getUniqueIdentifier(capabilities));
        }

        @Override
        public boolean equals(Object obj) {
            if (!TBMethod.class.isInstance(obj)) {
                return false;
            }

            return ((TBMethod) obj).capabilities.equals(capabilities)
                    && super.equals(obj);
        }

        /**
         * Returns a string which uniquely (enough) identifies this browser.
         * Used mainly in screenshot names.
         */
        private static String getUniqueIdentifier(Capabilities capabilities) {
            String platform = BrowserUtil.getPlatform(capabilities);
            String browser = BrowserUtil.getBrowserIdentifier(capabilities);
            String version;
            if (capabilities == null) {
                version = "Unknown";
            } else {
                version = capabilities.getVersion();
            }
            return platform + "_" + browser + "_" + version;
        }
    }

}
