package com.vaadin.tests.components.window;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class AttachShouldBeCalledForSubWindows extends AbstractTestCase {
    private static final long serialVersionUID = 1L;

    private Log log = new Log(20);

    boolean addSubWindowBeforeMainWindow = true;

    @Override
    public void init() {

        VaadinRequest request = VaadinService.getCurrentRequest();
        if (request.getParameter("attachMainFirst") != null) {
            addSubWindowBeforeMainWindow = false;
        } else {
            addSubWindowBeforeMainWindow = true;
        }

        LegacyWindow mainWindow = new LegacyWindow() {
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
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        Window w = new Window("Sub window", layout) {
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

            @Override
            public void buttonClick(ClickEvent event) {
                log.log("Button clicked");

            }
        });
        okButton.setClickShortcut(KeyCode.ENTER);
        layout.addComponent(okButton);
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

}
