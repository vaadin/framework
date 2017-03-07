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
package com.vaadin.tests.components.textfield;

import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.TextField;

public class TextFields extends ComponentTestCase<TextField> {

    @Override
    protected Class<TextField> getTestClass() {
        return TextField.class;
    }

    @Override
    protected void initializeComponents() {
        TextField tf;

        tf = createTextField("TextField 100% wide");
        tf.setWidth("100%");
        addTestComponent(tf);

        tf = createTextField(null, "TextField 100% wide, no caption");
        tf.setWidth("100%");
        addTestComponent(tf);

        tf = createTextField("TextField auto wide");
        addTestComponent(tf);

        tf = createTextField("TextField with input prompt");
        tf.setPlaceholder("Please enter a value");
        addTestComponent(tf);

        tf = createTextField("100px wide textfield");
        tf.setWidth("100px");
        addTestComponent(tf);

        tf = createTextField("150px wide, 120px high textfield");
        tf.setWidth("150px");
        tf.setHeight("120px");
        addTestComponent(tf);

        tf = createTextField("50px high textfield");
        tf.setHeight("50px");
        addTestComponent(tf);

        tf = createTextField(null, "No caption");
        addTestComponent(tf);

        tf = createTextField(null, "No caption and input prompt");
        tf.setPlaceholder("Enter a value");
        addTestComponent(tf);

    }

    private TextField createTextField(String caption, String value) {
        TextField tf = new TextField(caption);
        tf.setValue(value);

        return tf;
    }

    private TextField createTextField(String caption) {
        return createTextField(caption, "");
    }

}
