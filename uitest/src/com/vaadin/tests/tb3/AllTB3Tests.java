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

import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;

import com.vaadin.tests.tb3.AllTB3Tests.AllTestsFinder;

/**
 * 
 * @since
 * @author Vaadin Ltd
 */
@RunWith(AllTestsFinder.class)
public class AllTB3Tests {

    public static class AllTestsFinder extends TB3TestFinder {

        /**
         * @param klass
         * @throws InitializationError
         */
        public AllTestsFinder(Class<?> klass) throws InitializationError {
            super(klass, AbstractTB3Test.class, "com.vaadin.tests",
                    new String[] { "com.vaadin.tests.integration" });
        }

    }

}
