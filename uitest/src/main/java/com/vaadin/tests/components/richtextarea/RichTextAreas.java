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
package com.vaadin.tests.components.richtextarea;

import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.RichTextArea;

public class RichTextAreas extends ComponentTestCase<RichTextArea> {

    @Override
    protected Class<RichTextArea> getTestClass() {
        return RichTextArea.class;
    }

    @Override
    protected void initializeComponents() {
        RichTextArea rta;

        rta = createRichTextArea("TextField 100% wide, 100px high");
        rta.setWidth("100%");
        rta.setHeight("100px");
        addTestComponent(rta);

        rta = createRichTextArea("TextField auto width, auto height");
        addTestComponent(rta);

        rta = createRichTextArea(null, "500px wide, 120px high textfield");
        rta.setWidth("500px");
        rta.setHeight("120px");
        addTestComponent(rta);

    }

    private RichTextArea createRichTextArea(String caption, String value) {
        return new RichTextArea(caption, value);
    }

    private RichTextArea createRichTextArea(String caption) {
        return new RichTextArea(caption);
    }

}
