package com.vaadin.tests.integration;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.proxy.ProxyServlet;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServletService;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

@PreserveOnRefresh
public class ProxyTest extends AbstractReindeerTestUI {

    private Server server;

    private final Button startButton = new Button("Start proxy",
            new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    startProxy();
                    stopButton.setEnabled(true);
                }
            });
    private final Button stopButton = new Button("Stop proxy",
            new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    stopProxy();
                    startButton.setEnabled(true);
                }
            });
    private VerticalLayout linkHolder = new VerticalLayout();

    @Override
    protected void setup(VaadinRequest request) {
        stopButton.setDisableOnClick(true);
        stopButton.setEnabled(false);
        startButton.setDisableOnClick(true);

        addDetachListener(event -> {
            if (server != null && server.isRunning()) {
                try {
                    server.stop();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        addComponent(startButton);
        addComponent(stopButton);
        addComponent(linkHolder);
    }

    private void startProxy() {
        HttpServletRequest request = VaadinServletService
                .getCurrentServletRequest();

        // Set up a server
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        // Uses random available port by default, uncomment this to make local
        // testing easier (you can just reload old tab after restarting proxy)
        // connector.setPort(8889);
        server.setConnectors(new Connector[] { connector });

        // Create root context and add the ProxyServlet.Transparent to it
        ServletContextHandler contextHandler = new ServletContextHandler();
        server.setHandler(contextHandler);
        contextHandler.setContextPath("/");
        ServletHolder servletHolder = contextHandler
                .addServlet(ProxyServlet.Transparent.class, "/*");

        // Configure servlet to forward to the root of the original server
        servletHolder.setInitParameter("ProxyTo", "http://"
                + request.getLocalAddr() + ":" + request.getLocalPort() + "/");
        // Configure servlet to strip beginning of paths
        servletHolder.setInitParameter("Prefix", "/proxypath/");

        // Start the server
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Add links to some proxied urls to tests
        String linkBase = "http://" + request.getLocalName() + ":"
                + connector.getLocalPort() + "/proxypath/";

        linkHolder.removeAllComponents();
        linkHolder.addComponent(new Link("Open embed1 in proxy",
                new ExternalResource(linkBase + "embed1")));
        linkHolder.addComponent(new Link("Open embed1/ in proxy",
                new ExternalResource(linkBase + "embed1/")));
        linkHolder.addComponent(
                new Link("Open Buttons in proxy", new ExternalResource(linkBase
                        + "run/com.vaadin.tests.components.button.Buttons")));

    }

    private void stopProxy() {
        linkHolder.removeAllComponents();
        try {
            server.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        server.destroy();
        server = null;
    }

    @Override
    protected String getTestDescription() {
        return "Test UI for starting an embedded Jetty on a different port that proxies requests back to the original server using a different path.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(6771);
    }

}
