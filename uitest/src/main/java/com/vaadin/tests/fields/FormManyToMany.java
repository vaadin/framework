package com.vaadin.tests.fields;

import com.vaadin.tests.components.TestBase;

public class FormManyToMany extends TestBase {

    @Override
    protected void setup() {
        // TODO implement

        // TODO note that in one direction, a setter is used and automatically
        // updates the other direction (setting the Roles of a User updates
        // Roles), whereas in the other direction (updating the list of Users
        // for a Role), manual updates are needed at commit time to keep the
        // Users consistent with Roles
    }

    @Override
    protected String getDescription() {
        return "Forms which allow editing of a many-to-many mapping between users and roles";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
