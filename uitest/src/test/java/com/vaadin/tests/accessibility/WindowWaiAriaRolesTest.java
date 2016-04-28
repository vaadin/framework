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
package com.vaadin.tests.accessibility;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test to see if regular and alert windows get the correct wai-aria roles
 *
 * @author Vaadin Ltd
 */
public class WindowWaiAriaRolesTest extends MultiBrowserTest {

    @Test
    public void testRegularWindowRole() {
        openTestURL();

        $(ButtonElement.class).caption("Regular").first().click();
        String role = getWindowRole();
        Assert.assertTrue("Dialog has incorrect role '" + role
                + "', expected 'dialog'", "dialog".equals(role));
    }

    @Test
    public void testAlertWindowRole() {
        openTestURL();
        $(ButtonElement.class).caption("Alert").first().click();
        String role = getWindowRole();
        Assert.assertTrue("Dialog has incorrect role '" + role
                + "', expected 'alertdialog'", "alertdialog".equals(role));
    }

    public String getWindowRole() {
        return $(WindowElement.class).first().getAttribute("role");
    }
}
