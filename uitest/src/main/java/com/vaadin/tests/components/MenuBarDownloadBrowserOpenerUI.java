package com.vaadin.tests.components;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.ClassResource;
import com.vaadin.server.ConnectorResource;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.embedded.EmbeddedPdf;
import com.vaadin.tests.extensions.EventTriggerExtension;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.ui.Button;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;

@Widgetset(TestingWidgetSet.NAME)
public class MenuBarDownloadBrowserOpenerUI extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {

        ConnectorResource downloadResource = new ClassResource(
                EmbeddedPdf.class, "test.pdf");
        ExternalResource openResource = new ExternalResource(
                "https://vaadin.com");

        MenuBar menuBar = new MenuBar();
        MenuItem download = menuBar.addItem("Download");
        MenuItem saveAsNoLog = download.addItem("Save as without logging...");
        MenuItem saveAsLog = download.addItem("Save as with logging...",
                item -> log("Download triggered"));
        FileDownloader fd = new FileDownloader(downloadResource);
        fd.extend(saveAsNoLog);
        FileDownloader fd2 = new FileDownloader(downloadResource);
        fd2.extend(saveAsLog);

        MenuItem open = menuBar.addItem("Open");
        MenuItem openNoLog = open.addItem("Open without logging...");
        MenuItem openLog = open.addItem("Open with logging...",
                item -> log("Open triggered"));

        BrowserWindowOpener bwo = new BrowserWindowOpener(openResource);
        bwo.extend(openNoLog);
        BrowserWindowOpener bwo2 = new BrowserWindowOpener(openResource);
        bwo2.extend(openLog);

        addComponent(menuBar);

        addComponent(new Button("Remove downloaders and openers", event -> {
            fd.remove();
            fd2.remove();
            bwo.remove();
            bwo2.remove();
        }));

        setupTestExtension(menuBar);

    }

    private void setupTestExtension(MenuBar menuBar) {
        EventTriggerExtension triggerable1 = new EventTriggerExtension();
        EventTriggerExtension triggerable2 = new EventTriggerExtension();

        MenuItem testExtension = menuBar.addItem("TestExtension");
        MenuItem runMe = testExtension.addItem("RunMe");
        triggerable1.extend(runMe);

        testExtension.addItem("AddTrigger", c -> triggerable2.extend(runMe));
        testExtension.addItem("RemoveTrigger", c -> triggerable2.remove());

    }

}
