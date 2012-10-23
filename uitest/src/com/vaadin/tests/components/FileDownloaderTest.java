package com.vaadin.tests.components;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.vaadin.server.ClassResource;
import com.vaadin.server.ConnectorResource;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.tests.components.embedded.EmbeddedPdf;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;

public class FileDownloaderTest extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        List<Class<? extends Component>> components = new ArrayList<Class<? extends Component>>();
        components.add(Button.class);
        components.add(NativeButton.class);
        components.add(CssLayout.class);
        components.add(Label.class);

        // Resource resource = new ExternalResource(
        // "https://vaadin.com/download/prerelease/7.0/7.0.0/7.0.0.beta1/vaadin-all-7.0.0.beta1.zip");
        // addComponents(resource, components);
        // resource = new ExternalResource(
        // "https://vaadin.com/download/book-of-vaadin/current/pdf/book-of-vaadin.pdf");
        // addComponents(resource, components);
        ConnectorResource resource;
        resource = new StreamResource(new StreamResource.StreamSource() {

            @Override
            public InputStream getStream() {
                try {
                    BufferedImage img = getImage2("demo.png");
                    ByteArrayOutputStream imagebuffer = new ByteArrayOutputStream();
                    ImageIO.write(img, "png", imagebuffer);
                    Thread.sleep(5000);

                    return new ByteArrayInputStream(imagebuffer.toByteArray());
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }, "demo.png");
        addComponents("Dynamic image", resource, components);
        try {
            File hugeFile = File.createTempFile("huge", ".txt");
            hugeFile.deleteOnExit();
            BufferedOutputStream os = new BufferedOutputStream(
                    new FileOutputStream(hugeFile));
            int writeAtOnce = 1024 * 1024;
            byte[] b = new byte[writeAtOnce];
            for (int i = 0; i < 5l * 1024l * 1024l; i += writeAtOnce) {
                os.write(b);
            }
            os.close();
            resource = new FileResource(hugeFile);
            addComponents("Huge text file", resource, components);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // resource = new DynamicConnectorResource(this, "requestImage.png");
        // addComponents(resource, components);
        // resource = new ThemeResource("favicon.ico");
        // addComponents(resource, components);
        resource = new ClassResource(new EmbeddedPdf().getClass(), "test.pdf");
        addComponents("Class resource pdf", resource, components);
    }

    public void addComponents(String caption, ConnectorResource resource,
            List<Class<? extends Component>> components) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setCaption(caption);
        for (Class<? extends Component> cls : components) {
            try {
                AbstractComponent c = (AbstractComponent) cls.newInstance();
                c.setId(cls.getName());
                c.setCaption(cls.getName());
                c.setDescription(resource.getMIMEType() + " / "
                        + resource.getClass());
                c.setWidth("100px");
                c.setHeight("100px");

                layout.addComponent(c);

                FileDownloader.bind(c, resource);

                if (c instanceof Button) {
                    ((Button) c).addClickListener(new ClickListener() {

                        @Override
                        public void buttonClick(ClickEvent event) {
                        }
                    });
                }
            } catch (Exception e) {
                System.err.println("Could not instatiate " + cls.getName());
            }
        }
        addComponent(layout);
    }

    private static final String DYNAMIC_IMAGE_NAME = "requestImage.png";

    @Override
    public boolean handleConnectorRequest(VaadinRequest request,
            VaadinResponse response, String path) throws IOException {
        if (DYNAMIC_IMAGE_NAME.equals(path)) {
            // Create an image, draw the "text" parameter to it and output it to
            // the browser.
            String text = request.getParameter("text");
            if (text == null) {
                text = DYNAMIC_IMAGE_NAME;
            }
            BufferedImage bi = getImage(text);
            response.setContentType("image/png");
            response.setHeader("Content-Disposition", "attachment; filename=\""
                    + path + "\"");
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

    private BufferedImage getImage2(String text) {
        BufferedImage bi = new BufferedImage(200, 200,
                BufferedImage.TYPE_INT_RGB);
        bi.getGraphics()
                .drawChars(text.toCharArray(), 0, text.length(), 10, 20);
        return bi;
    }

    @Override
    protected String getTestDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
