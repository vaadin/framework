package com.vaadin.tests.application;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;

public class ApplicationCloseTest extends TestBase {

    private String memoryConsumer;

    @Override
    protected void setup() {
        Label applications = new Label("Applications in session: <br/>",
                ContentMode.HTML);
        if (getContext() != null) {
            applications.setValue(applications.getValue() + "App: "
                    + getContext() + "<br/>");
        }
        applications.setValue(applications.getValue() + "<br/><br/>");

        addComponent(applications);
        Label thisApp = new Label("This applications: " + this);
        Button close = new Button("Close this", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                LegacyWindow ui = (LegacyWindow) event.getButton().getUI();
                ui.getApplication().close();
            }
        });

        StringBuilder sb = new StringBuilder();

        // 100 bytes
        String str = "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789";

        int MB = 5;
        for (int i = 0; i < MB * 10000; i++) {
            sb.append(str);
        }

        memoryConsumer = sb.toString();
        long totalUsage = Runtime.getRuntime().totalMemory();
        String totalUsageString = totalUsage / 1000 / 1000 + "MiB";
        Label memoryUsage = new Label(
                "Using about "
                        + memoryConsumer.length()
                        / 1000
                        / 1000
                        + "MiB memory for this application.<br/>Total memory usage reported as "
                        + totalUsageString + "<br/>", ContentMode.HTML);

        addComponent(thisApp);
        addComponent(memoryUsage);
        addComponent(close);
    }

    @Override
    protected String getDescription() {
        return "Click close to close the application and open a new one";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3732;
    }

}
