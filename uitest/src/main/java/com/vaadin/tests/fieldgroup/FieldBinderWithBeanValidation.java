package com.vaadin.tests.fieldgroup;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.data.bean.Address;
import com.vaadin.tests.data.bean.Country;
import com.vaadin.tests.data.bean.PersonWithBeanValidationAnnotations;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

public class FieldBinderWithBeanValidation extends TestBase {

    private Log log = new Log(5);
    private TextField firstName;
    private TextArea lastName;
    private TextField email;
    private TextField age;
    private Table sex;
    private TextField deceased;

    @Override
    protected void setup() {
        addComponent(log);

        final BeanFieldGroup<PersonWithBeanValidationAnnotations> fieldGroup = new BeanFieldGroup<PersonWithBeanValidationAnnotations>(
                PersonWithBeanValidationAnnotations.class);

        fieldGroup.buildAndBindMemberFields(this);
        addComponent(firstName);
        addComponent(lastName);
        addComponent(email);
        addComponent(age);
        addComponent(sex);
        addComponent(deceased);

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
                log.log(msg);

            }
        });
        Button discardButton = new Button("Discard",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        fieldGroup.discard();
                        log.log("Discarded changes");

                    }
                });
        Button showBean = new Button("Show bean values",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        log.log(getPerson(fieldGroup).toString());

                    }
                });
        addComponent(commitButton);
        addComponent(discardButton);
        addComponent(showBean);
        sex.setPageLength(0);

        PersonWithBeanValidationAnnotations p = new PersonWithBeanValidationAnnotations(
                "John", "Doe", "john@doe.com", 64, Sex.MALE, new Address(
                        "John street", 11223, "John's town", Country.USA));
        fieldGroup
                .setItemDataSource(new BeanItem<PersonWithBeanValidationAnnotations>(
                        p));
    }

    public static PersonWithBeanValidationAnnotations getPerson(
            FieldGroup binder) {
        return ((BeanItem<PersonWithBeanValidationAnnotations>) binder
                .getItemDataSource()).getBean();
    }

    @Override
    protected String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
