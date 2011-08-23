package com.vaadin.tests.components.link;

import java.io.IOException;
import java.io.InputStream;

import com.vaadin.terminal.ApplicationResource;
import com.vaadin.terminal.StreamResource;
import com.vaadin.terminal.StreamResource.StreamSource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Link;

public class LinkToPercentage extends TestBase {

    @Override
    protected void setup() {
        String filename = "110% Vaadin";
        ApplicationResource resource = new StreamResource(new StreamSource() {
            public InputStream getStream() {
                return new InputStream() {
                    boolean first = true;

                    @Override
                    public int read() throws IOException {
                        if (first) {
                            first = false;
                            return 'a';
                        } else {
                            return -1;
                        }
                    }
                };
            }
        }, filename, this);
        addResource(resource);

        Link link = new Link("The link", resource);

        addComponent(link);
    }

    @Override
    protected String getDescription() {
        return "Tests using links with percentage signs in the address";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(5488);
    }

}
