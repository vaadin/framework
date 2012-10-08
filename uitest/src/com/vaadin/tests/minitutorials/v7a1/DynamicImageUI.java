package com.vaadin.tests.minitutorials.v7a1;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinServiceSession;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Embedded;

public class DynamicImageUI extends AbstractTestUI {

    @Override
    public void setup(VaadinRequest request) {
        // Add the request handler that handles our dynamic image
        getSession().addRequestHandler(new DynamicImageRequestHandler());

        // Create a URL that we can handle in DynamicImageRequestHandler
        String imageUrl = "app://" + DynamicImageRequestHandler.IMAGE_URL
                + "?text=Hello!";

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

    @Override
    public boolean handleRequest(VaadinServiceSession session,
            VaadinRequest request, VaadinResponse response) throws IOException {
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
