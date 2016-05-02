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
package com.vaadin.tests.components.window;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Dimension;

import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.tb3.newelements.WindowElement;

public class MaximizeRestoreWindowWithManagedLayoutTest extends
        MultiBrowserTest {

    // This tests a timing issue so if this test fails randomly, it is
    // likely that something is broken
    @Test
    public void contentSizeCorrectAfterMaximizeRestore()
            throws InterruptedException {
        openTestURL();
        WindowElement window = $(WindowElement.class).first();
        TextFieldElement field = $(TextFieldElement.class).first();

        // Sleeps are here as there is no server request while resizing the
        // window so we must wait until the animation is done before measuring
        // and comparing
        assertSameWidth(window, field);
        window.maximize();
        sleep(200);
        assertSameWidth(window, field);
        window.restore();
        sleep(200);
        assertSameWidth(window, field);
        window.maximize();
        sleep(200);
        assertSameWidth(window, field);

    }

    private void assertSameWidth(WindowElement window, TextFieldElement field) {
        Dimension windowSize = window.getSize();
        Dimension fieldSize = field.getSize();
        Assert.assertEquals(windowSize.getWidth(), fieldSize.getWidth());
    }
}
