package com.vaadin;

import java.io.IOException;
import java.io.PrintWriter;

import com.vaadin.terminal.RequestHandler;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.terminal.WrappedResponse;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Root;
import com.vaadin.ui.RootLayout;
import com.vaadin.ui.VerticalLayout;

public class RootTestApplication extends Application {
    private static class MyRootLayout extends VerticalLayout implements
            RootLayout {
        public void init() {
            addComponent(new Button("Roots, bloody roots",
                    new Button.ClickListener() {
                        public void buttonClick(ClickEvent event) {
                            event.getButton()
                                    .getRoot()
                                    .executeJavaScript(
                                            "window.alert(\"Here\");");
                        }
                    }));
        }
    }

    private final Root root = new Root(new MyRootLayout());

    @Override
    public void init() {
        // TODO Should be done by Application during init
        root.setApplication(this);

        addRequestHandler(new RequestHandler() {
            public boolean handleRequest(WrappedRequest request,
                    WrappedResponse response) {
                if (request.getParameter("myhandler") != null) {
                    response.setContentType("text/plain");
                    try {
                        PrintWriter writer = response.getWriter();
                        writer.println("Roots, bloody roots");
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    @Override
    public Root getRoot() {
        return root;
    }

}
