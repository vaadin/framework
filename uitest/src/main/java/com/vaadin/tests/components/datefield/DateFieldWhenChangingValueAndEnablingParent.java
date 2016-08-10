package com.vaadin.tests.components.datefield;

import java.util.Date;

import com.vaadin.legacy.ui.LegacyDateField;
import com.vaadin.legacy.ui.LegacyPopupDateField;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.VerticalLayout;

public class DateFieldWhenChangingValueAndEnablingParent
        extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout main = new VerticalLayout();
        final VerticalLayout sub = new VerticalLayout();
        final CheckBox chk = new CheckBox("Parent layout enabled");

        main.setMargin(true);
        setContent(main);

        final LegacyDateField df1 = createDateField(true);
        final LegacyDateField df2 = createDateField(false);
        final LegacyPopupDateField pdf1 = createPopupDateField(true, true);
        final LegacyPopupDateField pdf2 = createPopupDateField(true, false);
        final LegacyPopupDateField pdf3 = createPopupDateField(false, true);
        final LegacyPopupDateField pdf4 = createPopupDateField(false, false);

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
            df1.setValue(new Date());
            df2.setValue(new Date());
            pdf1.setValue(new Date());
            pdf2.setValue(new Date());
            pdf3.setValue(new Date());
            pdf4.setValue(new Date());
            sub.setEnabled(chk.getValue());
        });
    }

    private LegacyDateField createDateField(boolean enabled) {
        LegacyDateField df = new LegacyDateField(
                "DateField, " + (enabled ? "enabled" : "disabled"));
        df.setEnabled(enabled);
        df.setId("DATEFIELD_" + (enabled ? "ENABLED" : "DISABLED"));
        return df;
    }

    private LegacyPopupDateField createPopupDateField(boolean enabled,
            boolean textInputEnabled) {
        LegacyPopupDateField df = new LegacyPopupDateField("PopupDateField, "
                + (enabled ? "enabled" : "disabled") + ", text input "
                + (textInputEnabled ? "enabled" : "disabled"));
        df.setEnabled(enabled);
        df.setTextFieldEnabled(textInputEnabled);
        df.setId("DATEFIELD_" + (enabled ? "ENABLED" : "DISABLED") + "_"
                + (textInputEnabled ? "ENABLED" : "DISABLED"));
        return df;
    }
}
