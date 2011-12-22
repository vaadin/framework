package com.vaadin.tests.fieldbinder;

import com.vaadin.data.fieldbinder.BeanFieldGroup;
import com.vaadin.data.fieldbinder.FieldGroup;
import com.vaadin.data.fieldbinder.FieldGroup.CommitEvent;
import com.vaadin.data.fieldbinder.FieldGroup.CommitException;
import com.vaadin.data.fieldbinder.FieldGroup.CommitHandler;
import com.vaadin.data.fieldbinder.FormBuilder;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.converter.StringToBooleanConverter;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.data.bean.Address;
import com.vaadin.tests.data.bean.Country;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Root;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

public class BasicPersonForm extends TestBase {

    private Log log = new Log(5);
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
            super("Configuration");
            BeanItem<Configuration> bi = new BeanItem<BasicPersonForm.Configuration>(
                    configuration);
            FieldGroup confBinder = new FieldGroup(bi);
            confBinder.setItemDataSource(bi);
            confBinder.setFieldsBuffered(false);

            FormBuilder builder = new FormBuilder(confBinder);
            for (Object propertyId : bi.getItemPropertyIds()) {
                addComponent(builder.buildAndBind(propertyId));
            }

        }
    }

    @Override
    protected void setup() {
        addComponent(log);
        Panel confPanel = new ConfigurationPanel();
        addComponent(confPanel);

        final FieldGroup binder = new BeanFieldGroup<Person>(Person.class);
        binder.addCommitHandler(new CommitHandler() {

            public void preCommit(CommitEvent commitEvent)
                    throws CommitException {
                if (configuration.preCommitFails) {
                    throw new CommitException(
                            "Error in preCommit because first name is "
                                    + getPerson(commitEvent.getFieldBinder())
                                            .getFirstName());
                }

            }

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

        binder.setFieldsBuffered(true);

        FormBuilder builder = new FormBuilder(binder);
        builder.buildAndBindFields(this);
        addComponent(firstName);
        addComponent(lastName);
        addComponent(email);
        addComponent(age);
        addComponent(sex);
        addComponent(deceased);

        Button commitButton = new Button("Commit", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                String msg = "Commit succesful";
                try {
                    binder.commit();
                } catch (CommitException e) {
                    msg = "Commit failed: " + e.getMessage();
                }
                Root.getCurrentRoot().showNotification(msg);
                log.log(msg);

            }
        });
        Button discardButton = new Button("Discard",
                new Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        binder.discard();
                        log.log("Discarded changes");

                    }
                });
        Button showBean = new Button("Show bean values",
                new Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        log.log(getPerson(binder).toString());

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
        binder.setItemDataSource(new BeanItem<Person>(p));
    }

    public static Person getPerson(FieldGroup binder) {
        return ((BeanItem<Person>) binder.getItemDataSource()).getBean();
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
