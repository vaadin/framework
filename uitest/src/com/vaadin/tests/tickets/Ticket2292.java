package com.vaadin.tests.tickets;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.vaadin.server.DownloadStream;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Link;

public class Ticket2292 extends com.vaadin.server.LegacyApplication implements
        RequestHandler {

    @Override
    public void init() {
        final LegacyWindow main = new LegacyWindow(getClass().getName()
                .substring(getClass().getName().lastIndexOf(".") + 1));
        setMainWindow(main);

        ExternalResource icon = new ExternalResource("./icon.png");
        main.addComponent(new Label(
                "Note, run with trailing slash in url to have a working icon. Icon is built by servlet with a slow method, so it will show the bug (components not firing requestLayout)"));
        Button b = new Button();
        main.addComponent(b);
        b.setIcon(icon);

        CheckBox checkBox = new CheckBox();
        main.addComponent(checkBox);
        checkBox.setIcon(icon);

        Link l = new Link("l", icon);
        main.addComponent(l);

        VaadinSession.getCurrent().addRequestHandler(this);
    }

    @Override
    public boolean handleRequest(VaadinSession session, VaadinRequest request,
            VaadinResponse response) throws IOException {
        String relativeUri = request.getPathInfo();

        if (!relativeUri.contains("icon.png")) {
            return false;
        }

        // be slow to show bug
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

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
        drawable.drawString("Tex", 75, 100);

        try {
            // Write the image to a buffer.
            ByteArrayOutputStream imagebuffer = new ByteArrayOutputStream();
            ImageIO.write(image, "png", imagebuffer);

            // Return a stream from the buffer.
            ByteArrayInputStream istream = new ByteArrayInputStream(
                    imagebuffer.toByteArray());
            new DownloadStream(istream, null, null).writeResponse(request,
                    response);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
