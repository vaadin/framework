package com.vaadin.tests.components.datefield;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.InlineDateField;

public class DateFieldPrimaryStyleNames extends TestBase {

    @Override
    protected void setup() {
        final DateField df = new DateField();
        df.setPrimaryStyleName("my-datefield");
        addComponent(df);

        final InlineDateField idf = new InlineDateField();
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
