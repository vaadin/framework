package com.vaadin.tests.fieldgroup;

import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitException;

public abstract class AbstractBeanFieldGroupTest<T> extends TestBase {

    private Button commitButton;
    protected Log log = new Log(5);

    private Button discardButton;
    private Button showBeanButton;
    private BeanFieldGroup<T> fieldBinder;

    @Override
    protected void setup() {
        addComponent(log);
    }

    protected Button getDiscardButton() {
        if (discardButton == null) {
            discardButton = new Button("Discard", event -> {
                getFieldBinder().discard();
                log.log("Discarded changes");
            });
        }
        return discardButton;
    }

    protected Button getShowBeanButton() {
        if (showBeanButton == null) {
            showBeanButton = new Button("Show bean values", event -> log.log(
                    getFieldBinder().getItemDataSource().getBean().toString()));
        }
        return showBeanButton;
    }

    protected Button getCommitButton() {
        if (commitButton == null) {
            commitButton = new Button("Commit");
            commitButton.addClickListener(event -> {
                String msg = "Commit succesful";
                try {
                    getFieldBinder().commit();
                } catch (CommitException e) {
                    msg = "Commit failed: " + e.getMessage();
                }
                Notification.show(msg);
                log.log(msg);
            });
        }
        return commitButton;
    }

    protected BeanFieldGroup<T> getFieldBinder() {
        return fieldBinder;
    }

    protected void setFieldBinder(BeanFieldGroup<T> beanFieldBinder) {
        fieldBinder = beanFieldBinder;
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
