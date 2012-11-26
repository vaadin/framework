package com.vaadin.tests.minitutorials.v7b9;

import java.util.List;

import javax.servlet.ServletException;

import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Tag;

import com.vaadin.server.BootstrapFragmentResponse;
import com.vaadin.server.BootstrapListener;
import com.vaadin.server.BootstrapPageResponse;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinServlet;

public class MyVaadinServlet extends VaadinServlet {
    @Override
    protected void servletInitialized() throws ServletException {
        super.servletInitialized();
        getService().addSessionInitListener(new SessionInitListener() {
            @Override
            public void sessionInit(SessionInitEvent event) {
                event.getSession().addBootstrapListener(
                        new BootstrapListener() {
                            @Override
                            public void modifyBootstrapPage(
                                    BootstrapPageResponse response) {
                                response.getDocument()
                                        .body()
                                        .appendChild(
                                                new Comment(
                                                        "Powered by Vaadin!",
                                                        ""));
                            }

                            @Override
                            public void modifyBootstrapFragment(
                                    BootstrapFragmentResponse response) {
                                // Wrap the fragment in a custom div element
                                Element myDiv = new Element(Tag.valueOf("div"),
                                        "");
                                List<Node> nodes = response.getFragmentNodes();
                                for (Node node : nodes) {
                                    myDiv.appendChild(node);
                                }
                                nodes.clear();
                                nodes.add(myDiv);
                            }
                        });
            }
        });
    }
}