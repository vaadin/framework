package com.vaadin.tests.components;

import com.vaadin.Application;

public abstract class AbstractTestCase extends Application {

    protected abstract String getDescription();

    protected abstract Integer getTicketNumber();

}
