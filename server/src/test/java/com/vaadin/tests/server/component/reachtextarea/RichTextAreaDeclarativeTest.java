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
package com.vaadin.tests.server.component.reachtextarea;

import java.util.Locale;

import org.junit.Test;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.tests.server.component.abstractfield.AbstractFieldDeclarativeTest;
import com.vaadin.ui.RichTextArea;

public class RichTextAreaDeclarativeTest
        extends AbstractFieldDeclarativeTest<RichTextArea, String> {

    @Override
    public void valueDeserialization()
            throws InstantiationException, IllegalAccessException {
        String value = "<b>Header</b> \n<br>Some text";
        String design = String.format("<%s>\n      %s\n      </%s>",
                getComponentTag(), value, getComponentTag());

        RichTextArea component = new RichTextArea();
        component.setValue(value);

        testRead(design, component);
        testWrite(design, component);
    }

    @Override
    public void readOnlyValue()
            throws InstantiationException, IllegalAccessException {
        String value = "<b>Header</b> \n<br>Some text";
        String design = String.format("<%s readonly>\n      %s\n      </%s>",
                getComponentTag(), value, getComponentTag());

        RichTextArea component = new RichTextArea();
        component.setValue(value);
        component.setReadOnly(true);

        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void remainingAttributeDeserialization() {
        ValueChangeMode mode = ValueChangeMode.TIMEOUT;
        int timeout = 67;
        String design = String.format(
                "<%s value-change-mode='%s' value-change-timeout='%d'/>",
                getComponentTag(), mode.name().toLowerCase(Locale.ROOT),
                timeout);

        RichTextArea component = new RichTextArea();
        component.setValueChangeMode(mode);
        component.setValueChangeTimeout(timeout);

        testRead(design, component);
        testWrite(design, component);
    }

    @Override
    protected String getComponentTag() {
        return "vaadin-rich-text-area";
    }

    @Override
    protected Class<RichTextArea> getComponentClass() {
        return RichTextArea.class;
    }

}
