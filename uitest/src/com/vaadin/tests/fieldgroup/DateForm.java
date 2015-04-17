package com.vaadin.tests.fieldgroup;

import java.util.Date;
import java.util.Locale;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;

public class DateForm extends AbstractTestUIWithLog {

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
        final FieldGroup fieldGroup = new BeanFieldGroup<DateObject>(
                DateObject.class);
        fieldGroup.setBuffered(true);

        fieldGroup.buildAndBindMemberFields(this);
        textField.setWidth("20em");
        addComponent(dateField);
        addComponent(popupDateField);
        addComponent(inlineDateField);
        addComponent(textField);

        Button commitButton = new Button("Commit", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                String msg = "Commit succesful";
                try {
                    fieldGroup.commit();
                } catch (CommitException e) {
                    msg = "Commit failed: " + e.getMessage();
                }
                Notification.show(msg);
                log(msg);

            }
        });
        Button discardButton = new Button("Discard",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        fieldGroup.discard();
                        log("Discarded changes");
                    }
                });
        Button showBean = new Button("Show bean values",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        log(getPerson(fieldGroup).toString());

                    }
                });
        addComponent(commitButton);
        addComponent(discardButton);
        addComponent(showBean);

        DateObject d = new DateObject(new Date(443457289789L), new Date(
                443457289789L), new Date(443457289789L),
                new Date(443457289789L));
        fieldGroup.setItemDataSource(new BeanItem<DateObject>(d));
    }

    @SuppressWarnings("unchecked")
    public static Person getPerson(FieldGroup binder) {
        return ((BeanItem<Person>) binder.getItemDataSource()).getBean();
    }

    @Override
    public String getDescription() {
        return "Ensure FieldGroupFieldFactory supports Dates";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8539;
    }

}
