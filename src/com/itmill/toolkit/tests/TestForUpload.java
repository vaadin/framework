package com.itmill.toolkit.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.itmill.toolkit.terminal.StreamResource;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Layout;
import com.itmill.toolkit.ui.Link;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.ProgressIndicator;
import com.itmill.toolkit.ui.Upload;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Upload.FailedEvent;
import com.itmill.toolkit.ui.Upload.FailedListener;
import com.itmill.toolkit.ui.Upload.FinishedEvent;
import com.itmill.toolkit.ui.Upload.FinishedListener;
import com.itmill.toolkit.ui.Upload.StartedEvent;
import com.itmill.toolkit.ui.Upload.StartedListener;
import com.itmill.toolkit.ui.Upload.SucceededEvent;
import com.itmill.toolkit.ui.Upload.SucceededListener;

public class TestForUpload extends CustomComponent implements
		Upload.FinishedListener, FailedListener,SucceededListener, Upload.ProgressListener, StartedListener {

	Layout main = new OrderedLayout();

	Buffer buffer = new Buffer();

	Panel status = new Panel("Uploaded file:");

	private Upload up;

	private Label l;
	
	private ProgressIndicator pi = new ProgressIndicator();

	public TestForUpload() {
		setCompositionRoot(main);
		main.addComponent( new Label(
						"This is a simple test for upload application. "
								+ "Upload should work with big files and concurrent "
								+ "requests should not be blocked. Button 'b' reads "
								+ "current state into label below it. TODO make "
								+ "streaming example/test where upload contents "
								+ "is read but not saved and memory consumption is "
								+ "verified low. TODO make test where contents is "
								+ "written to disk and verifiy low memory consumption."));
		
		main.addComponent(new Label("Clicking on button b updates information about upload components status."));

		up = new Upload("Upload", buffer);
		up.setImmediate(true);
		up.addListener((FinishedListener)this);
		up.addListener((FailedListener) this);
		up.addListener((SucceededListener) this);
		up.addListener((StartedListener) this);
		
		
		up.setProgressListener(this);

		Button b = new Button("b", this, "readState");

		main.addComponent(b);



		main.addComponent(up);
		l = new Label("Idle");
		main.addComponent(l);
		
		pi.setVisible(false);
		pi.setPollingInterval(1000);
		main.addComponent(pi);

		status.setVisible(false);
		main.addComponent(status);


		Button restart = new Button("R");
		restart.addListener(new Button.ClickListener() {

			public void buttonClick(ClickEvent event) {
				getApplication().close();
			}
		});
		main.addComponent(restart);
		

	}

	public void readState() {
		StringBuffer sb = new StringBuffer();

		if (up.isUploading()) {
			sb.append("Uploading...");
			sb.append(up.getBytesRead());
			sb.append("/");
			sb.append(up.getUploadSize());
			sb.append(" ");
			sb.append(Math.round(100 * up.getBytesRead()
					/ (double) up.getUploadSize()));
			sb.append("%");
		} else {
			sb.append("Idle");
		}
		l.setValue(sb.toString());
	}

	public void uploadFinished(FinishedEvent event) {
		status.removeAllComponents();
		if (buffer.getStream() == null)
			status.addComponent(new Label(
					"Upload finished, but output buffer is null!!"));
		else {
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
			if (outputBuffer == null)
				return null;
			return new ByteArrayInputStream(outputBuffer.toByteArray());
		}

		/**
		 * @see com.itmill.toolkit.ui.Upload.Receiver#receiveUpload(String,
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

	public void uploadFailed(FailedEvent event) {
		System.out.println(event);
		
		System.out.println(event.getSource());
		
	}

	public void uploadSucceeded(SucceededEvent event) {
		pi.setVisible(false);
		l.setValue("Finished upload, idle");
		System.out.println(event);
	}
	
	public void updateProgress(long readBytes, long contentLenght) {
		pi.setValue(new Float(readBytes/(float)contentLenght));
	}

	public void uploadStarted(StartedEvent event) {
		pi.setVisible(true);
		l.setValue("Started uploading file " + event.getFilename());
	}

}
