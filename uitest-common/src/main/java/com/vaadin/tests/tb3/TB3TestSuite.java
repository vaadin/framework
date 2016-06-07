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

import java.io.IOException;

import org.junit.runners.model.InitializationError;

import com.vaadin.testbench.parallel.ParallelTestSuite;

/**
 * Test suite which consists of all the TB3 tests passed in the constructor.
 * Runs the tests in parallel using a {@link ParallelScheduler}
 * 
 * @author Vaadin Ltd
 */
public class TB3TestSuite extends ParallelTestSuite {

    public TB3TestSuite(Class<?> klass,
            Class<? extends AbstractTB3Test> baseClass, String basePackage,
            String[] ignorePackages) throws InitializationError, IOException {
        this(klass, baseClass, basePackage, ignorePackages,
                new TB3TestLocator());
    }

    public TB3TestSuite(Class<?> klass,
            Class<? extends AbstractTB3Test> baseClass, String basePackage,
            String[] ignorePackages, TB3TestLocator locator)
            throws InitializationError, IOException {
        super(klass, locator
                .findClasses(baseClass, basePackage, ignorePackages).toArray(
                        new Class<?>[] {}));
    }
}
