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
package com.vaadin.tests.push;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class PushWithPreserveOnRefreshTest extends MultiBrowserTest {

    @Test
    public void ensurePushWorksAfterRefresh() {
        openTestURL();
        $(ButtonElement.class).first().click();
        $(ButtonElement.class).first().click();
        Assert.assertEquals("2. Button has been clicked 2 times", getLogRow(0));
        openTestURL();
        Assert.assertEquals("2. Button has been clicked 2 times", getLogRow(0));
        $(ButtonElement.class).first().click();
        Assert.assertEquals("3. Button has been clicked 3 times", getLogRow(0));

    }
}
