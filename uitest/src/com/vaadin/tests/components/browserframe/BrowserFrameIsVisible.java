package com.vaadin.tests.components.browserframe;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import com.vaadin.server.StreamResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;

public class BrowserFrameIsVisible extends TestBase {

    @Override
    protected void setup() {

        HorizontalLayout buttonLayout = new HorizontalLayout();
        addComponent(buttonLayout);

        Button page1 = new Button("Hello World");
        buttonLayout.addComponent(page1);

        Button page2 = new Button("Lorem ipsum");
        buttonLayout.addComponent(page2);

        Button page3 = new Button("null");
        buttonLayout.addComponent(page3);

        final BrowserFrame browser = new BrowserFrame();
        browser.setId("browser");
        browser.setWidth("600px");
        browser.setHeight("300px");
        browser.setAlternateText("Browser alternative text");
        final TextSource textSource = new TextSource("initial");
        final StreamResource textResource = new StreamResource(textSource,
                "initial.txt");
        textResource.setMIMEType("text/plain");
        browser.setSource(textResource);
        addComponent(browser);

        page1.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                TextSource helloSource = new TextSource("Hello World");
                StreamResource helloResource = new StreamResource(helloSource,
                        "helloworld.txt");
                helloResource.setMIMEType("text/plain");
                browser.setSource(helloResource);
            }
        });

        page2.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                TextSource helloSource = new TextSource("Lorem Ipsum");
                StreamResource helloResource = new StreamResource(helloSource,
                        "loremipsum.txt");
                helloResource.setMIMEType("text/plain");
                browser.setSource(helloResource);
            }
        });

        page3.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                browser.setSource(null);
            }
        });
    }

    @Override
    protected String getDescription() {
        return "Embedded browser should be visible for all browsers";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

    public class TextSource implements StreamResource.StreamSource {
        private String text;

        public TextSource(String text) {
            this.text = text;
        }

        @Override
        public InputStream getStream() {

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 200; ++i) {
                sb.append(text);
                sb.append("\n");
            }

            ByteArrayInputStream istream;
            try {
                istream = new ByteArrayInputStream(sb.toString().getBytes(
                        "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
            return istream; // new DownloadStream (istream,null,null);

        }
    }

}
