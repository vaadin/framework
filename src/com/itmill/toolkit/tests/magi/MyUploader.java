package com.itmill.toolkit.tests.magi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.itmill.toolkit.terminal.FileResource;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Embedded;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.Upload;

public class MyUploader extends CustomComponent implements
		Upload.FinishedListener {
	MyUploadReceiver uploadReceiver; /* Upload receiver object. */
	Panel root; /* Root element for contained components. */
	Panel imagePanel; /* Panel that contains the uploaded image. */

	/* Custom upload receiver that has to be implemented for Upload. */
	class MyUploadReceiver implements Upload.Receiver {
		java.io.File file; /* File to write to. */
		java.io.FileOutputStream fos; /* Output stream to write to. */

		public OutputStream receiveUpload(String filename, String MIMEType) {
			file = new File("/tmp/uploads/" + filename);
			try {
				/* Open the file for writing. */
				fos = new FileOutputStream(file);
			} catch (java.io.FileNotFoundException e) {
				return null; /*
								 * Error while opening the file. Not reported
								 * here.
								 */
			}

			return fos; /* Return the output stream. */
		}

		public File getFile() {
			return file;
		}
	}

	MyUploader() {
		root = new Panel("My Upload Component");
		setCompositionRoot(root);

		/* Create the upload receiver required by Upload. */
		uploadReceiver = new MyUploadReceiver();

		/* Create the Upload component. */
		Upload upload = new Upload("Upload", uploadReceiver);

		/* Listen for Upload.FinishedEvent events. */
		upload.addListener(this);

		root.addComponent(upload);
		root.addComponent(new Label(
				"Click 'Browse' to select a file and then click 'Upload'."));

		/* Create a panel for displaying the uploaded file (image). */
		imagePanel = new Panel("Uploaded image");
		imagePanel.addComponent(new Label("No image uploaded yet"));
		root.addComponent(imagePanel);
	}

	/* This is called when the upload is finished. */
	public void uploadFinished(Upload.FinishedEvent event) {
		/* Log the upload on screen. */
		root.addComponent(new Label("File " + event.getFilename()
				+ " of type '" + event.getMIMEType() + "' uploaded."));

		/* Display the uploaded file in the image panel. */
		FileResource imageResource = new FileResource(uploadReceiver.getFile(),
				getApplication());
		imagePanel.removeAllComponents();
		imagePanel.addComponent(new Embedded("", imageResource));
	}
}
