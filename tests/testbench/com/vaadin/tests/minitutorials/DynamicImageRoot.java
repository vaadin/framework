package com.vaadin.tests.minitutorials;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import com.vaadin.Application;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.RequestHandler;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.terminal.WrappedResponse;
import com.vaadin.tests.components.AbstractTestRoot;
import com.vaadin.ui.Embedded;

public class DynamicImageRoot extends AbstractTestRoot {

    @Override
    public void setup(WrappedRequest request) {
        // Add the request handler that handles our dynamic image
        getApplication().addRequestHandler(new DynamicImageRequestHandler());

        // Create a URL that we can handle in DynamicImageRequestHandler
        URL imageUrl;
        try {
            imageUrl = new URL(getApplication().getURL(),
                    DynamicImageRequestHandler.IMAGE_URL + "?text=Hello!");
        } catch (MalformedURLException e) {
            // This should never happen
            throw new RuntimeException(e);
        }

        // Add an embedded using the created URL
        Embedded embedded = new Embedded("A dynamically generated image",
                new ExternalResource(imageUrl));
        embedded.setType(Embedded.TYPE_IMAGE);
        getContent().addComponent(embedded);

    }

    @Override
    protected String getTestDescription() {
        return "Mini tutorial for https://vaadin.com/wiki/-/wiki/Main/Generating%20dynamic%20resources%20based%20on%20URI%20or%20parameters";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }
}

class DynamicImageRequestHandler implements RequestHandler {

    public static final String IMAGE_URL = "myimage.png";

    public boolean handleRequest(Application application,
            WrappedRequest request, WrappedResponse response)
            throws IOException {
        String pathInfo = request.getRequestPathInfo();
        if (("/" + IMAGE_URL).equals(pathInfo)) {
            // Create an image, draw the "text" parameter to it and output it to
            // the browser.
            String text = request.getParameter("text");
            BufferedImage bi = new BufferedImage(100, 30,
                    BufferedImage.TYPE_3BYTE_BGR);
            bi.getGraphics().drawChars(text.toCharArray(), 0, text.length(),
                    10, 20);
            response.setContentType("image/png");
            ImageIO.write(bi, "png", response.getOutputStream());

            return true;
        }
        // If the URL did not match our image URL, let the other request
        // handlers handle it
        return false;
    }
}
