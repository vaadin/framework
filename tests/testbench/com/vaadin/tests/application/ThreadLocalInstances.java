package com.vaadin.tests.application;

import com.vaadin.Application;
import com.vaadin.RootRequiresMoreInformation;
import com.vaadin.terminal.ApplicationResource;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.tests.components.AbstractTestApplication;
import com.vaadin.tests.integration.FlagSeResource;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Root;

public class ThreadLocalInstances extends AbstractTestApplication {
    private static final Application staticInitApplication = Application
            .getCurrentApplication();
    private static final Root staticInitRoot = Root.getCurrentRoot();

    private final Root mainWindow = new Root() {
        boolean paintReported = false;

        @Override
        protected void init(WrappedRequest request) {
            reportCurrentStatus("root init");
        }

        @Override
        public void paintContent(com.vaadin.terminal.PaintTarget target)
                throws PaintException {
            if (!paintReported) {
                reportCurrentStatus("root paint");
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        synchronized (ThreadLocalInstances.this) {
                            reportCurrentStatus("background thread");
                        }
                    }
                };
                thread.start();
                paintReported = true;
            }
            super.paintContent(target);
        }
    };

    private final ApplicationResource resource = new FlagSeResource(this) {
        @Override
        public DownloadStream getStream() {
            reportCurrentStatus("resource handler");
            return super.getStream();
        }
    };

    private final Log log = new Log(16);

    public ThreadLocalInstances() {
        mainWindow.addComponent(log);
        mainWindow.addComponent(new Embedded("Icon", resource));
        mainWindow.addComponent(new Button("Sync", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                reportCurrentStatus("button listener");
            }
        }));

        reportStatus("class init", staticInitApplication, staticInitRoot);
        reportCurrentStatus("app constructor");
    }

    @Override
    public void init() {
        reportCurrentStatus("app init");
    }

    @Override
    protected Root getRoot(WrappedRequest request)
            throws RootRequiresMoreInformation {
        return mainWindow;
    }

    @Override
    protected String getTestDescription() {
        return "Tests the precence of Application.getCurrentApplication() and Root.getCurrentRoot() from different contexts";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(7895);
    }

    private void reportCurrentStatus(String phase) {
        reportStatus(phase, Application.getCurrentApplication(),
                Root.getCurrentRoot());
    }

    private void reportStatus(String phase, Application application, Root root) {
        log.log(getState(application, this) + " app in " + phase);
        log.log(getState(root, mainWindow) + " root in " + phase);
    }

    private static String getState(Object value, Object reference) {
        if (value == null) {
            return "null";
        } else if (value == reference) {
            return "this";
        } else {
            return value.toString();
        }
    }

}
