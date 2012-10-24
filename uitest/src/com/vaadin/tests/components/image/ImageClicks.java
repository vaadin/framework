package com.vaadin.tests.components.image;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.imageio.ImageIO;

import com.vaadin.event.MouseEvents.ClickEvent;
import com.vaadin.event.MouseEvents.ClickListener;
import com.vaadin.server.StreamResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;

public class ImageClicks extends TestBase {

    private int clickCounter = 0;

    @Override
    protected void setup() {

        final Label label = new Label(labelText());
        addComponent(label);

        final Image image = new Image();
        final MyImageSource imageSource = new MyImageSource();
        final StreamResource imageResource = new StreamResource(imageSource,
                "testimage.png");
        image.setSource(imageResource);
        image.addClickListener(new ClickListener() {

            @Override
            public void click(ClickEvent event) {
                ++clickCounter;
                imageResource.setFilename("testimage.png?"
                        + new Date().getTime());
                image.markAsDirty();
                label.setValue(labelText());
            }

        });
        addComponent(image);

    }

    private String labelText() {
        StringBuilder sb = new StringBuilder();
        sb.append("Image clicked ");
        sb.append(clickCounter);
        sb.append(" times.");
        return sb.toString();
    }

    @Override
    protected String getDescription() {
        return "Each click on the dynamically generated image should update the image and add another black square";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

    public class MyImageSource implements StreamResource.StreamSource {
        public MyImageSource() {
        }

        int intervalPos(int pos, int resolution, int cells) {
            return (int) Math.round(pos * resolution / (cells * 1.0));
        }

        @Override
        public InputStream getStream() {
            // Create an image and draw some background on it.
            BufferedImage image = new BufferedImage(300, 300,
                    BufferedImage.TYPE_INT_RGB);
            Graphics drawable = image.getGraphics();

            // Background
            drawable.setColor(Color.white);
            drawable.fillRect(0, 0, 300, 300);

            final int rows = 4;
            final int cols = 4;

            // Grid
            for (int row = 0; row < rows; row++) {
                int gridy = intervalPos(row, 300, rows);
                int gridynext = intervalPos(row + 1, 300, rows);

                // Horizontal grid line
                if (row > 0) {
                    drawable.setColor(Color.lightGray);
                    drawable.drawLine(0, gridy, 300 - 1, gridy);
                }

                for (int col = 0; col < cols; col++) {
                    int gridx = intervalPos(col, 300, cols);
                    int gridxnext = intervalPos(col + 1, 300, cols);

                    // Vertical grid line
                    if (row == 0 && col > 0) {
                        drawable.setColor(Color.lightGray);
                        drawable.drawLine(gridx, 0, gridx, 300 - 1);
                    }

                    // Cell
                    int cellIndex = col + row * cols;
                    if (clickCounter > cellIndex) {
                        drawable.setColor(Color.black);
                    } else {
                        drawable.setColor(Color.white);
                    }

                    drawable.fillRect(gridx + 1, gridy + 1, gridxnext - gridx
                            - 1, gridynext - gridy - 1);
                }
            }

            try {
                // Write the image to a buffer.
                ByteArrayOutputStream imagebuffer = new ByteArrayOutputStream();
                ImageIO.write(image, "png", imagebuffer);

                // Return a stream from the buffer.
                ByteArrayInputStream istream = new ByteArrayInputStream(
                        imagebuffer.toByteArray());
                return istream; // new DownloadStream (istream,null,null);
            } catch (IOException e) {
                return null;
            }
        }

    }
}
