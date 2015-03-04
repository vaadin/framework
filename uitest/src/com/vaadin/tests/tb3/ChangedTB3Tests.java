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

import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;

import com.vaadin.tests.tb3.ChangedTB3Tests.ChangedTB3TestsSuite;

/**
 * Test suite that runs tests from test classes which have changes in the
 * current workspace. If there are no changes in the workspace, it will run the
 * changes to test classes introduced in the HEAD commit.
 * 
 * @since
 * @author Vaadin Ltd
 */
@RunWith(ChangedTB3TestsSuite.class)
public class ChangedTB3Tests {
    public static class ChangedTB3TestsSuite extends TB3TestSuite {
        public ChangedTB3TestsSuite(Class<?> klass) throws InitializationError,
                IOException {
            super(klass, AbstractTB3Test.class, "com.vaadin.tests",
                    new String[] { "com.vaadin.tests.integration" },
                    new ChangedTB3TestLocator());

        }
    }
}
