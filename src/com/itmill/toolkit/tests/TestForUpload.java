package com.itmill.toolkit.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.terminal.StreamResource;
import com.itmill.toolkit.ui.AbstractField;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.CustomComponent;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Layout;
import com.itmill.toolkit.ui.Link;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.ProgressIndicator;
import com.itmill.toolkit.ui.Select;
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
		Upload.FinishedListener, FailedListener, SucceededListener,
		Upload.ProgressListener, StartedListener {

	Layout main = new OrderedLayout();

	Buffer buffer = new MemooryBuffer();

	Panel status = new Panel("Uploaded file:");

	private final Upload up;

	private final Label l;

	private final ProgressIndicator pi = new ProgressIndicator();

	private final Label memoryStatus;

	private final Select uploadBufferSelector;

	public TestForUpload() {
		setCompositionRoot(this.main);
		this.main.addComponent(new Label(
				"This is a simple test for upload application. "
						+ "Upload should work with big files and concurrent "
						+ "requests should not be blocked. Button 'b' reads "
						+ "current state into label below it. Memory receiver "
						+ "streams upload contents into memory. You may track"
						+ "consumption."
						+ "tempfile receiver writes upload to file and "
						+ "should have low memory consumption."));

		this.main
				.addComponent(new Label(
						"Clicking on button b updates information about upload components status or same with garbage collector."));

		this.up = new Upload("Upload", this.buffer);
		this.up.setImmediate(true);
		this.up.addListener((FinishedListener) this);
		this.up.addListener((FailedListener) this);
		this.up.addListener((SucceededListener) this);
		this.up.addListener((StartedListener) this);

		this.up.setProgressListener(this);

		Button b = new Button("b", this, "readState");

		Button c = new Button("b with gc", this, "gc");

		this.main.addComponent(b);
		this.main.addComponent(c);

		this.uploadBufferSelector = new Select("Receiver type");
		this.uploadBufferSelector.setColumns(6);
		this.uploadBufferSelector.setImmediate(true);
		this.uploadBufferSelector.addItem("memory");
		this.uploadBufferSelector.setValue("memory");
		this.uploadBufferSelector.addItem("tempfile");
		this.uploadBufferSelector
				.addListener(new AbstractField.ValueChangeListener() {
					public void valueChange(ValueChangeEvent event) {
						setBuffer();
					}
				});
		this.main.addComponent(this.uploadBufferSelector);

		this.main.addComponent(this.up);
		this.l = new Label("Idle");
		this.main.addComponent(this.l);

		this.pi.setVisible(false);
		this.pi.setPollingInterval(1000);
		this.main.addComponent(this.pi);

		this.memoryStatus = new Label();
		this.main.addComponent(this.memoryStatus);

		this.status.setVisible(false);
		this.main.addComponent(this.status);

		Button restart = new Button("R");
		restart.addListener(new Button.ClickListener() {

			public void buttonClick(ClickEvent event) {
				getApplication().close();
			}
		});
		this.main.addComponent(restart);

	}

	private void setBuffer() {
		String id = (String) this.uploadBufferSelector.getValue();
		if ("memory".equals(id)) {
			this.buffer = new MemooryBuffer();
		} else if ("tempfile".equals(id)) {
			this.buffer = new TmpFileBuffer();
		}
		this.up.setReceiver(this.buffer);
	}

	public void gc() {
		Runtime.getRuntime().gc();
		readState();
	}

	public void readState() {
		StringBuffer sb = new StringBuffer();

		if (this.up.isUploading()) {
			sb.append("Uploading...");
			sb.append(this.up.getBytesRead());
			sb.append("/");
			sb.append(this.up.getUploadSize());
			sb.append(" ");
			sb.append(Math.round(100 * this.up.getBytesRead()
					/ (double) this.up.getUploadSize()));
			sb.append("%");
		} else {
			sb.append("Idle");
		}
		this.l.setValue(sb.toString());
		refreshMemUsage();
	}

	public void uploadFinished(FinishedEvent event) {
		this.status.removeAllComponents();
		InputStream stream = this.buffer.getStream();
		if (stream == null) {
			this.status.addComponent(new Label(
					"Upload finished, but output buffer is null!!"));
		} else {
			this.status.addComponent(new Label("<b>Name:</b> "
					+ event.getFilename(), Label.CONTENT_XHTML));
			this.status.addComponent(new Label("<b>Mimetype:</b> "
					+ event.getMIMEType(), Label.CONTENT_XHTML));
			this.status.addComponent(new Label("<b>Size:</b> "
					+ event.getLength() + " bytes.", Label.CONTENT_XHTML));

			this.status.addComponent(new Link("Download "
					+ this.buffer.getFileName(), new StreamResource(
					this.buffer, this.buffer.getFileName(), getApplication())));

			this.status.setVisible(true);
		}
	}

	public interface Buffer extends StreamResource.StreamSource,
			Upload.Receiver {

		String getFileName();
	}

	public class MemooryBuffer implements Buffer {
		ByteArrayOutputStream outputBuffer = null;

		String mimeType;

		String fileName;

		public MemooryBuffer() {

		}

		public InputStream getStream() {
			if (this.outputBuffer == null) {
				return null;
			}
			return new ByteArrayInputStream(this.outputBuffer.toByteArray());
		}

		/**
		 * @see com.itmill.toolkit.ui.Upload.Receiver#receiveUpload(String,
		 *      String)
		 */
		public OutputStream receiveUpload(String filename, String MIMEType) {
			this.fileName = filename;
			this.mimeType = MIMEType;
			this.outputBuffer = new ByteArrayOutputStream();
			return this.outputBuffer;
		}

		/**
		 * Returns the fileName.
		 * 
		 * @return String
		 */
		public String getFileName() {
			return this.fileName;
		}

		/**
		 * Returns the mimeType.
		 * 
		 * @return String
		 */
		public String getMimeType() {
			return this.mimeType;
		}

	}

	public class TmpFileBuffer implements Buffer {
		String mimeType;

		String fileName;

		private File file;

		private FileInputStream stream;

		public TmpFileBuffer() {
			String tempFileName = "upload_tmpfile_"
					+ System.currentTimeMillis();
			try {
				this.file = File.createTempFile(tempFileName, null);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		public InputStream getStream() {
			if (this.file == null) {
				return null;
			}
			try {
				return new FileInputStream(this.file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * @see com.itmill.toolkit.ui.Upload.Receiver#receiveUpload(String,
		 *      String)
		 */
		public OutputStream receiveUpload(String filename, String MIMEType) {
			this.fileName = filename;
			this.mimeType = MIMEType;
			try {
				return new FileOutputStream(this.file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * Returns the fileName.
		 * 
		 * @return String
		 */
		public String getFileName() {
			return this.fileName;
		}

		/**
		 * Returns the mimeType.
		 * 
		 * @return String
		 */
		public String getMimeType() {
			return this.mimeType;
		}

	}

	public void uploadFailed(FailedEvent event) {
		System.out.println(event);

		System.out.println(event.getSource());

	}

	public void uploadSucceeded(SucceededEvent event) {
		this.pi.setVisible(false);
		this.l.setValue("Finished upload, idle");
		System.out.println(event);
		setBuffer();
	}

	public void updateProgress(long readBytes, long contentLenght) {
		this.pi.setValue(new Float(readBytes / (float) contentLenght));

		refreshMemUsage();
	}

	private void refreshMemUsage() {
		this.memoryStatus.setValue("Not available in Java 1.4");
		/*
		 * StringBuffer mem = new StringBuffer(); MemoryMXBean mmBean =
		 * ManagementFactory.getMemoryMXBean(); mem.append("Heap (M):");
		 * mem.append(mmBean.getHeapMemoryUsage().getUsed() / 1048576);
		 * mem.append(" |�Non-Heap (M):");
		 * mem.append(mmBean.getNonHeapMemoryUsage().getUsed() / 1048576);
		 * memoryStatus.setValue(mem.toString());
		 */
	}

	public void uploadStarted(StartedEvent event) {
		this.pi.setVisible(true);
		this.l.setValue("Started uploading file " + event.getFilename());
	}

}
