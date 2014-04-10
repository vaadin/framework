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

package com.vaadin.tests.components.ui;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class UIReinitTest extends MultiBrowserTest {

    @Test
    public void testUIReinit() {
        openTestURL();
        Assert.assertFalse(reinitLabelExists());
        // Reload the page; UI.reinit should be invoked
        openTestURL();
        Assert.assertTrue(reinitLabelExists());
    }

    private boolean reinitLabelExists() {
        return !getDriver().findElements(By.id(UIReinit.REINIT_ID)).isEmpty();
    }
}
