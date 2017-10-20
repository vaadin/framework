package com.vaadin.tests.components.browserframe;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.vaadin.server.StreamResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;

public class BrowserFrameIsVisible extends TestBase {

    @Override
    protected void setup() {

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(false);
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

        page1.addClickListener(e -> {
            TextSource helloSource = new TextSource("Hello World");
            StreamResource helloResource = new StreamResource(helloSource,
                    "helloworld.txt");
            helloResource.setMIMEType("text/plain");
            browser.setSource(helloResource);
        });

        page2.addClickListener(e -> {
            TextSource helloSource = new TextSource("Lorem Ipsum");
            StreamResource helloResource = new StreamResource(helloSource,
                    "loremipsum.txt");
            helloResource.setMIMEType("text/plain");
            browser.setSource(helloResource);
        });

        page3.addClickListener(e -> browser.setSource(null));
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

            return new ByteArrayInputStream(sb.toString().getBytes(UTF_8));
        }
    }

}
