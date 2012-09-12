package com.vaadin.tests.resources;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.vaadin.server.DynamicConnectorResource;
import com.vaadin.server.WrappedRequest;
import com.vaadin.server.WrappedResponse;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Embedded;

public class ConnectorResourceTest extends AbstractTestUI {

    private static final String DYNAMIC_IMAGE_NAME = "requestImage.png";

    @Override
    protected void setup(WrappedRequest request) {
        addComponent(new Embedded(DYNAMIC_IMAGE_NAME,
                new DynamicConnectorResource(this, DYNAMIC_IMAGE_NAME)));
        addComponent(new Embedded("Dynamic text", new DynamicConnectorResource(
                this, DYNAMIC_IMAGE_NAME, new HashMap<String, String>() {
                    {
                        put("text", "Dynamic%20text");
                    }
                })));
    }

    @Override
    protected String getTestDescription() {
        // Adding description would break screenshots -> too lazy to change
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf("9419");
    }

    @Override
    public boolean handleConnectorRequest(WrappedRequest request,
            WrappedResponse response, String path) throws IOException {
        if (DYNAMIC_IMAGE_NAME.equals(path)) {
            // Create an image, draw the "text" parameter to it and output it to
            // the browser.
            String text = request.getParameter("text");
            if (text == null) {
                text = DYNAMIC_IMAGE_NAME;
            }
            BufferedImage bi = getImage(text);
            response.setContentType("image/png");
            ImageIO.write(bi, "png", response.getOutputStream());

            return true;
        } else {
            return super.handleConnectorRequest(request, response, path);
        }
    }

    private BufferedImage getImage(String text) {
        BufferedImage bi = new BufferedImage(150, 30,
                BufferedImage.TYPE_3BYTE_BGR);
        bi.getGraphics()
                .drawChars(text.toCharArray(), 0, text.length(), 10, 20);
        return bi;
    }

}
