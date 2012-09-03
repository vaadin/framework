package com.vaadin.tests.application;

import com.vaadin.Application;
import com.vaadin.server.DownloadStream;
import com.vaadin.server.PaintException;
import com.vaadin.server.UIProvider;
import com.vaadin.server.WrappedRequest;
import com.vaadin.tests.components.AbstractTestApplication;
import com.vaadin.tests.integration.FlagSeResource;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.UI;

public class ThreadLocalInstances extends AbstractTestApplication {
    private static final Application staticInitApplication = Application
            .getCurrent();
    private static final UI staticInitRoot = UI.getCurrent();

    private final UI mainWindow = new UI() {
        boolean paintReported = false;

        @Override
        protected void init(WrappedRequest request) {
            reportCurrentStatus("root init");
        }

        @Override
        public void paintContent(com.vaadin.server.PaintTarget target)
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

    private final FlagSeResource resource = new FlagSeResource() {
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
            @Override
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
        addUIProvider(new UIProvider() {
            @Override
            public UI createInstance(Application application,
                    Class<? extends UI> type, WrappedRequest request) {
                return mainWindow;
            }

            @Override
            public Class<? extends UI> getUIClass(Application application,
                    WrappedRequest request) {
                return mainWindow.getClass();
            }
        });
    }

    @Override
    protected String getTestDescription() {
        return "Tests the precence of Application.getCurrentApplication() and UI.getCurrentRoot() from different contexts";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(7895);
    }

    private void reportCurrentStatus(String phase) {
        reportStatus(phase, Application.getCurrent(), UI.getCurrent());
    }

    private void reportStatus(String phase, Application application, UI uI) {
        log.log(getState(application, this) + " app in " + phase);
        log.log(getState(uI, mainWindow) + " root in " + phase);
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
