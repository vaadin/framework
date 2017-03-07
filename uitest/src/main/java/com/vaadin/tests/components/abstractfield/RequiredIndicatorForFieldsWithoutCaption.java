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
package com.vaadin.tests.components.abstractfield;

import java.util.HashSet;
import java.util.Set;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

public class RequiredIndicatorForFieldsWithoutCaption
        extends AbstractReindeerTestUI {
    private Set<Field> fields = new HashSet<>();

    @Override
    protected void setup(VaadinRequest request) {

        CheckBox required = new CheckBox("Fields required", true);
        required.addValueChangeListener(event -> {
            boolean newRequired = event.getValue();
            for (Field f : fields) {
                f.setRequired(newRequired);
            }
        });
        addComponent(required);
        addComponent(createWrappedTextField(new GridLayout(2, 1)));
        addComponent(createWrappedTextField(new VerticalLayout()));
        addComponent(createWrappedTextField(new HorizontalLayout()));
        AbsoluteLayout al = new AbsoluteLayout();
        al.setWidth("400px");
        al.setHeight("100px");
        addComponent(createWrappedTextField(al));
        addComponent(createWrappedTextField(new CssLayout()));
    }

    /**
     * @since
     * @param gridLayout
     * @return
     */
    private Component createWrappedTextField(ComponentContainer container) {
        TextField tf = new TextField();
        tf.setRequired(true);
        tf.setWidth(200, Unit.PIXELS);
        fields.add(tf);
        container.addComponent(new Label(container.getClass().getSimpleName()));
        container.addComponent(tf);
        if (container instanceof AbsoluteLayout) {
            ((AbsoluteLayout) container).getPosition(tf).setLeft(100.0f,
                    Unit.PIXELS);
            ((AbsoluteLayout) container).getPosition(tf).setTop(50.0f,
                    Unit.PIXELS);
        }

        return container;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Test for ensuring that the required indicator is visible for fields even when they would not otherwise have a caption";
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 12077;
    }
}
