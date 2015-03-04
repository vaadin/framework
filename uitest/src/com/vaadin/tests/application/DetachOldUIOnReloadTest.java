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
package com.vaadin.tests.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DetachOldUIOnReloadTest extends MultiBrowserTest {

    private static final String RELOAD = "Reload page";
    private static final String READ_LOG = "Read log messages from session";

    @Test
    public void testDetachesUIOnReload() throws InterruptedException {
        openTestURL();
        List<LabelElement> labels = $(LabelElement.class).all();
        assertEquals("initial label incorrect", "This is UI 0",
                lastLabelText(labels));

        assertFalse("reloading button not found", $(ButtonElement.class)
                .caption(RELOAD).all().isEmpty());

        openTestURL();
        click(READ_LOG);

        checkLabels("first", 1);

        click(RELOAD);
        click(READ_LOG);

        checkLabels("second", 2);

        openTestURL();
        click(READ_LOG);

        checkLabels("third", 3);

        // restarting reverts to 0
        openTestURL("restartApplication");

        checkLabels("final", 0);
    }

    private void checkLabels(String descriptor, int index) {
        List<LabelElement> labels = $(LabelElement.class).all();
        assertEquals(
                String.format("label incorrect after %s reload", descriptor),
                String.format("This is UI %s", index), lastLabelText(labels));
        if (!"final".equals(descriptor)) {
            assertEquals(String.format("log message incorrect after %s reload",
                    descriptor), String.format("%s. UI %s has been detached",
                    index, index - 1), labels.get(0).getText());
        }
    }

    private String lastLabelText(List<LabelElement> labels) {
        return labels.get(labels.size() - 1).getText();
    }

    private void click(String caption) {
        $(ButtonElement.class).caption(caption).first().click();
    }

}
