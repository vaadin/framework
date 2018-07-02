package com.vaadin.tests.fieldgroup;

import java.util.Date;
import java.util.Locale;

import com.vaadin.annotations.PropertyId;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.InlineDateField;
import com.vaadin.v7.ui.PopupDateField;
import com.vaadin.v7.ui.TextField;

public class DateForm extends AbstractTestUIWithLog {

    static final Date DATE = new Date(443457289789L);
    @PropertyId("date1")
    private DateField dateField;
    @PropertyId("date2")
    private PopupDateField popupDateField;
    @PropertyId("date3")
    private InlineDateField inlineDateField;
    @PropertyId("date4")
    private TextField textField;

    public static class DateObject {
        private Date date1, date2, date3, date4;

        public DateObject(Date date1, Date date2, Date date3, Date date4) {
            super();
            this.date1 = date1;
            this.date2 = date2;
            this.date3 = date3;
            this.date4 = date4;
        }

        public Date getDate1() {
            return date1;
        }

        public void setDate1(Date date1) {
            this.date1 = date1;
        }

        public Date getDate2() {
            return date2;
        }

        public void setDate2(Date date2) {
            this.date2 = date2;
        }

        public Date getDate3() {
            return date3;
        }

        public void setDate3(Date date3) {
            this.date3 = date3;
        }

        public Date getDate4() {
            return date4;
        }

        public void setDate4(Date date4) {
            this.date4 = date4;
        }

    }

    @Override
    protected void setup(VaadinRequest request) {
        setLocale(Locale.US);
        addComponent(log);
        final FieldGroup fieldGroup = new BeanFieldGroup<>(DateObject.class);
        fieldGroup.setBuffered(true);

        fieldGroup.buildAndBindMemberFields(this);
        textField.setWidth("20em");
        addComponent(dateField);
        addComponent(popupDateField);
        addComponent(inlineDateField);
        addComponent(textField);

        Button commitButton = new Button("Commit", event -> {
            String msg = "Commit succesful";
            try {
                fieldGroup.commit();
            } catch (CommitException e) {
                msg = "Commit failed: " + e.getMessage();
            }
            Notification.show(msg);
            log(msg);
        });
        Button discardButton = new Button("Discard", event -> {
            fieldGroup.discard();
            log("Discarded changes");
        });
        Button showBean = new Button("Show bean values",
                event -> log(getPerson(fieldGroup).toString()));
        addComponent(commitButton);
        addComponent(discardButton);
        addComponent(showBean);

        DateObject d = new DateObject(DATE, new Date(443543689789L), DATE,
                DATE);
        fieldGroup.setItemDataSource(new BeanItem<>(d));
    }

    @SuppressWarnings("unchecked")
    public static Person getPerson(FieldGroup binder) {
        return ((BeanItem<Person>) binder.getItemDataSource()).getBean();
    }

    @Override
    protected String getTestDescription() {
        return "Ensure FieldGroupFieldFactory supports Dates";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8539;
    }

}
