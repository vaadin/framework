package com.vaadin.tests.themes;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

import com.vaadin.server.Page;
import com.vaadin.server.Page.Styles;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.TextArea;

public class CSSInjectTest extends TestBase {

    @Override
    protected void setup() {

        final Styles stylesheet = Page.getCurrent().getStyles();

        // Inject some resources initially
        final StreamResource initialResource = new StreamResource(
                new StreamResource.StreamSource() {

                    @Override
                    public InputStream getStream() {
                        return new ByteArrayInputStream(
                                ".hello, .world { color:silver; }".getBytes());
                    }
                }, "mystyles-" + System.currentTimeMillis() + ".css");
        stylesheet.add(initialResource);

        Label hello = new Label(
                "<span class='hello'>Hello</span> <span class='world'>world</span>",
                ContentMode.HTML);
        addComponent(hello);

        final TextArea cssToInject = new TextArea();
        cssToInject.setImmediate(true);
        addComponent(cssToInject);

        Button inject = new Button("Inject!", event -> {
            stylesheet.add(cssToInject.getValue());
            cssToInject.setValue("");
        });
        addComponent(inject);

        Button injectRandom = new Button("Inject as resource!", event -> {
            final String css = cssToInject.getValue();

            stylesheet
                    .add(new StreamResource(new StreamResource.StreamSource() {

                        @Override
                        public InputStream getStream() {
                            return new ByteArrayInputStream(css.getBytes());
                        }
                    }, UUID.randomUUID() + ".css"));

            cssToInject.setValue("");
        });
        addComponent(injectRandom);

        addComponent(new Button("Inject initial again!",
                event -> stylesheet.add(initialResource)));
    }

    @Override
    protected String getDescription() {
        return "Demonstrates how CSS injections can be used to theme the \"Hello world\" label below";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5500;
    }

}
