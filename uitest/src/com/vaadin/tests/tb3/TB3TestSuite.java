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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        this(klass, baseClass, basePackage, ignorePackages,
                new TB3TestLocator());
    }

    public TB3TestSuite(Class<?> klass,
            Class<? extends AbstractTB3Test> baseClass, String basePackage,
            String[] ignorePackages, TB3TestLocator testLocator)
            throws InitializationError {
        super(klass, testLocator.findTests(baseClass, basePackage,
                ignorePackages));
        setScheduler(new ParallelScheduler(service));
    }
}
