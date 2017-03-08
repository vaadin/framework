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
package com.vaadin.tests.components.label;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.Label;

public class LabelModes extends ComponentTestCase<Label> {

    @Override
    protected Class<Label> getTestClass() {
        return Label.class;
    }

    @Override
    protected void initializeComponents() {

        Label l;
        l = createLabel(
                "This is an undefined wide label with default content mode");
        l.setWidth(null);
        addTestComponent(l);

        l = createLabel(
                "This label                       contains\nnewlines and spaces\nbut is in\ndefault content mode");
        l.setWidth(null);
        addTestComponent(l);

        l = createLabel(
                "This label                       contains\nnewlines and spaces\nand is in\npreformatted mode");
        l.setContentMode(ContentMode.PREFORMATTED);
        l.setWidth(null);
        addTestComponent(l);

        l = createLabel(
                "This label                       contains\nnewlines and spaces\nand is in\nhtml mode");
        l.setContentMode(ContentMode.HTML);
        l.setWidth(null);
        addTestComponent(l);

    }

    private Label createLabel(String text, String caption) {
        Label l = new Label(text);
        l.setCaption(caption);

        return l;
    }

    private Label createLabel(String text) {
        return createLabel(text, null);
    }

    @Override
    protected String getTestDescription() {
        return "A generic test for Labels in different configurations";
    }

}
