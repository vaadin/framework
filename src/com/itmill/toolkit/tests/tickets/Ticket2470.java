package com.itmill.toolkit.tests.tickets;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.terminal.StreamResource;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Embedded;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class Ticket2470 extends Application {

    @Override
    public void init() {
        final Window main = new Window();
        setMainWindow(main);
        
        final Embedded embedded = new Embedded();
        embedded.setDescription("Click on the grid cells to switch them.");

        final MyImageSource imageSource = new MyImageSource();
        final StreamResource imageResource = new StreamResource(imageSource, "testimage.png", this);
        imageResource.setCacheTime(0);
        embedded.setSource(imageResource);
        
        main.addComponent(embedded);
        
        Button button = new Button ("Click to Update");
        button.addListener(new Button.ClickListener() {
           @Override
            public void buttonClick(ClickEvent event) {
               embedded.requestRepaint();
            } 
        });
        main.addComponent(button);
    }

    public class MyImageSource implements StreamResource.StreamSource {
        public MyImageSource() {
        }
        
        int intervalPos(int pos, int resolution, int cells) {
                return (int) Math.round(pos*resolution/(cells*1.0));
        }

        public InputStream getStream() {
            // Create an image and draw some background on it.
            BufferedImage image = new BufferedImage (640, 480, BufferedImage.TYPE_INT_RGB);
            Graphics drawable = image.getGraphics();
                
                // Background
                drawable.setColor(Color.white);
                drawable.fillRect(0, 0, 640, 480);
                
                final int rows = 10;
                final int cols = 10;

                // Grid
                for (int row=0; row<rows; row++) {
                        int gridy     = intervalPos(row,   480, rows);
                        int gridynext = intervalPos(row+1, 480, rows);
                        
                        // Horizontal grid line
                        if (row > 0) {
                                drawable.setColor(Color.lightGray);
                                drawable.drawLine(0, gridy, 640-1, gridy);
                        }
                        
                        for (int col=0; col<cols; col++) {
                                int gridx     = intervalPos(col,   640, cols);
                                int gridxnext = intervalPos(col+1, 640, cols);
                                
                                // Vertical grid line
                                if (row == 0 && col > 0) {
                                        drawable.setColor(Color.lightGray);
                                        drawable.drawLine(gridx, 0, gridx, 480-1);
                                }

                                // Cell
                                if (Math.random() < 0.5f) {
                                    drawable.setColor(Color.white);
                                } else {
                                    drawable.setColor(Color.black);
                                }
                                drawable.fillRect(gridx+1, gridy+1, gridxnext-gridx-1, gridynext-gridy-1);
                        }
                }
                
                try {
                    // Write the image to a buffer.
                    ByteArrayOutputStream imagebuffer = new ByteArrayOutputStream();
                    ImageIO.write(image, "png", imagebuffer);

                    // Return a stream from the buffer.
                    ByteArrayInputStream istream = new ByteArrayInputStream(imagebuffer.toByteArray());
                    return istream; //new DownloadStream (istream,null,null);
                } catch (IOException e) {
                    return null;
                }
                }

    }
}
