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
package com.vaadin.tests.components.textfield;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Locale;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * @since 7.2
 * @author Vaadin Ltd
 */
public class BigDecimalTextField extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setLocale(new Locale("fi", "FI"));

        BeanBigDecimal beanBigDecimal = new BeanBigDecimal();
        BeanItem<BeanBigDecimal> beanItem = new BeanItem<BeanBigDecimal>(
                beanBigDecimal);

        FormLayout formLayout = new FormLayout();
        TextField textField = new TextField("BigDecimal field");
        textField.setImmediate(true);
        textField.setValue("12");
        formLayout.addComponent(textField);

        final FieldGroup fieldGroup = new FieldGroup(beanItem);
        fieldGroup.bind(textField, "decimal");

        Button setValue = new Button("Set value to 15,2", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                ((TextField) fieldGroup.getField("decimal")).setValue("15,2");
            }
        });

        Button button = new Button("Commit");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    fieldGroup.commit();
                    log("Commit ok. Property value: "
                            + fieldGroup.getItemDataSource()
                                    .getItemProperty("decimal").getValue());
                } catch (FieldGroup.CommitException e) {
                    log("Commit failed: " + e.getMessage());
                }
            }
        });

        layout.addComponent(formLayout);
        layout.addComponent(setValue);
        layout.addComponent(button);

        setContent(layout);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Tests that BigDecimals work correctly with TextFields";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 9997;
    }

    public static class BeanBigDecimal implements Serializable {
        BigDecimal decimal;

        public BeanBigDecimal() {

        }

        public BigDecimal getDecimal() {
            return decimal;
        }

        public void setDecimal(BigDecimal decimal) {
            this.decimal = decimal;
        }
    }

}
