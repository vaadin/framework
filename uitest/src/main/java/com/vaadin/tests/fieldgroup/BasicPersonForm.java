package com.vaadin.tests.fieldgroup;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.converter.StringToBooleanConverter;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.data.bean.Address;
import com.vaadin.tests.data.bean.Country;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class BasicPersonForm extends AbstractTestUIWithLog {

    private TextField firstName;
    private TextArea lastName;
    private TextField email;
    private TextField age;
    private Table sex;
    private TextField deceased;

    public class Configuration {
        public boolean preCommitFails = false;
        public boolean postCommitFails = false;

        public boolean isPreCommitFails() {
            return preCommitFails;
        }

        public void setPreCommitFails(boolean preCommitFails) {
            this.preCommitFails = preCommitFails;
        }

        public boolean isPostCommitFails() {
            return postCommitFails;
        }

        public void setPostCommitFails(boolean postCommitFails) {
            this.postCommitFails = postCommitFails;
        }

    }

    private Configuration configuration = new Configuration();

    private class ConfigurationPanel extends Panel {

        public ConfigurationPanel() {
            super("Configuration", new VerticalLayout());
            ((VerticalLayout) getContent()).setMargin(true);
            BeanItem<Configuration> bi = new BeanItem<BasicPersonForm.Configuration>(
                    configuration);
            FieldGroup confFieldGroup = new FieldGroup(bi);
            confFieldGroup.setItemDataSource(bi);
            confFieldGroup.setBuffered(false);

            for (Object propertyId : bi.getItemPropertyIds()) {
                ((ComponentContainer) getContent()).addComponent(confFieldGroup
                        .buildAndBind(propertyId));
            }

        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(log);
        Panel confPanel = new ConfigurationPanel();
        addComponent(confPanel);

        final FieldGroup fieldGroup = new BeanFieldGroup<Person>(Person.class);
        fieldGroup.addCommitHandler(new CommitHandler() {

            @Override
            public void preCommit(CommitEvent commitEvent)
                    throws CommitException {
                if (configuration.preCommitFails) {
                    throw new CommitException(
                            "Error in preCommit because first name is "
                                    + getPerson(commitEvent.getFieldBinder())
                                            .getFirstName());
                }

            }

            @Override
            public void postCommit(CommitEvent commitEvent)
                    throws CommitException {
                if (configuration.postCommitFails) {
                    throw new CommitException(
                            "Error in postCommit because first name is "
                                    + getPerson(commitEvent.getFieldBinder())
                                            .getFirstName());
                }
            }
        });

        fieldGroup.setBuffered(true);

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
                Notification notification = new Notification(msg);
                notification.setDelayMsec(Notification.DELAY_FOREVER);
                notification.show(getPage());
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
        email.addValidator(new EmailValidator("Must be a valid address"));
        lastName.addValidator(new StringLengthValidator("Must be min 5 chars",
                5, null, true));

        age.addValidator(new IntegerRangeValidator(
                "Must be between 0 and 150, {0} is not", 0, 150));
        sex.setPageLength(0);
        deceased.setConverter(new StringToBooleanConverter() {
            @Override
            protected String getTrueString() {
                return "YAY!";
            }

            @Override
            protected String getFalseString() {
                return "NAAAAAH";
            }
        });
        Person p = new Person("John", "Doe", "john@doe.com", 64, Sex.MALE,
                new Address("John street", 11223, "John's town", Country.USA));
        fieldGroup.setItemDataSource(new BeanItem<Person>(p));
    }

    @SuppressWarnings("unchecked")
    public static Person getPerson(FieldGroup binder) {
        return ((BeanItem<Person>) binder.getItemDataSource()).getBean();
    }

    @Override
    public String getDescription() {
        return "Basic Person Form";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8094;
    }

}
