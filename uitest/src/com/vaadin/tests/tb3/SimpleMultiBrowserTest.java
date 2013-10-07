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

import org.junit.Test;

/**
 * A simple version of {@link MultiBrowserTest} which allows only one test
 * method ({@link #test()}). Uses only the enclosing class name as test
 * identifier (i.e. excludes "-test").
 * 
 * This class is only provided as a helper for converting existing TB2 tests
 * without renaming all screenshots. All new TB3+ tests should extend
 * {@link MultiBrowserTest} directly instead of this.
 * 
 * @author Vaadin Ltd
 */
@Deprecated
public abstract class SimpleMultiBrowserTest extends MultiBrowserTest {

    @Test
    public abstract void test() throws Exception;

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.tb3.ScreenshotTB3Test#getScreenshotBaseName()
     */
    @Override
    public String getScreenshotBaseName() {
        return super.getScreenshotBaseName().replaceFirst("-test$", "");
    }
}
