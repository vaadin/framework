package com.itmill.toolkit.tests.tickets;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import com.itmill.toolkit.terminal.DownloadStream;
import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CheckBox;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Link;
import com.itmill.toolkit.ui.Window;

public class Ticket2292 extends com.itmill.toolkit.Application {

    @Override
    public void init() {
        final Window main = new Window(getClass().getName().substring(
                getClass().getName().lastIndexOf(".") + 1));
        setMainWindow(main);

        ExternalResource icon = new ExternalResource("./icon.png");
        main
                .addComponent(new Label(
                        "Note, run with trailing slash in url to have a working icon. Icon is built by servlet with a slow method, so it will show the bug (components not firing requestLayout)"));
        Button b = new Button();
        main.addComponent(b);
        b.setIcon(icon);

        CheckBox checkBox = new CheckBox();
        main.addComponent(checkBox);
        checkBox.setIcon(icon);

        Link l = new Link("l", icon);
        main.addComponent(l);

    }

    @Override
    public DownloadStream handleURI(URL context, String relativeUri) {
        if (!relativeUri.contains("icon.png")) {
            return null;
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
            ByteArrayInputStream istream = new ByteArrayInputStream(imagebuffer
                    .toByteArray());
            return new DownloadStream(istream, null, null);
        } catch (IOException e) {
            return null;
        }
    }

}
