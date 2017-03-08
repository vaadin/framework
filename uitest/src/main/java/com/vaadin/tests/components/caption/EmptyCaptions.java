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
package com.vaadin.tests.components.caption;

import com.vaadin.server.UserError;
import com.vaadin.tests.components.TestBase;
import com.vaadin.v7.ui.TextField;

public class EmptyCaptions extends TestBase {

    @Override
    protected void setup() {
        TextField tf;

        tf = new TextField(null, "Null caption");
        addComponent(tf);

        tf = new TextField("", "Empty caption");
        addComponent(tf);

        tf = new TextField(" ", "Space as caption");
        addComponent(tf);

        tf = new TextField(null, "Null caption, required");
        tf.setRequired(true);
        addComponent(tf);
        tf = new TextField("", "Empty caption, required");
        tf.setRequired(true);
        addComponent(tf);
        tf = new TextField(" ", "Space as caption, required");
        tf.setRequired(true);
        addComponent(tf);

        tf = new TextField(null, "Null caption, error");
        tf.setComponentError(new UserError("error"));
        addComponent(tf);

        tf = new TextField("", "Empty caption, error");
        tf.setComponentError(new UserError("error"));
        addComponent(tf);

        tf = new TextField(" ", "Space as caption, error");
        tf.setComponentError(new UserError("error"));
        addComponent(tf);

    }

    @Override
    protected String getDescription() {
        return "Null caption should never use space while a non-null caption always should use space.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3846;
    }

}
