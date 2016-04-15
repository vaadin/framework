package com.vaadin.tests.resources;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class ResourceDownload extends TestBase {

    @Override
    public void setup() {

        Button b = new Button("Download (_new)", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                download("_new");
            }
        });
        addComponent(b);

        b = new Button("Download (_blank)", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                download("_blank");
            }
        });
        addComponent(b);

        b = new Button("Download ()", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                download("");
            }
        });
        addComponent(b);

        b = new Button("Download (_top)", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                download("_top");
            }
        });
        addComponent(b);

        b = new Button("Test", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                ResourceDownload.this.getMainWindow().showNotification(
                        "Still working");
            }

        });
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
                    // TODO Auto-generated catch block
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
        return "Downloading with target _new should work, aswell as with target _blank and _top.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3289;
    }
}
