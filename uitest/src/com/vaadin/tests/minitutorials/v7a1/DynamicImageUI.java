package com.vaadin.tests.minitutorials.v7a1;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.vaadin.server.DynamicConnectorResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Image;

public class DynamicImageUI extends AbstractTestUI {
    public static final String IMAGE_URL = "myimage.png";

    @Override
    public void setup(VaadinRequest request) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("text", "Hello!");
        DynamicConnectorResource resource = new DynamicConnectorResource(this,
                IMAGE_URL, parameters);

        // Add an image using the resource
        Image image = new Image("A dynamically generated image", resource);

        addComponent(image);
    }

    @Override
    public boolean handleConnectorRequest(VaadinRequest request,
            VaadinResponse response, String path) throws IOException {
        if ((IMAGE_URL).equals(path)) {
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

    @Override
    protected String getTestDescription() {
        return "Mini tutorial for https://vaadin.com/wiki/-/wiki/Main/Generating%20dynamic%20resources%20based%20on%20URI%20or%20parameters";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }
}