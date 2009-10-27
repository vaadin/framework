/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.featurebrowser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.vaadin.terminal.StreamResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FinishedEvent;

public class FeatureUpload extends Feature implements Upload.FinishedListener {
    Buffer buffer = new Buffer();

    Panel status = new Panel("Uploaded file:");

    public FeatureUpload() {
        super();
    }

    @Override
    protected Component getDemoComponent() {

        final OrderedLayout l = new OrderedLayout();

        final Upload up = new Upload("Upload", buffer);
        up.setImmediate(true);
        up.addListener(this);

        status.setVisible(false);

        l.addComponent(up);
        l.addComponent(status);

        // Properties
        propertyPanel = new PropertyPanel(up);

        setJavadocURL("ui/Upload.html");

        return l;
    }

    @Override
    protected String getExampleSrc() {
        return "Upload u = new Upload(\"Upload a file:\", uploadReceiver);\n\n"
                + "public class uploadReceiver \n"
                + "implements Upload.receiver, Upload.FinishedListener { \n"
                + "\n" + " java.io.File file;\n"
                + " java.io.FileOutputStream fos;\n"
                + " public uploadReceiver() {\n" + " }";

    }

    @Override
    protected String getDescriptionXHTML() {
        return "This demonstrates the use of the Upload component together with the Link component. "
                + "This implementation does not actually store the file to disk, it only keeps it in a buffer. "
                + "The example given on the <em>Code Sample</em>-tab on the other hand stores the file to disk and binds the link to that file.";
    }

    @Override
    protected String getImage() {
        return "icon_demo.png";
    }

    @Override
    protected String getTitle() {
        return "Upload";
    }

    public void uploadFinished(FinishedEvent event) {
        status.removeAllComponents();
        if (buffer.getStream() == null) {
            status.addComponent(new Label(
                    "Upload finished, but output buffer is null!!"));
        } else {
            status
                    .addComponent(new Label("<b>Name:</b> "
                            + event.getFilename(), Label.CONTENT_XHTML));
            status.addComponent(new Label("<b>Mimetype:</b> "
                    + event.getMIMEType(), Label.CONTENT_XHTML));
            status.addComponent(new Label("<b>Size:</b> " + event.getLength()
                    + " bytes.", Label.CONTENT_XHTML));

            status.addComponent(new Link("Download " + buffer.getFileName(),
                    new StreamResource(buffer, buffer.getFileName(),
                            getApplication())));

            status.setVisible(true);
        }
    }

    public class Buffer implements StreamResource.StreamSource, Upload.Receiver {
        ByteArrayOutputStream outputBuffer = null;

        String mimeType;

        String fileName;

        public Buffer() {

        }

        public InputStream getStream() {
            if (outputBuffer == null) {
                return null;
            }
            return new ByteArrayInputStream(outputBuffer.toByteArray());
        }

        /**
         * @see com.vaadin.ui.Upload.Receiver#receiveUpload(String,
         *      String)
         */
        public OutputStream receiveUpload(String filename, String MIMEType) {
            fileName = filename;
            mimeType = MIMEType;
            outputBuffer = new ByteArrayOutputStream();
            return outputBuffer;
        }

        /**
         * Returns the fileName.
         * 
         * @return String
         */
        public String getFileName() {
            return fileName;
        }

        /**
         * Returns the mimeType.
         * 
         * @return String
         */
        public String getMimeType() {
            return mimeType;
        }

    }
}