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

/**
 * 
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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.jar.JarEntry;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerScheduler;

import com.vaadin.tests.tb3.AllTB3Tests.TB3TestFinder;

/**
 * 
 * @since
 * @author Vaadin Ltd
 */
@RunWith(TB3TestFinder.class)
public class AllTB3Tests {

    public static class ParallelScheduler implements RunnerScheduler {
        private final List<Future<Object>> fResults = new ArrayList<Future<Object>>();

        private final ExecutorService fService = Executors
                .newCachedThreadPool();

        @Override
        public void schedule(final Runnable childStatement) {
            fResults.add(fService.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    childStatement.run();
                    return null;
                }
            }));
        }

        @Override
        public void finished() {
            for (Future<Object> each : fResults) {
                try {
                    each.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class TB3TestFinder extends Suite {

        /**
         * @param klass
         * @param suiteClasses
         * @throws InitializationError
         */
        public TB3TestFinder(Class<?> klass) throws InitializationError {
            super(klass, getAllTB3Tests());
            setScheduler(new ParallelScheduler());
        }

        /**
         * @since
         * @return
         */
        private static Class<?>[] getAllTB3Tests() {
            try {
                List<Class<? extends AbstractTB3Test>> l = findClasses(
                        AbstractTB3Test.class, "com.vaadin",
                        new String[] { "com.vaadin.tests.integration" });
                return l.toArray(new Class[] {});
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        private static <T> List<Class<? extends T>> findClasses(
                Class<T> baseClass, String basePackage, String[] ignoredPackages)
                throws IOException {
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
                JarURLConnection juc = (JarURLConnection) location
                        .openConnection();
                findPackages(juc, basePackage, baseClass, classes);
            }

            Collections.sort(classes, new Comparator<Class<? extends T>>() {

                @Override
                public int compare(Class<? extends T> o1, Class<? extends T> o2) {
                    return o1.getName().compareTo(o2.getName());
                }

            });
            return classes;
        }

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
                    addClassIfMatches(result, fullyQualifiedClassName,
                            baseClass);
                }
            }

        }

        private static <T> void findPackages(JarURLConnection juc,
                String javaPackage, Class<T> baseClass,
                Collection<Class<? extends T>> result) throws IOException {
            String prefix = "com/vaadin/ui";
            Enumeration<JarEntry> ent = juc.getJarFile().entries();
            while (ent.hasMoreElements()) {
                JarEntry e = ent.nextElement();
                if (e.getName().endsWith(".class")
                        && e.getName().startsWith(prefix)) {
                    String fullyQualifiedClassName = e.getName()
                            .replace('/', '.').replace(".class", "");
                    addClassIfMatches(result, fullyQualifiedClassName,
                            baseClass);
                }
            }
        }

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
                if (!Modifier.isAbstract(c.getModifiers())
                        && !c.isAnonymousClass()) {
                    result.add((Class<? extends T>) c);
                }
            } catch (Exception e) {
                // Could ignore that class cannot be loaded
                e.printStackTrace();
            } catch (LinkageError e) {
                // Ignore. Client side classes will at least throw LinkageErrors
            }

        }

    }

}
