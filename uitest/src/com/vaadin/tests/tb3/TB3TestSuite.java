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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.jar.JarEntry;

import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;

/**
 * Test suite which consists of all the TB3 tests passed in the constructor.
 * Runs the tests in parallel using a {@link ParallelScheduler}
 * 
 * @author Vaadin Ltd
 */
public class TB3TestSuite extends Suite {

    /**
     * This only restricts the number of test suites running concurrently. The
     * number of tests to run concurrently are configured in {@link TB3Runner}.
     */
    private static final int MAX_CONCURRENT_TEST_SUITES = 20;

    /**
     * This is static so it is shared by all test suites running concurrently on
     * the same machine and thus can limit the number of threads in use.
     */
    private final ExecutorService service = Executors
            .newFixedThreadPool(MAX_CONCURRENT_TEST_SUITES);

    public TB3TestSuite(Class<?> klass,
            Class<? extends AbstractTB3Test> baseClass, String basePackage,
            String[] ignorePackages) throws InitializationError {
        super(klass, findTests(baseClass, basePackage, ignorePackages));
        setScheduler(new ParallelScheduler(service));
    }

    /**
     * Traverses the directory on the classpath (inside or outside a Jar file)
     * specified by 'basePackage'. Collects all classes inside the location
     * which can be assigned to 'baseClass' except for classes inside packages
     * listed in 'ignoredPackages'.
     * 
     * @param baseClass
     * @param basePackage
     * @param ignorePackages
     * @return
     */
    private static Class<?>[] findTests(
            Class<? extends AbstractTB3Test> baseClass, String basePackage,
            String[] ignorePackages) {
        try {
            List<?> l = findClasses(baseClass, basePackage, ignorePackages);
            return l.toArray(new Class[] {});
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Traverses the directory on the classpath (inside or outside a Jar file)
     * specified by 'basePackage'. Collects all classes inside the location
     * which can be assigned to 'baseClass' except for classes inside packages
     * listed in 'ignoredPackages'.
     * 
     * @param baseClass
     * @param basePackage
     * @param ignoredPackages
     * @return
     * @throws IOException
     */
    private static <T> List<Class<? extends T>> findClasses(Class<T> baseClass,
            String basePackage, String[] ignoredPackages) throws IOException {
        List<Class<? extends T>> classes = new ArrayList<Class<? extends T>>();
        String basePackageDirName = "/" + basePackage.replace('.', '/');
        URL location = baseClass.getResource(basePackageDirName);
        if (location.getProtocol().equals("file")) {
            try {
                File f = new File(location.toURI());
                if (!f.exists()) {
                    throw new IOException("Directory " + f.toString()
                            + " does not exist");
                }
                findPackages(f, basePackage, baseClass, classes,
                        ignoredPackages);
            } catch (URISyntaxException e) {
                throw new IOException(e.getMessage());
            }
        } else if (location.getProtocol().equals("jar")) {
            JarURLConnection juc = (JarURLConnection) location.openConnection();
            findClassesInJar(juc, basePackage, baseClass, classes);
        }

        Collections.sort(classes, new Comparator<Class<? extends T>>() {

            @Override
            public int compare(Class<? extends T> o1, Class<? extends T> o2) {
                return o1.getName().compareTo(o2.getName());
            }

        });
        return classes;
    }

    /**
     * Traverses the given directory and collects all classes which are inside
     * the given 'javaPackage' and can be assigned to the given 'baseClass'. The
     * found classes are added to 'result'.
     * 
     * @param parent
     *            The directory to traverse
     * @param javaPackage
     *            The java package which 'parent' contains
     * @param baseClass
     *            The class which the target classes extend
     * @param result
     *            The collection to which found classes are added
     * @param ignoredPackages
     *            A collection of packages (including sub packages) to ignore
     */
    private static <T> void findPackages(File parent, String javaPackage,
            Class<T> baseClass, Collection<Class<? extends T>> result,
            String[] ignoredPackages) {
        for (String ignoredPackage : ignoredPackages) {
            if (javaPackage.equals(ignoredPackage)) {
                return;
            }
        }

        for (File file : parent.listFiles()) {
            if (file.isDirectory()) {
                findPackages(file, javaPackage + "." + file.getName(),
                        baseClass, result, ignoredPackages);
            } else if (file.getName().endsWith(".class")) {
                String fullyQualifiedClassName = javaPackage + "."
                        + file.getName().replace(".class", "");
                addClassIfMatches(result, fullyQualifiedClassName, baseClass);
            }
        }

    }

    /**
     * Traverses a Jar file using the given connection and collects all classes
     * which are inside the given 'javaPackage' and can be assigned to the given
     * 'baseClass'. The found classes are added to 'result'.
     * 
     * @param javaPackage
     *            The java package containing the classes (classes may be in a
     *            sub package)
     * @param baseClass
     *            The class which the target classes extend
     * @param result
     *            The collection to which found classes are added
     * @throws IOException
     */
    private static <T> void findClassesInJar(JarURLConnection juc,
            String javaPackage, Class<T> baseClass,
            Collection<Class<? extends T>> result) throws IOException {
        String javaPackageDir = javaPackage.replace('.', '/');
        Enumeration<JarEntry> ent = juc.getJarFile().entries();
        while (ent.hasMoreElements()) {
            JarEntry e = ent.nextElement();
            if (e.getName().endsWith(".class")
                    && e.getName().startsWith(javaPackageDir)) {
                String fullyQualifiedClassName = e.getName().replace('/', '.')
                        .replace(".class", "");
                addClassIfMatches(result, fullyQualifiedClassName, baseClass);
            }
        }
    }

    /**
     * Verifies that the class represented by 'fullyQualifiedClassName' can be
     * loaded, assigned to 'baseClass' and is not an abstract or anonymous
     * class.
     * 
     * @param result
     *            The collection to add to
     * @param fullyQualifiedClassName
     *            The candidate class
     * @param baseClass
     *            The class 'fullyQualifiedClassName' should be assignable to
     */
    @SuppressWarnings("unchecked")
    private static <T> void addClassIfMatches(
            Collection<Class<? extends T>> result,
            String fullyQualifiedClassName, Class<T> baseClass) {
        try {
            // Try to load the class

            Class<?> c = Class.forName(fullyQualifiedClassName);
            if (!baseClass.isAssignableFrom(c)) {
                return;
            }
            if (!includeInSuite(c)) {
                return;
            }

            if (!Modifier.isAbstract(c.getModifiers()) && !c.isAnonymousClass()) {
                result.add((Class<? extends T>) c);
            }
        } catch (Exception e) {
            // Could ignore that class cannot be loaded
            e.printStackTrace();
        } catch (LinkageError e) {
            // Ignore. Client side classes will at least throw LinkageErrors
        }

    }

    /**
     * @return true if the class should be included in the suite, false if not
     */
    private static boolean includeInSuite(Class<?> c) {
        if (c.getAnnotation(ExcludeFromSuite.class) != null) {
            return false;
        }

        return true;
    }
}
