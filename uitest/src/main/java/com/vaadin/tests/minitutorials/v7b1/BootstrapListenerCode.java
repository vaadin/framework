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
                    .appendChild(new Comment("Powered by Vaadin!"));
            response.setHeader("X-Powered-By", "Vaadin 7");
        }

        @Override
        public void modifyBootstrapFragment(
                BootstrapFragmentResponse response) {
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
                event.getSession()
                        .addBootstrapListener(BootstrapListenerCode.listener);
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
                event.getSession()
                        .addBootstrapListener(BootstrapListenerCode.listener);
            }
        });
    }
}
