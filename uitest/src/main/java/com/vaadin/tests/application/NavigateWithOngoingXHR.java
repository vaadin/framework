package com.vaadin.tests.application;

import java.io.PrintWriter;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Link;
import com.vaadin.v7.shared.ui.progressindicator.ProgressIndicatorServerRpc;
import com.vaadin.v7.ui.ProgressIndicator;

public class NavigateWithOngoingXHR extends AbstractReindeerTestUI {
    private final RequestHandler slowRequestHandler = (session, request,
            response) -> {
        if ("/slowRequestHandler".equals(request.getPathInfo())) {
            // Make the navigation request last longer to keep the
            // communication error visible
            // System.out.println("Got slow content request");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (request.getParameter("download") != null) {
                response.setHeader("Content-Disposition", "attachment");
            }

            response.setContentType("text/plain");
            PrintWriter writer = response.getWriter();
            writer.println("Loaded slowly");
            writer.close();

            // System.out.println("Finished slow content request");

            return true;
        }
        return false;
    };

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new ProgressIndicator() {
            {
                registerRpc((ProgressIndicatorServerRpc) () -> {
                    // System.out.println("Pausing poll request");
                    try {
                        // Make the XHR request last longer to make it
                        // easier to click the link at the right moment.
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // System.out.println("Continuing poll request");
                });
                setPollingInterval(3000);
            }
        });

        // Hacky URLs that are might not work in all deployment scenarios
        addComponent(new Link("Navigate away",
                new ExternalResource("slowRequestHandler")));
        addComponent(new Link("Start download",
                new ExternalResource("slowRequestHandler?download")));
    }

    @Override
    public void attach() {
        super.attach();
        getSession().addRequestHandler(slowRequestHandler);
    }

    @Override
    public void detach() {
        getSession().removeRequestHandler(slowRequestHandler);
        super.detach();
    }

    @Override
    protected String getTestDescription() {
        return "Navigating away from a Vaadin page while there's an ongoing XHR request should not cause a communication error to be displayed";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(8891);
    }

}
