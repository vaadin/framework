/* *************************************************************************
 
 IT Mill Toolkit 

 Development of Browser User Interfaces Made Easy

 Copyright (C) 2000-2006 IT Mill Ltd
 
 *************************************************************************

 This product is distributed under commercial license that can be found
 from the product package on license.pdf. Use of this product might 
 require purchasing a commercial license from IT Mill Ltd. For guidelines 
 on usage, see licensing-guidelines.html

 *************************************************************************
 
 For more information, contact:
 
 IT Mill Ltd                           phone: +358 2 4802 7180
 Ruukinkatu 2-4                        fax:   +358 2 4802 7181
 20540, Turku                          email:  info@itmill.com
 Finland                               company www: www.itmill.com
 
 Primary source for information and releases: www.itmill.com

 ********************************************************************** */

package com.itmill.toolkit.ui;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Map;

import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;
import com.itmill.toolkit.terminal.UploadStream;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Component for client file uploading.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public class Upload extends AbstractComponent implements Component.Focusable {

	/**
	 * Upload buffer size.
	 */
	private static final int BUFFER_SIZE = 64 * 1024; // 64k

	/**
	 * Should the field be focused on next repaint?
	 */
	private boolean focus = false;

	/**
	 * The tab order number of this field.
	 */
	private int tabIndex = 0;

	/**
	 * The output of the upload is redirected to this receiver.
	 */
	private Receiver receiver;

	private long focusableId = -1;

	/* TODO: Add a default constructor, receive to temp file. */

	/**
	 * Creates a new instance of Upload that redirects the uploaded data to
	 * given stream.
	 * 
	 * @param caption
	 * @param uploadReceiver
	 */
	public Upload(String caption, Receiver uploadReceiver) {
		this.focusableId = Window.getNewFocusableId(this);
		setCaption(caption);
		receiver = uploadReceiver;
	}

	/**
	 * Gets the component type.
	 * 
	 * @return Component type as string.
	 */
	public String getTag() {
		return "upload";
	}

	/**
	 * Invoked when the value of a variable has changed.
	 * 
	 * @see com.itmill.toolkit.ui.AbstractComponent#changeVariables(java.lang.Object,
	 *      java.util.Map)
	 */
	public void changeVariables(Object source, Map variables) {

		// Checks the variable name
		if (!variables.containsKey("stream"))
			return;

		// Gets the upload stream
		UploadStream upload = (UploadStream) variables.get("stream");

		// Gets file properties
		String filename = upload.getContentName();
		String type = upload.getContentType();

		// Gets the output target stream
		OutputStream out = receiver.receiveUpload(filename, type);
		if (out == null)
			throw new RuntimeException(
					"Error getting outputstream from upload receiver");

		InputStream in = upload.getStream();
		if (null == in) {
			// No file, for instance non-existent filename in html upload
			fireUploadInterrupted(filename, type, 0);
			return;
		}
		byte buffer[] = new byte[BUFFER_SIZE];
		int bytesRead = 0;
		long totalBytes = 0;
		try {
			while ((bytesRead = in.read(buffer)) > 0) {
				out.write(buffer, 0, bytesRead);
				totalBytes += bytesRead;
			}

			// Download successfull
			out.close();
			fireUploadSuccess(filename, type, totalBytes);
			requestRepaint();

		} catch (IOException e) {

			// Download interrupted
			fireUploadInterrupted(filename, type, totalBytes);
		}
	}

	/**
	 * Paints the content of this component.
	 * 
	 * @param target
	 *            Target to paint the content on.
	 * @throws PaintException
	 *             if the paint operation failed.
	 */
	public void paintContent(PaintTarget target) throws PaintException {
		// The field should be focused
		if (focus)
			target.addAttribute("focus", true);

		// The tab ordering number
		if (this.tabIndex >= 0)
			target.addAttribute("tabindex", this.tabIndex);

		target.addUploadStreamVariable(this, "stream");
	}

	/**
	 * Interface that must be implemented by the upload receivers.
	 * 
	 * @author IT Mill Ltd.
	 * @version
	 * @VERSION@
	 * @since 3.0
	 */
	public interface Receiver {

		/**
		 * Invoked when a new upload arrives.
		 * 
		 * @param filename
		 *            the desired filename of the upload, usually as specified
		 *            by the client.
		 * @param MIMEType
		 *            the MIME type of the uploaded file.
		 * @return Stream to which the uploaded file should be written.
		 */
		public OutputStream receiveUpload(String filename, String MIMEType);
	}

	/* Upload events ************************************************ */

	private static final Method UPLOAD_FINISHED_METHOD;

	private static final Method UPLOAD_FAILED_METHOD;

	private static final Method UPLOAD_SUCCEEDED_METHOD;

	static {
		try {
			UPLOAD_FINISHED_METHOD = FinishedListener.class.getDeclaredMethod(
					"uploadFinished", new Class[] { FinishedEvent.class });
			UPLOAD_FAILED_METHOD = FailedListener.class.getDeclaredMethod(
					"uploadFailed", new Class[] { FailedEvent.class });
			UPLOAD_SUCCEEDED_METHOD = SucceededListener.class
					.getDeclaredMethod("uploadSucceeded",
							new Class[] { SucceededEvent.class });
		} catch (java.lang.NoSuchMethodException e) {
			// This should never happen
			throw new java.lang.RuntimeException("Internal error");
		}
	}

	/**
	 * Upload.Received event is sent when the upload receives a file, regardless
	 * if the receival was successfull.
	 * 
	 * @author IT Mill Ltd.
	 * @version
	 * @VERSION@
	 * @since 3.0
	 */
	public class FinishedEvent extends Component.Event {

		/**
		 * Serial generated by eclipse.
		 */
		private static final long serialVersionUID = 3257288015385670969L;

		/**
		 * Length of the received file.
		 */
		private long length;

		/**
		 * MIME type of the received file.
		 */
		private String type;

		/**
		 * Received file name.
		 */
		private String filename;

		/**
		 * 
		 * @param source
		 *            the source of the file.
		 * @param filename
		 *            the received file name.
		 * @param MIMEType
		 *            the MIME type of the received file.
		 * @param length
		 *            the length of the received file.
		 */
		public FinishedEvent(Upload source, String filename, String MIMEType,
				long length) {
			super(source);
			this.type = MIMEType;
			this.filename = filename;
			this.length = length;
		}

		/**
		 * Uploads where the event occurred.
		 * 
		 * @return the Source of the event.
		 */
		public Upload getUpload() {
			return (Upload) getSource();
		}

		/**
		 * Gets the file name.
		 * 
		 * @return the filename.
		 */
		public String getFilename() {
			return filename;
		}

		/**
		 * Gets the length of the file.
		 * 
		 * @return the length.
		 */
		public long getLength() {
			return length;
		}

		/**
		 * Gets the MIME Type of the file.
		 * 
		 * @return the MIME type.
		 */
		public String getMIMEType() {
			return type;
		}

	}

	/**
	 * Upload.Interrupted event is sent when the upload is received, but the
	 * reception is interrupted for some reason.
	 * 
	 * @author IT Mill Ltd.
	 * @version
	 * @VERSION@
	 * @since 3.0
	 */
	public class FailedEvent extends FinishedEvent {

		/**
		 * Serial generated by eclipse.
		 */
		private static final long serialVersionUID = 3833746590157386293L;

		/**
		 * 
		 * @param source
		 * @param filename
		 * @param MIMEType
		 * @param length
		 */
		public FailedEvent(Upload source, String filename, String MIMEType,
				long length) {
			super(source, filename, MIMEType, length);
		}

	}

	/**
	 * Upload.Success event is sent when the upload is received successfully.
	 * 
	 * @author IT Mill Ltd.
	 * @version
	 * @VERSION@
	 * @since 3.0
	 */
	public class SucceededEvent extends FinishedEvent {

		/**
		 * Serial generated by eclipse.
		 */
		private static final long serialVersionUID = 3256445798169524023L;

		/**
		 * 
		 * @param source
		 * @param filename
		 * @param MIMEType
		 * @param length
		 */
		public SucceededEvent(Upload source, String filename, String MIMEType,
				long length) {
			super(source, filename, MIMEType, length);
		}

	}

	/**
	 * Receives the events when the uploads are ready.
	 * 
	 * @author IT Mill Ltd.
	 * @version
	 * @VERSION@
	 * @since 3.0
	 */
	public interface FinishedListener {

		/**
		 * Upload has finished.
		 * 
		 * @param event
		 *            the Upload finished event.
		 */
		public void uploadFinished(FinishedEvent event);
	}

	/**
	 * Receives events when the uploads are finished, but unsuccessful.
	 * 
	 * @author IT Mill Ltd.
	 * @version
	 * @VERSION@
	 * @since 3.0
	 */
	public interface FailedListener {

		/**
		 * Upload has finished unsuccessfully.
		 * 
		 * @param event
		 *            the Upload failed event.
		 */
		public void uploadFailed(FailedEvent event);
	}

	/**
	 * Receives events when the uploads are successfully finished.
	 * 
	 * @author IT Mill Ltd.
	 * @version
	 * @VERSION@
	 * @since 3.0
	 */
	public interface SucceededListener {

		/**
		 * Upload successfull..
		 * 
		 * @param event
		 *            the Upload successfull event.
		 */
		public void uploadSucceeded(SucceededEvent event);
	}

	/**
	 * Adds the upload received event listener.
	 * 
	 * @param listener
	 *            the Listener to be added.
	 */
	public void addListener(FinishedListener listener) {
		addListener(FinishedEvent.class, listener, UPLOAD_FINISHED_METHOD);
	}

	/**
	 * Removes the upload received event listener.
	 * 
	 * @param listener
	 *            the Listener to be removed.
	 */
	public void removeListener(FinishedListener listener) {
		removeListener(FinishedEvent.class, listener, UPLOAD_FINISHED_METHOD);
	}

	/**
	 * Adds the upload interrupted event listener.
	 * 
	 * @param listener
	 *            the Listener to be added.
	 */
	public void addListener(FailedListener listener) {
		addListener(FailedEvent.class, listener, UPLOAD_FAILED_METHOD);
	}

	/**
	 * Removes the upload interrupted event listener.
	 * 
	 * @param listener
	 *            the Listener to be removed.
	 */
	public void removeListener(FailedListener listener) {
		removeListener(FinishedEvent.class, listener, UPLOAD_FAILED_METHOD);
	}

	/**
	 * Adds the upload success event listener.
	 * 
	 * @param listener
	 *            the Listener to be added.
	 */
	public void addListener(SucceededListener listener) {
		addListener(SucceededEvent.class, listener, UPLOAD_SUCCEEDED_METHOD);
	}

	/**
	 * Removes the upload success event listener.
	 * 
	 * @param listener
	 *            the Listener to be removed.
	 */
	public void removeListener(SucceededListener listener) {
		removeListener(SucceededEvent.class, listener, UPLOAD_SUCCEEDED_METHOD);
	}

	/**
	 * Emit upload received event.
	 * 
	 * @param filename
	 * @param MIMEType
	 * @param length
	 */
	protected void fireUploadReceived(String filename, String MIMEType,
			long length) {
		fireEvent(new Upload.FinishedEvent(this, filename, MIMEType, length));
	}

	/**
	 * Emits the upload interrupted event.
	 * 
	 * @param filename
	 * @param MIMEType
	 * @param length
	 */
	protected void fireUploadInterrupted(String filename, String MIMEType,
			long length) {
		fireEvent(new Upload.FailedEvent(this, filename, MIMEType, length));
	}

	/**
	 * Emits the upload success event.
	 * 
	 * @param filename
	 * @param MIMEType
	 * @param length
	 * 
	 */
	protected void fireUploadSuccess(String filename, String MIMEType,
			long length) {
		fireEvent(new Upload.SucceededEvent(this, filename, MIMEType, length));
	}

	/**
	 * Returns the current receiver.
	 * 
	 * @return the Receiver.
	 */
	public Receiver getReceiver() {
		return receiver;
	}

	/**
	 * Sets the receiver.
	 * 
	 * @param receiver
	 *            the receiver to set.
	 */
	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}

	/**
	 * Sets the focus to this component.
	 * 
	 * @see com.itmill.toolkit.ui.Component.Focusable#focus()
	 */
	public void focus() {
		Window w = getWindow();
		if (w != null) {
			w.setFocusedComponent(this);
		}
	}

	/**
	 * Gets the Tabulator index of this Focusable component.
	 * 
	 * @see com.itmill.toolkit.ui.Component.Focusable#getTabIndex()
	 */
	public int getTabIndex() {
		return this.tabIndex;
	}

	/**
	 * Sets the Tabulator index of this Focusable component.
	 * 
	 * @see com.itmill.toolkit.ui.Component.Focusable#setTabIndex(int)
	 */
	public void setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;
	}

	/**
	 * Gets the unique ID of focusable.
	 * 
	 * @see com.itmill.toolkit.ui.Component.Focusable#getFocusableId()
	 */
	public long getFocusableId() {
		return this.focusableId;
	}

}
