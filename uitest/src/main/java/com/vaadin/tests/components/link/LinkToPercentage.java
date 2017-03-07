/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.components.link;

import java.io.IOException;
import java.io.InputStream;

import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Link;

public class LinkToPercentage extends TestBase {

    @Override
    protected void setup() {
        String filename = "110% Vaadin";
        Resource resource = new StreamResource(new StreamSource() {
            @Override
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
        }, filename);

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
