package com.vaadin.tests.components.window;

import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.*;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ClosingWindowWithBrowserFrameShouldntGenerate404
        extends AbstractTestUI {

    private VerticalLayout layout;

    @Override
    protected void setup(VaadinRequest request) {
        layout = new VerticalLayout();

        Button button = new Button("Click Me", e -> showPdfInAWindow());
        layout.addComponent(button);
        addComponent(layout);
    }

    private void showPdfInAWindow() {
        try {
            final String fileName = "sample.pdf";
            final byte[] bytes = IOUtils
                    .toByteArray(getClass().getResource("/" + fileName));
            StreamResource.StreamSource source = new StreamResource.StreamSource() {
                @Override
                public InputStream getStream() {
                    return new ByteArrayInputStream(bytes);
                }
            };

            StreamResource resource = new StreamResource(source, fileName);

            resource.setMIMEType("application/pdf");
            resource.getStream().setParameter("Content-Disposition",
                    "attachment; filename=" + fileName);
            resource.setCacheTime(-1);

            // Use browser frame
            BrowserFrame frame = new BrowserFrame();
            frame.setSizeFull();
            frame.setSource(resource);
            frame.setHeight("650px");
            Window pdfWindow = new Window("Sample PDF");
            pdfWindow.center();
            pdfWindow.setModal(true);
            pdfWindow.setResizable(false);
            pdfWindow.setHeight("700px");
            pdfWindow.setWidth("900px");
            pdfWindow.setContent(frame);

            pdfWindow.addCloseListener(e -> {
                layout.addComponent(new Label("PDF was sent"));

            });

            UI.getCurrent().addWindow(pdfWindow);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
