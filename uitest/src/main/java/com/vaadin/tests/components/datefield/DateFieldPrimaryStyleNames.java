package com.vaadin.tests.components.datefield;

import com.vaadin.legacy.ui.LegacyDateField;
import com.vaadin.legacy.ui.LegacyInlineDateField;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

public class DateFieldPrimaryStyleNames extends TestBase {

    @Override
    protected void setup() {
        final LegacyDateField df = new LegacyDateField();
        df.setPrimaryStyleName("my-datefield");
        addComponent(df);

        final LegacyInlineDateField idf = new LegacyInlineDateField();
        idf.setPrimaryStyleName("my-inline-datefield");
        addComponent(idf);

        addComponent(new Button("Set primary stylename",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        df.setPrimaryStyleName("my-second-datefield");
                        idf.setPrimaryStyleName("my-second-inline-datefield");
                    }
                }));

    }

    @Override
    protected String getDescription() {
        return "Datefield should work with primary stylenames both initially and dynamically";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9903;
    }

}
