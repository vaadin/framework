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

package com.vaadin.tests.integration;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import com.vaadin.tests.tb3.TB3Runner;

/**
 * JUnit runner for integration tests. Replaces the actual method name with the
 * server-name property when generating the test name.
 * 
 * @author Vaadin Ltd
 */
public class IntegrationTestRunner extends TB3Runner {

    private Class<?> testClass;

    public IntegrationTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
        testClass = klass;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.junit.runners.BlockJUnit4ClassRunner#testName(org.junit.runners.model
     * .FrameworkMethod)
     */
    @Override
    protected String testName(FrameworkMethod method) {
        if (AbstractIntegrationTest.class.isAssignableFrom(testClass)) {
            return System.getProperty("server-name");
        } else {
            return super.testName(method);
        }
    }
}
