package com.vaadin;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import com.vaadin.terminal.ApplicationResource;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.RequestHandler;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.terminal.WrappedResponse;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.LoginForm.LoginEvent;
import com.vaadin.ui.Root;
import com.vaadin.ui.RootLayout;
import com.vaadin.ui.VerticalLayout;

public class RootTestApplication extends Application {
    private static class MyRootLayout extends VerticalLayout implements
            RootLayout {
        private final String rootText;

        public MyRootLayout(String rootText) {
            this.rootText = rootText;
        }

        public void init(WrappedRequest request) {
            if (rootText != null && rootText.trim().length() != 0) {
                addComponent(new Label(rootText));
            }
            addComponent(new Button("Roots, bloody roots",
                    new Button.ClickListener() {
                        public void buttonClick(ClickEvent event) {
                            event.getButton()
                                    .getRoot()
                                    .executeJavaScript(
                                            "window.alert(\"Here\");");
                        }
                    }));
            ApplicationResource resource = new ApplicationResource() {

                public String getMIMEType() {
                    return "text/plain";
                }

                public DownloadStream getStream() {
                    try {
                        return new DownloadStream(new ByteArrayInputStream(
                                "Roots".getBytes("UTF-8")), getMIMEType(),
                                getFilename());
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                }

                public String getFilename() {
                    return "roots.txt";
                }

                public long getCacheTime() {
                    return 60 * 60 * 1000;
                }

                public int getBufferSize() {
                    return 0;
                }

                public Application getApplication() {
                    return MyRootLayout.this.getApplication();
                }
            };
            getApplication().addResource(resource);
            addComponent(new Link("Resource", resource));

            LoginForm loginForm = new LoginForm();
            loginForm.addListener(new LoginForm.LoginListener() {
                public void onLogin(LoginEvent event) {
                    System.out.println("User: "
                            + event.getLoginParameter("username")
                            + ", Password: "
                            + event.getLoginParameter("password"));
                }
            });
            addComponent(loginForm);
        }
    }

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

    @Override
    protected Root createRoot(WrappedRequest request) {
        String rootText = request.getParameter("rootText");
        Root root = new Root(new MyRootLayout(rootText));
        return root;
    }

}
