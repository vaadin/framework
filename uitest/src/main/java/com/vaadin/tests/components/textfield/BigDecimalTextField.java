package com.vaadin.tests.components.textfield;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Locale;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.ui.TextField;

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
        BeanItem<BeanBigDecimal> beanItem = new BeanItem<>(beanBigDecimal);

        FormLayout formLayout = new FormLayout();
        TextField textField = new TextField("BigDecimal field");
        textField.setImmediate(true);
        textField.setValue("12");
        formLayout.addComponent(textField);

        final FieldGroup fieldGroup = new FieldGroup(beanItem);
        fieldGroup.bind(textField, "decimal");

        Button setValue = new Button("Set value to 15,2",
                event -> ((TextField) fieldGroup.getField("decimal"))
                        .setValue("15,2"));

        Button button = new Button("Commit");
        button.addClickListener(event -> {
            try {
                fieldGroup.commit();
                log("Commit ok. Property value: "
                        + fieldGroup.getItemDataSource()
                                .getItemProperty("decimal").getValue());
            } catch (FieldGroup.CommitException e) {
                log("Commit failed: " + e.getMessage());
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
