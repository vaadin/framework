/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.ui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TextAreaTest {
    @Test
    public void initiallyEmpty() {
        TextArea textArea = new TextArea();
        assertTrue(textArea.isEmpty());
    }

    @Test
    public void emptyAfterClear() {
        TextArea textArea = new TextArea();
        textArea.setValue("foobar");
        assertFalse(textArea.isEmpty());
        textArea.clear();
        assertTrue(textArea.isEmpty());
    }

}
