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
package com.vaadin.tests.elements.abstracttextfield;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.v7.ui.AbstractField;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Form;
import com.vaadin.v7.ui.ListSelect;
import com.vaadin.v7.ui.NativeSelect;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.PasswordField;
import com.vaadin.v7.ui.ProgressBar;
import com.vaadin.v7.ui.RichTextArea;
import com.vaadin.v7.ui.Slider;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.Tree;
import com.vaadin.v7.ui.TwinColSelect;

public class CompatibilityAbstractFieldElementSetValueReadOnly
        extends AbstractTestUI {

    AbstractField<?>[] elems = { new ComboBox(), new ListSelect(),
            new NativeSelect(), new OptionGroup(), new Table(), new Tree(),
            new TwinColSelect(), new TextArea(), new TextField(),
            new DateField(), new PasswordField(), new CheckBox(), new Form(),
            new ProgressBar(), new RichTextArea(), new Slider() };

    @Override
    protected void setup(VaadinRequest request) {
        for (int i = 0; i < elems.length; i++) {
            elems[i].setReadOnly(true);
            addComponent(elems[i]);
        }
    }

    @Override
    protected String getTestDescription() {
        return "When vaadin element is set ReadOnly, setValue() method should raise an exception";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14068;
    }

}
