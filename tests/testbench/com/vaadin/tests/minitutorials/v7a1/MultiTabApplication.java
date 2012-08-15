/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.minitutorials.v7a1;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Root;
import com.vaadin.ui.VerticalLayout;

/**
 * Mini tutorial code for
 * https://vaadin.com/wiki/-/wiki/Main/Creating%20multi%20tab%20applications
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public class MultiTabApplication extends Root {

    private class MainView extends VerticalLayout {
        public MainView() {
            addComponent(new Link("Edit person 1", new ExternalResource(
                    "?editPerson=person1")));
            addComponent(new Link("Edit person 2", new ExternalResource(
                    "?editPerson=person2")));
        }
    }

    private class EditPersonView extends VerticalLayout {

        public EditPersonView(String person) {
            addComponent(new Label("Editor for " + person));
        }

    }

    @Override
    public void init(WrappedRequest request) {
        String person = request.getParameter("editPerson");
        if (person == null) {
            setContent(new MainView());
        } else {
            setContent(new EditPersonView(person));
        }
    }

}
