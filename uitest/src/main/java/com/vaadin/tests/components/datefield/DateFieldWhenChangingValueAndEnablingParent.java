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
package com.vaadin.tests.components.datefield;

import java.time.LocalDate;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.components.TestDateField;
import com.vaadin.ui.AbstractLocalDateField;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.VerticalLayout;

public class DateFieldWhenChangingValueAndEnablingParent
        extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout main = new VerticalLayout();
        final VerticalLayout sub = new VerticalLayout();
        final CheckBox chk = new CheckBox("Parent layout enabled");

        main.setMargin(true);
        setContent(main);

        final AbstractLocalDateField df1 = createDateField(true);
        final AbstractLocalDateField df2 = createDateField(false);
        final DateField pdf1 = createPopupDateField(true, true);
        final DateField pdf2 = createPopupDateField(true, false);
        final DateField pdf3 = createPopupDateField(false, true);
        final DateField pdf4 = createPopupDateField(false, false);

        sub.addComponent(df1);
        sub.addComponent(df2);
        sub.addComponent(pdf1);
        sub.addComponent(pdf2);
        sub.addComponent(pdf3);
        sub.addComponent(pdf4);
        sub.setEnabled(false);
        main.addComponent(chk);
        main.addComponent(sub);

        chk.addValueChangeListener(event -> {
            df1.setValue(LocalDate.now());
            df2.setValue(LocalDate.now());
            pdf1.setValue(LocalDate.now());
            pdf2.setValue(LocalDate.now());
            pdf3.setValue(LocalDate.now());
            pdf4.setValue(LocalDate.now());
            sub.setEnabled(chk.getValue());
        });
    }

    private AbstractLocalDateField createDateField(boolean enabled) {
        AbstractLocalDateField df = new TestDateField(
                "DateField, " + (enabled ? "enabled" : "disabled"));
        df.setEnabled(enabled);
        df.setId("DATEFIELD_" + (enabled ? "ENABLED" : "DISABLED"));
        return df;
    }

    private DateField createPopupDateField(boolean enabled,
            boolean textInputEnabled) {
        DateField df = new DateField("PopupDateField, "
                + (enabled ? "enabled" : "disabled") + ", text input "
                + (textInputEnabled ? "enabled" : "disabled"));
        df.setEnabled(enabled);
        df.setTextFieldEnabled(textInputEnabled);
        df.setId("DATEFIELD_" + (enabled ? "ENABLED" : "DISABLED") + "_"
                + (textInputEnabled ? "ENABLED" : "DISABLED"));
        return df;
    }
}
