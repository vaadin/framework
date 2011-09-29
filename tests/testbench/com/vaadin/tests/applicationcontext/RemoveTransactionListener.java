package com.vaadin.tests.applicationcontext;

import com.vaadin.Application;
import com.vaadin.service.ApplicationContext;
import com.vaadin.service.ApplicationContext.TransactionListener;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;

public class RemoveTransactionListener extends TestBase {

    private final Log log = new Log(10);

    @Override
    protected void setup() {
        // Add one listener that will remove itself from within transactionEnd
        getMainWindow().getApplication().getContext()
                .addTransactionListener(new TransactionListener() {
                    public void transactionStart(Application application,
                            Object transactionData) {
                    }

                    public void transactionEnd(Application application,
                            Object transactionData) {
                        removeListener(this);
                        log.log("Listener removed in transactionEnd");
                    }
                });

        // Add one listener that will remove itself from within transactionStart
        getMainWindow().getApplication().getContext()
                .addTransactionListener(new TransactionListener() {
                    public void transactionStart(Application application,
                            Object transactionData) {
                        removeListener(this);
                        log.log("Listener removed in transactionStart");
                    }

                    public void transactionEnd(Application application,
                            Object transactionData) {
                    }
                });

        // Add one listener to verify that all listeners are called, as thrown
        // ConcurrentModificationException causes subsequent listeners to be
        // ignored
        getMainWindow().getApplication().getContext()
                .addTransactionListener(new TransactionListener() {
                    public void transactionStart(Application application,
                            Object transactionData) {
                        log.log("transactionStart from last listener");
                    }

                    public void transactionEnd(Application application,
                            Object transactionData) {
                        log.log("transactionEnd from last listener");
                    }
                });

        addComponent(log);
    }

    private void removeListener(TransactionListener l) {
        ApplicationContext context = getMainWindow().getApplication()
                .getContext();
        context.removeTransactionListener(l);
    }

    @Override
    protected String getDescription() {
        return "Tests that a transaction listener can be removed from within the listener.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(7065);
    }

}
