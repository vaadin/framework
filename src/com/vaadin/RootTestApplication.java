package com.vaadin;

import java.io.IOException;
import java.io.PrintWriter;

import com.vaadin.terminal.RequestHandler;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.terminal.WrappedResponse;

public class RootTestApplication extends Application {
    @Override
    public void init() {
        addRequestHandler(new RequestHandler() {
            public boolean handleRequest(Application application,
                    WrappedRequest request, WrappedResponse response) {
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

    // @Override
    // protected Root createRoot(WrappedRequest request) {
    // String rootText = request.getParameter("rootText");
    // Root root = new Root(new RootTestLayout(rootText));
    // return root;
    // }

}
