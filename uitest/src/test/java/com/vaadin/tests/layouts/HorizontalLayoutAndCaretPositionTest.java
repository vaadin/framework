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
package com.vaadin.tests.layouts;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class HorizontalLayoutAndCaretPositionTest extends MultiBrowserTest {

    @Test
    public void testCaretPositionOnClck() {
        openTestURL();
        TextFieldElement first = $(TextFieldElement.class).first();
        // type in some text to the first field
        first.click();
        first.sendKeys("test");
        // make sure that the field could be focused and text typed
        Assert.assertEquals("Field must be focused on click", "test",
                first.getValue());
        // now move the focus to the next text field
        $(TextFieldElement.class).get(1).click();
        // and back to the first one
        first.click(30, 10);
        first.sendKeys("do_not_put_in_beginning_");
        Assert.assertNotEquals("The caret position must be maintained",
                "do_not_put_in_beginning_test", first.getValue());
    }

}
