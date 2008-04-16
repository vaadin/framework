/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.tests.magi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.itmill.toolkit.terminal.FileResource;
import com.itmill.toolkit.ui.*;

public class MyUploader extends CustomComponent
implements Upload.SucceededListener, Upload.FailedListener, Upload.Receiver {
    Panel root;         // Root element for contained components.
    Panel imagePanel;   // Panel that contains the uploaded image.
    File  file;         // File to write to.

    MyUploader() {
        root = new Panel("My Upload Component");
        setCompositionRoot(root);

        // Create the Upload component.
        final Upload upload = new Upload("Upload the file here", this);

        // Listen for Upload.SucceededEvent and FailedEvent events.
        upload.addListener((Upload.SucceededListener) this);
        upload.addListener((Upload.FailedListener) this);

        root.addComponent(upload);
        root.addComponent(new Label("Click 'Browse' to select a file and then click 'Upload'."));

        // Create a panel for displaying the uploaded file (image).
        imagePanel = new Panel("Uploaded image");
        imagePanel.addComponent(new Label("No image uploaded yet"));
        root.addComponent(imagePanel);
    }

    // Callback method to begin receiving the upload.
    public OutputStream receiveUpload(String filename, String MIMEType) {
        FileOutputStream fos = null; // Output stream to write to.
        file = new File("/tmp/uploads/" + filename);
        try {
            // Open the file for writing.
            fos = new FileOutputStream(file);
        } catch (final java.io.FileNotFoundException e) {
            // Error while opening the file. Not reported here.
            e.printStackTrace();
            return null;
        }

        return fos; // Return the output stream to write to
    }

    // This is called if the upload is finished successfully.
    public void uploadSucceeded(Upload.SucceededEvent event) {
        // Log the upload on screen.
        root.addComponent(new Label("File " + event.getFilename()
                + " of type '" + event.getMIMEType() + "' uploaded."));
        
        // Display the uploaded file in the image panel.
        final FileResource imageResource = new FileResource(file, getApplication());
        imagePanel.removeAllComponents();
        imagePanel.addComponent(new Embedded("", imageResource));
    }

    // This is called if the upload fails.
    public void uploadFailed(Upload.FailedEvent event) {
        // Log the failure on screen.
        root.addComponent(new Label("Uploading " + event.getFilename()
                + " of type '" + event.getMIMEType() + "' failed."));
    }
}
