/* 
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.tests;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

/**
 * This is a demonstration of how URL parameters can be recieved and handled.
 * Parameters and URL:s can be received trough the windows by registering
 * URIHandler and ParameterHandler classes window.
 * 
 * @since 3.1.1
 */
public class Parameters extends com.vaadin.server.LegacyApplication implements
        RequestHandler {

    private final Label context = new Label();

    private final Label relative = new Label();

    private final Table params = new Table();

    @Override
    public void init() {
        final LegacyWindow main = new LegacyWindow("Parameters demo");
        setMainWindow(main);

        // This class acts both as URI handler and parameter handler
        VaadinSession.getCurrent().addRequestHandler(this);

        final VerticalLayout layout = new VerticalLayout();
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
        final VerticalLayout panel1Layout = new VerticalLayout();
        panel1Layout.setMargin(true);
        final Panel panel1 = new Panel("URI Handler", panel1Layout);
        context.setCaption("Last URI handler context");
        panel1Layout.addComponent(context);
        relative.setCaption("Last relative URI");
        panel1Layout.addComponent(relative);
        layout.addComponent(panel1);

        params.addContainerProperty("Key", String.class, "");
        params.addContainerProperty("Value", String.class, "");
        final VerticalLayout panel2Layout = new VerticalLayout();
        panel2Layout.setMargin(true);
        final Panel panel2 = new Panel("Parameter Handler", panel2Layout);
        params.setSizeFull();

        params.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_ID);
        panel2Layout.addComponent(params);
        layout.addComponent(panel2);

        // expand parameter panel and its table
        layout.setExpandRatio(panel2, 1);

        layout.setMargin(true);
        layout.setSpacing(true);

        main.setContent(layout);
    }

    @Override
    public boolean handleRequest(VaadinSession session, VaadinRequest request,
            VaadinResponse response) throws IOException {
        context.setValue("Context not available");
        relative.setValue(request.getPathInfo());

        params.removeAllItems();
        Map<String, String[]> parameters = request.getParameterMap();
        for (final Iterator<String> i = parameters.keySet().iterator(); i
                .hasNext();) {
            final String name = i.next();
            final String[] values = parameters.get(name);
            String v = "";
            for (int j = 0; j < values.length; j++) {
                if (v.length() > 0) {
                    v += ", ";
                }
                v += "'" + values[j] + "'";
            }
            params.addItem(new Object[] { name, v }, name);
        }

        return false;
    }
}
