package com.vaadin.tests.fieldgroup;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;

public abstract class AbstractBeanFieldGroupTest extends TestBase {

    private Button commitButton;
    protected Log log = new Log(5);

    private Button discardButton;
    private Button showBeanButton;
    private BeanFieldGroup fieldBinder;

    @Override
    protected void setup() {
        addComponent(log);
    }

    protected Button getDiscardButton() {
        if (discardButton == null) {
            discardButton = new Button("Discard", new Button.ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    getFieldBinder().discard();
                    log.log("Discarded changes");

                }
            });
        }
        return discardButton;
    }

    protected Button getShowBeanButton() {
        if (showBeanButton == null) {
            showBeanButton = new Button("Show bean values",
                    new Button.ClickListener() {

                        @Override
                        public void buttonClick(ClickEvent event) {
                            log.log(getFieldBinder().getItemDataSource()
                                    .getBean().toString());

                        }
                    });
        }
        return showBeanButton;
    }

    protected Button getCommitButton() {
        if (commitButton == null) {
            commitButton = new Button("Commit");
            commitButton.addListener(new ClickListener() {

                @Override
                public void buttonClick(ClickEvent event) {
                    String msg = "Commit succesful";
                    try {
                        getFieldBinder().commit();
                    } catch (CommitException e) {
                        msg = "Commit failed: " + e.getMessage();
                    }
                    Notification.show(msg);
                    log.log(msg);

                }
            });
        }
        return commitButton;
    }

    protected BeanFieldGroup getFieldBinder() {
        return fieldBinder;
    }

    protected void setFieldBinder(BeanFieldGroup beanFieldBinder) {
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
