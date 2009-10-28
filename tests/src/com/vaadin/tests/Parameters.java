/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.tests;

import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ParameterHandler;
import com.vaadin.terminal.URIHandler;
import com.vaadin.ui.ExpandLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

/**
 * This is a demonstration of how URL parameters can be recieved and handled.
 * Parameters and URL:s can be received trough the windows by registering
 * URIHandler and ParameterHandler classes window.
 * 
 * @since 3.1.1
 */
public class Parameters extends com.vaadin.Application implements
        URIHandler, ParameterHandler {

    private final Label context = new Label();

    private final Label relative = new Label();

    private final Table params = new Table();

    @Override
    @SuppressWarnings("deprecation")
    public void init() {
        final Window main = new Window("Parameters demo");
        setMainWindow(main);

        // This class acts both as URI handler and parameter handler
        main.addURIHandler(this);
        main.addParameterHandler(this);

        final ExpandLayout layout = new ExpandLayout();
        final Label info = new Label("To test URI and Parameter Handlers, "
                + "add get parameters to URL. For example try examples below: ");
        info.setCaption("Usage info");
        layout.addComponent(info);
        try {
            final URL u1 = new URL(getURL(), "test/uri?test=1&test=2");
            final URL u2 = new URL(getURL(), "foo/bar?mary=john&count=3");
            layout.addComponent(new Link(u1.toString(),
                    new ExternalResource(u1)));
            layout.addComponent(new Label("Or this: "));
            layout.addComponent(new Link(u2.toString(),
                    new ExternalResource(u2)));
        } catch (final Exception e) {
            System.out.println("Couldn't get hostname for this machine: "
                    + e.toString());
            e.printStackTrace();
        }

        // URI
        final Panel panel1 = new Panel("URI Handler");
        context.setCaption("Last URI handler context");
        panel1.addComponent(context);
        relative.setCaption("Last relative URI");
        panel1.addComponent(relative);
        layout.addComponent(panel1);

        params.addContainerProperty("Key", String.class, "");
        params.addContainerProperty("Value", String.class, "");
        final Panel panel2 = new Panel("Parameter Handler");
        params.setSizeFull();
        panel2.setLayout(new ExpandLayout());
        panel2.getLayout().setMargin(true);

        params.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_ID);
        panel2.addComponent(params);
        layout.addComponent(panel2);

        // expand parameter panel and its table
        layout.expand(panel2);

        layout.setMargin(true);
        layout.setSpacing(true);

        main.setLayout(layout);
    }

    /**
     * Update URI
     * 
     * @see com.vaadin.terminal.URIHandler#handleURI(URL, String)
     */
    @Override
    public DownloadStream handleURI(URL context, String relativeUri) {
        this.context.setValue(context.toString());
        relative.setValue(relativeUri);
        return null;
    }

    /**
     * Handles GET parameters, in this demo GET parameteres are used to
     * communicate with EmbeddedToolkit.jsp
     */
    public void handleParameters(Map parameters) {
        params.removeAllItems();
        for (final Iterator i = parameters.keySet().iterator(); i.hasNext();) {
            final String name = (String) i.next();
            final String[] values = (String[]) parameters.get(name);
            String v = "";
            for (int j = 0; j < values.length; j++) {
                if (v.length() > 0) {
                    v += ", ";
                }
                v += "'" + values[j] + "'";
            }
            params.addItem(new Object[] { name, v }, name);
        }
    }
}
