package com.vaadin.tests.integration;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.WindowState;

import com.vaadin.annotations.StyleSheet;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinPortletRequest;
import com.vaadin.server.VaadinPortletService;
import com.vaadin.server.VaadinPortletSession;
import com.vaadin.server.VaadinPortletSession.PortletListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.VerticalLayout;

/**
 * Adapted from old PortletDemo to support integration testing.
 */
@StyleSheet("PortletConnectorResource.css")
public class JSR286Portlet extends UI {

    TextField tf = new TextField("Some value");
    Label userInfo = new Label();
    Link portletEdit = new Link();
    Link portletMax = new Link();
    Link someAction = null;
    Label userAgent = new Label();
    Label screenWidth = new Label();
    Label screenHeight = new Label();
    private VerticalLayout main = new VerticalLayout();

    @Override
    protected void init(VaadinRequest request) {
        setContent(main);
        Embedded appResourceTest = new Embedded(
                "Test of ApplicationResources with full path",
                new FlagSeResource());
        main.addComponent(appResourceTest);
        Embedded specialNameResourceTest = new Embedded(
                "Test ApplicationResources with special names",
                new SpecialNameResource());
        specialNameResourceTest.addStyleName("hugeBorder");
        main.addComponent(specialNameResourceTest);

        userInfo.setCaption("User info");
        userInfo.setContentMode(ContentMode.PREFORMATTED);
        main.addComponent(userInfo);

        tf.setEnabled(false);
        tf.setImmediate(true);
        main.addComponent(tf);

        portletEdit.setEnabled(false);
        main.addComponent(portletEdit);
        portletMax.setEnabled(false);
        main.addComponent(portletMax);

        Upload upload = new Upload("Upload a file", new Receiver() {

            @Override
            public OutputStream receiveUpload(String filename, String mimeType) {
                return new ByteArrayOutputStream();
            }
        });
        main.addComponent(upload);

        possiblyChangedModeOrState();

        userAgent.setCaption("User Agent");
        main.addComponent(userAgent);

        screenWidth.setCaption("Screen width");
        main.addComponent(screenWidth);

        screenHeight.setCaption("Screen height");
        main.addComponent(screenHeight);

        getSession().addPortletListener(new DemoPortletListener());
    }

    @Override
    public VaadinPortletSession getSession() {
        return (VaadinPortletSession) super.getSession();
    }

    private void possiblyChangedModeOrState() {
        VaadinPortletRequest request = VaadinPortletService.getCurrentRequest();

        String censoredUserAgent = getPage().getWebBrowser()
                .getBrowserApplication();
        if (censoredUserAgent != null && censoredUserAgent.contains("Chrome/")) {
            // Censor version info as it tends to change
            censoredUserAgent = censoredUserAgent.replaceAll("Chrome/[^ ]* ",
                    "Chrome/xyz ");
        }
        userAgent.setValue(censoredUserAgent);
        screenWidth.setValue(String.valueOf(getPage().getBrowserWindowWidth()));
        screenHeight.setValue(String
                .valueOf(getPage().getBrowserWindowHeight()));

        boolean inViewMode = (request.getPortletMode() == PortletMode.VIEW);
        boolean inNormalState = (request.getWindowState() == WindowState.NORMAL);
        // Portlet up-and-running, enable stuff
        portletEdit.setEnabled(true);
        portletMax.setEnabled(true);

        // Editable if we're in editmode
        tf.setEnabled(!inViewMode);

        // Show notification about current mode and state
        getPage().showNotification(
                new Notification("Portlet status", "Mode: "
                        + request.getPortletMode() + " State: "
                        + request.getWindowState(), Type.WARNING_MESSAGE));

        // Display current user info
        Map<?, ?> uinfo = (Map<?, ?>) request
                .getAttribute(PortletRequest.USER_INFO);
        if (uinfo != null) {
            String s = "";
            for (Iterator<?> it = uinfo.keySet().iterator(); it.hasNext();) {
                Object key = it.next();
                Object val = uinfo.get(key);
                s += key + ": " + val + "\n";
            }
            if (request.isUserInRole("administrator")) {
                s += "(administrator)";
            }
            userInfo.setValue(s);
        } else {
            userInfo.setValue("-");
        }

        // Create Edit/Done link (actionUrl)
        PortletURL url = getSession().generateActionURL("changeMode");
        try {
            if (inViewMode) {
                url.setPortletMode(PortletMode.EDIT);
                portletEdit.setCaption("Edit");
            } else {
                url.setPortletMode(PortletMode.VIEW);
                portletEdit.setCaption("Done");
            }
            portletEdit.setResource(new ExternalResource(url.toString()));
        } catch (Exception e) {
            portletEdit.setEnabled(false);
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                    "Error creating edit mode link", e);
        }

        // Create Maximize/Normal link (actionUrl)
        url = getSession().generateActionURL("changeState");
        try {
            if (inNormalState) {
                url.setWindowState(WindowState.MAXIMIZED);
                portletMax.setCaption("Maximize");
            } else {
                url.setWindowState(WindowState.NORMAL);
                portletMax.setCaption("Back to normal");

            }
            portletMax.setResource(new ExternalResource(url.toString()));
        } catch (Exception e) {
            portletMax.setEnabled(false);
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                    "Error creating state change link", e);
        }

        if (someAction == null) {
            url = getSession().generateActionURL("someAction");
            try {
                someAction = new Link("An action", new ExternalResource(
                        url.toString()));
                main.addComponent(someAction);
            } catch (Exception e) {
                // Oops
                System.err.println("Could not create someAction: " + e);
            }

        }
    }

    private class DemoPortletListener implements PortletListener {

        @Override
        public void handleActionRequest(ActionRequest request,
                ActionResponse response, UI window) {
            main.addComponent(new Label("Action '"
                    + request.getParameter("javax.portlet.action")
                    + "' received"));
        }

        @Override
        public void handleRenderRequest(RenderRequest request,
                RenderResponse response, UI window) {
            possiblyChangedModeOrState();
        }

        @Override
        public void handleEventRequest(EventRequest request,
                EventResponse response, UI window) {
            // events not used by this test
        }

        @Override
        public void handleResourceRequest(ResourceRequest request,
                ResourceResponse response, UI window) {
            // nothing special to do here
        }
    }

}
