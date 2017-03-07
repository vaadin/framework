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
package com.vaadin.tests.components.uitest.components;

import com.vaadin.tests.components.uitest.TestSampler;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextField;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.themes.ChameleonTheme;
import com.vaadin.v7.ui.themes.Reindeer;

public class TextFieldsCssTest extends GridLayout {

    private TestSampler parent;
    private int debugIdCounter = 0;

    public TextFieldsCssTest(TestSampler parent) {
        this.parent = parent;
        setSpacing(true);
        setColumns(7);
        setRows(2);

        setWidth("100%");

        createTextFieldWith(null, null, null);
        createTextFieldWith("Input prompt", null, "Input prompt");
        createTextFieldWith("Small", Reindeer.TEXTFIELD_SMALL, null);
        createTextFieldWith("Big", ChameleonTheme.TEXTFIELD_BIG, null);
        createTextFieldWith("Search", ChameleonTheme.TEXTFIELD_SEARCH, null);

        TextArea ta = new TextArea();
        ta.setId("textfield" + debugIdCounter++);
        addComponent(ta);

        PasswordField pf = new PasswordField();
        pf.setId("textfield" + debugIdCounter++);
        addComponent(pf);

        RichTextArea rta = new RichTextArea();
        rta.setId("textfield" + debugIdCounter++);
        addComponent(rta, 0, 1, 6, 1);

    }

    private void createTextFieldWith(String caption, String primaryStyleName,
            String inputPrompt) {
        TextField tf = new TextField();
        tf.setId("textfield" + debugIdCounter++);
        if (caption != null) {
            tf.setCaption(caption);
        }

        if (primaryStyleName != null) {
            tf.addStyleName(primaryStyleName);
        }

        if (inputPrompt != null) {
            tf.setPlaceholder(inputPrompt);
        }

        addComponent(tf);

    }

    @Override
    public void addComponent(Component c) {
        parent.registerComponent(c);
        super.addComponent(c);
    }

    @Override
    public void addComponent(Component component, int column1, int row1,
            int column2, int row2)
            throws OverlapsException, OutOfBoundsException {

        parent.registerComponent(component);
        super.addComponent(component, column1, row1, column2, row2);
    }
}
