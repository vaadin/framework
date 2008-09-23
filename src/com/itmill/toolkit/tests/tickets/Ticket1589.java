package com.itmill.toolkit.tests.tickets;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

import javax.imageio.ImageIO;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.terminal.DownloadStream;
import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.terminal.URIHandler;
import com.itmill.toolkit.ui.Link;
import com.itmill.toolkit.ui.Window;

public class Ticket1589 extends Application {

    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);

        MyDynamicResource res = new MyDynamicResource();

        w.addURIHandler(res);

        w
                .addComponent(new Link(
                        "Test (without Content-Disposition, should suggest generatedFile.png when saving, browser default for actual disposition)",
                        new ExternalResource("myresource")));

        w
                .addComponent(new Link(
                        "Test (with Content-Disposition, should popup download dialog that suggests  filename downloadedPNG.png)",
                        new ExternalResource("myresource_download")));
    }
}

class MyDynamicResource implements URIHandler {
    String textToDisplay = (new Date()).toString();

    /**
     * Provides the dynamic resource if the URI matches the resource URI. The
     * matching URI is "/myresource" under the application URI context.
     * 
     * Returns null if the URI does not match. Otherwise returns a download
     * stream that contains the response from the server.
     */
    public DownloadStream handleURI(URL context, String relativeUri) {
        // Catch the given URI that identifies the resource, otherwise let other
        // URI handlers or the Application to handle the response.
        if (!relativeUri.startsWith("myresource")) {
            return null;
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
            ByteArrayInputStream istream = new ByteArrayInputStream(imagebuffer
                    .toByteArray());
            DownloadStream downloadStream = new DownloadStream(istream,
                    "image/png", "generatedFile.png");

            if (relativeUri.startsWith("myresource_download")) {
                downloadStream.setParameter("Content-Disposition",
                        "attachment; filename=\"downloadedPNG.png\"");
            }
            return downloadStream;
        } catch (IOException e) {
            return null;
        }
    }
}