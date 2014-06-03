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

import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;

import com.vaadin.tests.tb3.AllTB3Tests.AllTB3TestsSuite;

/**
 * Test consisting of all TB3 tests except integration tests (classes extending
 * AbstractTB3Test, excludes package com.vaadin.test.integration).
 * 
 * @author Vaadin Ltd
 */
@RunWith(AllTB3TestsSuite.class)
public class AllTB3Tests {

    public static class AllTB3TestsSuite extends TB3TestSuite {

        public AllTB3TestsSuite(Class<?> klass) throws InitializationError {
            super(klass, AbstractTB3Test.class, "com.vaadin.tests",
                    new String[] { "com.vaadin.tests.integration" });
        }

    }

}
