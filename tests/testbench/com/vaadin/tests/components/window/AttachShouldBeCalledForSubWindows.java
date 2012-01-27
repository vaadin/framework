package com.vaadin.tests.components.window;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

public class AttachShouldBeCalledForSubWindows extends AbstractTestCase
        implements HttpServletRequestListener {
    private static final long serialVersionUID = 1L;

    private Log log = new Log(20);

    boolean addSubWindowBeforeMainWindow = true;

    @Override
    public void init() {

        Window mainWindow = new Window() {
            @Override
            public void attach() {
                log(this);
                super.attach();
            }

            @Override
            public void addWindow(Window w) {
                log.log("Adding sub window");
                super.addWindow(w);
                log.log("Sub window added");

            }
        };
        mainWindow.setCaption("Main window");
        mainWindow.addComponent(log);
        mainWindow.getContent().setSizeFull();
        Label label = new Label("This is the main app") {
            @Override
            public void attach() {
                log(this);
                super.attach();
            }
        };

        mainWindow.addComponent(label);
        Window loginWindow = createSubWindow();
        if (addSubWindowBeforeMainWindow) {
            mainWindow.addWindow(loginWindow);
        }

        log.log("Setting main window");
        setMainWindow(mainWindow); // At this point
        log.log("Main window set");

        if (!addSubWindowBeforeMainWindow) {
            mainWindow.addWindow(loginWindow);
        }
    }

    private Window createSubWindow() {
        Window w = new Window("Sub window") {
            @Override
            public void attach() {
                log(this);
                super.attach();
            }
        };
        Button okButton = new Button("OK") {
            @Override
            public void attach() {
                super.attach();
                log(this);
            }
        };
        okButton.addListener(new ClickListener() {

            public void buttonClick(ClickEvent event) {
                log.log("Button clicked");

            }
        });
        okButton.setClickShortcut(KeyCode.ENTER);
        w.addComponent(okButton);
        w.center();
        return w;
    }

    public void log(Component c) {
        Class<?> cls = c.getClass();
        if (cls.isAnonymousClass()) {
            cls = cls.getSuperclass();
        }
        log.log(cls.getName() + " '" + c.getCaption()
                + "' attached to application");
    }

    @Override
    protected String getDescription() {
        return "By default attaches a sub window with a button to the main window and then set the main window to the application. Use ?attachMainFirst to reverse the order. In both cases attach events should be sent for the components in the sub window";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8170;
    }

    public void onRequestStart(HttpServletRequest request,
            HttpServletResponse response) {
        if (request.getParameter("attachMainFirst") != null) {
            addSubWindowBeforeMainWindow = false;
        }

    }

    public void onRequestEnd(HttpServletRequest request,
            HttpServletResponse response) {
        // TODO Auto-generated method stub

    }
}
