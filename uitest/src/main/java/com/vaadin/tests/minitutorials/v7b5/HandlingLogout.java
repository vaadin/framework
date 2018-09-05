package com.vaadin.tests.minitutorials.v7b5;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServletService;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

public class HandlingLogout extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new Button("Logout", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                logout();
            }
        }));
    }

    private void logout() {
        // Close the VaadinServiceSession
        getUI().getSession().close();

        // Invalidate underlying session instead if login info is stored there
        // VaadinService.getCurrentRequest().getWrappedSession().invalidate();

        // Redirect to avoid keeping the removed UI open in the browser
        getUI().getPage().setLocation(getLogoutPageLocation());
    }

    protected String getLogoutPageLocation() {
        return VaadinServletService.getCurrentRequest().getContextPath()
                + "logout.html";
    }

    @Override
    protected String getTestDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(9646);
    }

}
