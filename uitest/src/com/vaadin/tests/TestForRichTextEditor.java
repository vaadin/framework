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

package com.vaadin.tests;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;

/**
 * 
 * @author Vaadin Ltd.
 */
public class TestForRichTextEditor extends CustomComponent implements
        ValueChangeListener {

    private final VerticalLayout main = new VerticalLayout();

    private Label l;

    private RichTextArea rte;

    public TestForRichTextEditor() {

        setCompositionRoot(main);
        createNewView();
    }

    public void createNewView() {
        main.removeAllComponents();
        main.addComponent(new Label(
                "RTE uses google richtextArea and their examples toolbar."));

        rte = new RichTextArea();
        rte.addListener(this);

        main.addComponent(rte);

        main.addComponent(new Button("commit content to label below"));

        l = new Label("", ContentMode.HTML);
        main.addComponent(l);

        CheckBox b = new CheckBox("enabled");
        b.setImmediate(true);
        b.addListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                rte.setEnabled(!rte.isEnabled());
            }
        });
        main.addComponent(b);

    }

    @Override
    public void valueChange(ValueChangeEvent event) {
        l.setValue(rte.getValue());
    }

}
