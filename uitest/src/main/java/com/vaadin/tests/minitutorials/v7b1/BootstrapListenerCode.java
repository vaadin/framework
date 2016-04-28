/*
 * Copyright 2012 Vaadin Ltd.
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

package com.vaadin.tests.minitutorials.v7b1;

import java.util.List;

import javax.portlet.PortletException;
import javax.servlet.ServletException;

import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Tag;

import com.vaadin.server.BootstrapFragmentResponse;
import com.vaadin.server.BootstrapListener;
import com.vaadin.server.BootstrapPageResponse;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinPortlet;
import com.vaadin.server.VaadinServlet;

public class BootstrapListenerCode {
    public static BootstrapListener listener = new BootstrapListener() {
        @Override
        public void modifyBootstrapPage(BootstrapPageResponse response) {
            response.getDocument().body()
                    .appendChild(new Comment("Powered by Vaadin!", ""));
            response.setHeader("X-Powered-By", "Vaadin 7");
        }

        @Override
        public void modifyBootstrapFragment(BootstrapFragmentResponse response) {
            // Wrap the fragment in a custom div element
            Element myDiv = new Element(Tag.valueOf("div"), "");
            List<Node> nodes = response.getFragmentNodes();
            for (Node node : nodes) {
                myDiv.appendChild(node);
            }
            nodes.clear();
            nodes.add(myDiv);
        }
    };
}

class MyVaadinServlet extends VaadinServlet {
    @Override
    protected void servletInitialized() throws ServletException {
        super.servletInitialized();
        getService().addSessionInitListener(new SessionInitListener() {
            @Override
            public void sessionInit(SessionInitEvent event)
                    throws ServiceException {
                event.getSession().addBootstrapListener(
                        BootstrapListenerCode.listener);
            }
        });
    }
}

// Or...

class MyVaadinPortlet extends VaadinPortlet {
    @Override
    protected void portletInitialized() throws PortletException {
        super.portletInitialized();
        getService().addSessionInitListener(new SessionInitListener() {
            @Override
            public void sessionInit(SessionInitEvent event)
                    throws ServiceException {
                event.getSession().addBootstrapListener(
                        BootstrapListenerCode.listener);
            }
        });
    }
}
