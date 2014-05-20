package com.vaadin.tests.tickets;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import javax.imageio.ImageIO;

import com.vaadin.server.DownloadStream;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.LegacyApplication;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Link;

public class Ticket1589 extends LegacyApplication {

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getSimpleName());
        setMainWindow(w);

        MyDynamicResource res = new MyDynamicResource();

        VaadinSession.getCurrent().addRequestHandler(res);

        w.addComponent(new Link(
                "Test (without Content-Disposition, should suggest generatedFile.png when saving, browser default for actual disposition)",
                new ExternalResource("myresource")));

        w.addComponent(new Link(
                "Test (with Content-Disposition, should popup download dialog that suggests  filename downloadedPNG.png)",
                new ExternalResource("myresource_download")));
    }
}

class MyDynamicResource implements RequestHandler {
    String textToDisplay = (new Date()).toString();

    /**
     * Provides the dynamic resource if the URI matches the resource URI. The
     * matching URI is "/myresource" under the application URI context.
     * 
     * Returns null if the URI does not match. Otherwise returns a download
     * stream that contains the response from the server.
     */
    @Override
    public boolean handleRequest(VaadinSession session, VaadinRequest request,
            VaadinResponse response) throws IOException {
        String relativeUri = request.getPathInfo();
        // Catch the given URI that identifies the resource, otherwise let other
        // URI handlers or the Application to handle the response.
        if (!relativeUri.startsWith("myresource")) {
            return false;
        }

        // Create an image and draw some background on it.
        BufferedImage image = new BufferedImage(200, 200,
                BufferedImage.TYPE_INT_RGB);
        Graphics drawable = image.getGraphics();
        drawable.setColor(Color.lightGray);
        drawable.fillRect(0, 0, 200, 200);
        drawable.setColor(Color.yellow);
        drawable.fillOval(25, 25, 150, 150);
        drawable.setColor(Color.blue);
        drawable.drawRect(0, 0, 199, 199);

        // Use the parameter to create dynamic content.
        drawable.setColor(Color.black);
        drawable.drawString("Time: " + textToDisplay, 75, 100);

        try {
            // Write the image to a buffer.
            ByteArrayOutputStream imagebuffer = new ByteArrayOutputStream();
            ImageIO.write(image, "png", imagebuffer);

            // Return a stream from the buffer.
            ByteArrayInputStream istream = new ByteArrayInputStream(
                    imagebuffer.toByteArray());
            DownloadStream downloadStream = new DownloadStream(istream,
                    "image/png", "generatedFile.png");

            if (relativeUri.startsWith("myresource_download")) {
                downloadStream.setParameter("Content-Disposition",
                        "attachment; filename=\"downloadedPNG.png\"");
            }
            downloadStream.writeResponse(request, response);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
