package com.vaadin.tests.components.window;

import com.vaadin.server.ExternalResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;

public class DownloadAndUpdate extends TestBase {

    @Override
    protected void setup() {
        addComponent(new Button("Download and update",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        downloadAndUpdate();
                    }
                }));
    }

    protected void downloadAndUpdate() {
        getMainWindow().open(
                new ExternalResource("/statictestfiles/dummy.zip", "_new"));

        // Any component sending an UIDL request when rendered will likely do
        Table table = new Table();
        table.addContainerProperty("A", String.class, "");
        for (int i = 0; i < 100; i++) {
            table.addItem(new Object[] { Integer.toString(i) },
                    Integer.valueOf(i));
        }
        addComponent(table);
    }

    @Override
    protected String getDescription() {
        return "There should be no problems downloading a file from the same request that triggers another request, even in webkit browsers.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(8781);
    }

}
