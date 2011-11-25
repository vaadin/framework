package com.vaadin.tests.components.root;

import com.vaadin.Application;
import com.vaadin.RootRequiresMoreInformation;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.terminal.WrappedRequest.BrowserDetails;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Root;
import com.vaadin.ui.VerticalLayout;

public class LazyInitRoots extends Application {

    @Override
    public Root getRoot(WrappedRequest request)
            throws RootRequiresMoreInformation {
        if (request.getParameter("lazyCreate") != null) {
            BrowserDetails browserDetails = request.getBrowserDetails();
            if (browserDetails == null) {
                throw new RootRequiresMoreInformation();
            } else {
                VerticalLayout content = new VerticalLayout();
                content.addComponent(new Label(browserDetails.getUriFragmet()));
                return new Root(content);
            }
        } else {
            VerticalLayout content = new VerticalLayout();
            Link lazyCreateLink = new Link("Open lazyCreate root",
                    new ExternalResource(getURL() + "?lazyCreate"));
            lazyCreateLink.setTargetName("_blank");
            content.addComponent(lazyCreateLink);
            return new Root(content);
        }
    }

    protected String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
