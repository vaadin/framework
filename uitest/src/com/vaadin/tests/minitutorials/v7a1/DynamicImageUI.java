package com.vaadin.tests.minitutorials.v7a1;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Image;

public class DynamicImageUI extends AbstractTestUI {
    public static final String IMAGE_URL = "myimage.png";

    private final RequestHandler requestHandler = new RequestHandler() {
        @Override
        public boolean handleRequest(VaadinSession session,
                VaadinRequest request, VaadinResponse response)
                throws IOException {
            if (("/" + IMAGE_URL).equals(request.getPathInfo())) {
                // Create an image, draw the "text" parameter to it and output
                // it to the browser.
                String text = request.getParameter("text");
                BufferedImage bi = new BufferedImage(100, 30,
                        BufferedImage.TYPE_3BYTE_BGR);
                bi.getGraphics().drawChars(text.toCharArray(), 0,
                        text.length(), 10, 20);
                response.setContentType("image/png");
                ImageIO.write(bi, "png", response.getOutputStream());

                return true;
            }
            // If the URL did not match our image URL, let the other request
            // handlers handle it
            return false;
        }
    };

    @Override
    public void setup(VaadinRequest request) {
        Resource resource = new ExternalResource(IMAGE_URL + "?text=Hello!");

        getSession().addRequestHandler(requestHandler);

        // Add an image using the resource
        Image image = new Image("A dynamically generated image", resource);

        addComponent(image);
    }

    @Override
    public void detach() {
        super.detach();

        // Clean up
        getSession().removeRequestHandler(requestHandler);
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
