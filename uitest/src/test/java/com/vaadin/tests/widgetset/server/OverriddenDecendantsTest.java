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
package com.vaadin.tests.widgetset.server;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Class for unit testing that @DelegateToWidget works on derived widget states.
 *
 * @since
 * @author Vaadin Ltd
 */
public class OverriddenDecendantsTest extends MultiBrowserTest {

    @Test
    public void allExtendingFieldsShouldGetRowsFromTextAreaStateAnnotation()
            throws InterruptedException {
        openTestURL();

        List<TextAreaElement> textAreas = $(TextAreaElement.class).all();

        assertEquals("Did not contain all 3 text areas", 3, textAreas.size());

        for (TextAreaElement area : textAreas) {
            assertEquals("Text area was missing rows", "10",
                    area.getAttribute("rows"));
        }

    }
}
