package com.vaadin.tests.push;

import com.vaadin.annotations.Push;
import com.vaadin.server.SessionDestroyEvent;
import com.vaadin.server.SessionDestroyListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.WrappedSession;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;

@Push(transport = Transport.WEBSOCKET)
public class PushWebsocketDeadlockUI extends AbstractTestUIWithLog {

    // Test for https://dev.vaadin.com/ticket/18436
    // Needs breakpoints to test, see ticket for more information
    // Can reproduce on Tomcat 8, can't seem to reproduce using
    // DevelopmentServerLauncher

    // Rough steps to reproduce
    // 1. Open test in a new Chrome window
    // 2. Set breakpoint in PushHandler.connectionLost
    // 3. Set breakpoint in UI.close
    // 4. Set breakpoint in PushRequestHandler.handleRequest
    // 5. Click the "schedule UI close" button
    // 6. Close the Chrome window before the 5s timeout expires and ensure it
    // really closes
    // 7. Wait for three threads to hit their breakpoints
    // 8. Continue/step forward in proper order (see ticket)

    @Override
    protected void setup(VaadinRequest request) {
        WrappedSession wrappedSession = getSession().getSession();
        request.getService()
                .addSessionDestroyListener(new SessionDestroyListener() {
                    @Override
                    public void sessionDestroy(SessionDestroyEvent e) {
                        System.out.println(
                                "Session " + e.getSession() + " destroyed");
                    }
                });
        final Label l = new Label("Session timeout is "
                + wrappedSession.getMaxInactiveInterval() + "s");
        addComponents(l);

        Button button = new Button("Invalidate session");
        button.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent e) {
                System.out.println(
                        "invalidating " + getSession() + " for http session "
                                + getSession().getSession().getId());
                getSession().getSession().invalidate();
                System.out.println("invalidated " + getSession());
            }
        });
        addComponents(button);
        button = new Button("Close UI");
        button.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent e) {
                System.out.println("closing UI " + getUIId() + " in session "
                        + getSession() + " for http session "
                        + getSession().getSession().getId());
                close();
            }
        });
        addComponents(button);
        button = new Button("Schedule Close UI (5s delay)");
        button.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        // Breakpoint here
                        access(new Runnable() {
                            @Override
                            public void run() {
                                close();
                                System.out.println("closing UI " + getUIId()
                                        + " in session " + getSession()
                                        + " for http session "
                                        + getSession().getSession().getId());

                            }
                        });

                    }
                }).start();
            }
        });
        addComponents(button);
        button = new Button("Slow (5s) operation");
        button.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent e) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                addComponent(new Label("Slow operation done"));
            }
        });

        addComponents(button);

    }

}
