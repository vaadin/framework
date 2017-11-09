package com.vaadin.tests.resources;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;

public class ResourceDownload extends TestBase {

    @Override
    public void setup() {

        Button b = new Button("Download (_new)", event -> download("_new"));
        addComponent(b);

        b = new Button("Download (_blank)", event -> download("_blank"));
        addComponent(b);

        b = new Button("Download ()", event -> download(""));
        addComponent(b);

        b = new Button("Download (_top)", event -> download("_top"));
        addComponent(b);

        b = new Button("Test", event -> ResourceDownload.this.getMainWindow()
                .showNotification("Still working"));
        addComponent(b);

    }

    protected void download(String target) {
        String filename = "filename";
        StreamResource streamResource = new StreamResource(new StreamSource() {

            @Override
            public InputStream getStream() {
                try {
                    return new FileInputStream("FIXME C:/temp/file.xls");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }, filename + ".xls");
        streamResource.setCacheTime(5000); // no cache (<=0) does not work with
        // IE8
        streamResource.setMIMEType("application/x-msexcel");

        getMainWindow().open(streamResource, target);

    }

    @Override
    protected String getDescription() {
        return "Downloading with target _new should work, as well as with target _blank and _top.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3289;
    }
}
