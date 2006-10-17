/* *************************************************************************
 
                               Enably Toolkit 

               Development of Browser User Intarfaces Made Easy

                    Copyright (C) 2000-2006 IT Mill Ltd
                     
   *************************************************************************

   This product is distributed under commercial license that can be found
   from the product package on license/license.txt. Use of this product might 
   require purchasing a commercial license from IT Mill Ltd. For guidelines 
   on usage, see license/licensing-guidelines.html

   *************************************************************************
   
   For more information, contact:
   
   IT Mill Ltd                           phone: +358 2 4802 7180
   Ruukinkatu 2-4                        fax:   +358 2 4802 7181
   20540, Turku                          email:  info@enably.com
   Finland                               company www: www.enably.com
   
   Primary source for information and releases: www.enably.com

   ********************************************************************** */

package com.enably.tk.ui;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Map;

import com.enably.tk.terminal.PaintException;
import com.enably.tk.terminal.PaintTarget;
import com.enably.tk.terminal.UploadStream;

import java.io.IOException;
import java.io.OutputStream;

/** Component for client file uploading.
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class Upload extends AbstractComponent implements Component.Focusable {

	/** Upload buffer size. */
	private static final int BUFFER_SIZE = 64 * 1024; // 64k

	/** Should the field be focused on next repaint */
	private boolean focus = false;

	/** The tab order number of this field */
	private int tabIndex = 0;

	/** The output of the upload is redirected to this receiver. */
	private Receiver receiver;
	
	private long focusableId = -1;

    /* TODO: Add a default constructor, receive to temp file. */
    
	/** Creates a new instance of Upload that redirects the 
	 * uploaded data to given stream. 
	 * 
	 */
	public Upload(String caption, Receiver uploadReceiver) {
		this.focusableId = Window.getNewFocusableId(this);
		setCaption(caption);
		receiver = uploadReceiver;
	}

	/** Get component type.
	 * @return Component type as string.
	 */
	public String getTag() {
		return "upload";
	}

	/** Invoked when the value of a variable has changed.  */
	public void changeVariables(Object source, Map variables) {

		// Check the variable name
		if (!variables.containsKey("stream"))
			return;

		// Get the upload stream    
		UploadStream upload = (UploadStream) variables.get("stream");

		// Get file properties
		String filename = upload.getContentName();
		String type = upload.getContentType();

		// Get the output target stream
		OutputStream out = receiver.receiveUpload(filename, type);
		if (out == null)
			throw new RuntimeException("Error getting outputstream from upload receiver");

		InputStream in = upload.getStream();
		if (null==in) {
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

	/** Paint the content of this component.
	 * @param target Target to paint the content on.
	 * @throws PaintException The paint operation failed.
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

	/** Notify all upload listeners */
	private void notifyListeners() {

	}

	/** Interface that must be implemented by the upload receivers. 
	 * @author IT Mill Ltd.
	 * @version @VERSION@
	 * @since 3.0
	 */
	public interface Receiver {

		/** Invoked when a new upload arrives. 
		 * @param filename The desired filename of the upload, usually as specified by the client.
		 * @param MIMEType The MIME type of the uploaded file. 
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
			UPLOAD_FINISHED_METHOD =
				FinishedListener.class.getDeclaredMethod(
					"uploadFinished",
					new Class[] { FinishedEvent.class });
			UPLOAD_FAILED_METHOD =
				FailedListener.class.getDeclaredMethod(
					"uploadFailed",
					new Class[] { FailedEvent.class });
			UPLOAD_SUCCEEDED_METHOD =
				SucceededListener.class.getDeclaredMethod(
					"uploadSucceeded",
					new Class[] { SucceededEvent.class });
		} catch (java.lang.NoSuchMethodException e) {
			// This should never happen
			throw new java.lang.RuntimeException("Internal error");
		}
	}

	/** Upload.Received event is sent when the upload receives a file,
	 * regardless if the receival was successfull.
	 * @author IT Mill Ltd.
	 * @version @VERSION@
	 * @since 3.0
	 */
	public class FinishedEvent extends Component.Event {

		/**
         * Serial generated by eclipse.
         */
        private static final long serialVersionUID = 3257288015385670969L;

        /** Length of the received file. */
		private long length;

		/** MIME type of the received file. */
		private String type;

		/** Received file name */
		private String filename;

		public FinishedEvent(
			Upload source,
			String filename,
			String MIMEType,
			long length) {
			super(source);
			this.type = MIMEType;
			this.filename = filename;
			this.length = length;
		}

		/** Upload where the event occurred
		 * @return Source of the event.
		 */
		public Upload getUpload() {
			return (Upload) getSource();
		}
		/**
		 * Returns the filename.
		 */
		public String getFilename() {
			return filename;
		}

		/**
		 * Returns the length.
		 */
		public long getLength() {
			return length;
		}

		/**
		 * Returns the type.
		 */
		public String getMIMEType() {
			return type;
		}

	}

	/** Upload.Interrupted event is sent when the upload is received, but the
	 * reception is interrupted for some reason.
	 * @author IT Mill Ltd.
	 * @version @VERSION@
	 * @since 3.0
	 */
	public class FailedEvent extends FinishedEvent {

		/**
         * Serial generated by eclipse.
         */
        private static final long serialVersionUID = 3833746590157386293L;

        public FailedEvent(
			Upload source,
			String filename,
			String MIMEType,
			long length) {
			super(source, filename, MIMEType, length);
		}

	}

	/** Upload.Success event is sent when the upload is received successfully.
	 * @author IT Mill Ltd.
	 * @version @VERSION@
	 * @since 3.0
	 */
	public class SucceededEvent extends FinishedEvent {

		/**
         * Serial generated by eclipse.
         */
        private static final long serialVersionUID = 3256445798169524023L;

        public SucceededEvent(
			Upload source,
			String filename,
			String MIMEType,
			long length) {
			super(source, filename, MIMEType, length);
		}

	}

	/** Receives events when the uploads are ready.
	 * @author IT Mill Ltd.
	 * @version @VERSION@
	 * @since 3.0
	 */
	public interface FinishedListener {

		/** Upload has finished.
		 * @param event Upload finished event.
		 */
		public void uploadFinished(FinishedEvent event);
	}

	/** Receives events when the uploads are finished, but unsuccessfull. 
	 * @author IT Mill Ltd.
		 * @version @VERSION@
	 * @since 3.0
	 */
	public interface FailedListener {

		/** Upload has finished unsuccessfully.
		 * @param event Upload failed event.
		 */
		public void uploadFailed(FailedEvent event);
	}

	/** Receives events when the uploads are successfully finished.
	 * @author IT Mill Ltd.
		 * @version @VERSION@
	 * @since 3.0
	 */
	public interface SucceededListener {

		/** Upload successfull..
		 * @param event Upload successfull event.
		 */
		public void uploadSucceeded(SucceededEvent event);
	}

	/** Add upload received event listener
	 * @param listener Listener to be added.
	 */
	public void addListener(FinishedListener listener) {
		addListener(FinishedEvent.class, listener, UPLOAD_FINISHED_METHOD);
	}

	/** Remove upload received event listener
	 * @param listener Listener to be removed.
	 */
	public void removeListener(FinishedListener listener) {
		removeListener(FinishedEvent.class, listener, UPLOAD_FINISHED_METHOD);
	}

	/** Add upload interrupted event listener
	 * @param listener Listener to be added.
	 */
	public void addListener(FailedListener listener) {
		addListener(FailedEvent.class, listener, UPLOAD_FAILED_METHOD);
	}

	/** Remove upload interrupted event listener
	 * @param listener Listener to be removed.
	 */
	public void removeListener(FailedListener listener) {
		removeListener(FinishedEvent.class, listener, UPLOAD_FAILED_METHOD);
	}

	/** Add upload success event listener
	 * @param listener Listener to be added.
	 */
	public void addListener(SucceededListener listener) {
		addListener(SucceededEvent.class, listener, UPLOAD_SUCCEEDED_METHOD);
	}

	/** Remove upload success event listener
	 * @param listener Listener to be removed.
	 */
	public void removeListener(SucceededListener listener) {
		removeListener(SucceededEvent.class, listener, UPLOAD_SUCCEEDED_METHOD);
	}

	/** Emit upload received event. */
	protected void fireUploadReceived(
		String filename,
		String MIMEType,
		long length) {
		fireEvent(new Upload.FinishedEvent(this, filename, MIMEType, length));
	}

	/** Emit upload interrupted event. */
	protected void fireUploadInterrupted(
		String filename,
		String MIMEType,
		long length) {
		fireEvent(new Upload.FailedEvent(this, filename, MIMEType, length));
	}

	/** Emit upload success event. */
	protected void fireUploadSuccess(
		String filename,
		String MIMEType,
		long length) {
		fireEvent(new Upload.SucceededEvent(this, filename, MIMEType, length));
	}
	/** Returns the current receiver.
	 * @return Receiver
	 */
	public Receiver getReceiver() {
		return receiver;
	}

	/** Sets the receiver.
	 * @param receiver The receiver to set
	 */
	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}
	/**
	 * @see com.enably.tk.ui.Component.Focusable#focus()
	 */
	public void focus() {
		Window w = getWindow();
		if (w != null) {
			w.setFocusedComponent(this);
		}
	}
	
	/**
	 * @see com.enably.tk.ui.Component.Focusable#getTabIndex()
	 */
	public int getTabIndex() {
		return this.tabIndex;
	}
	
	/**
	 * @see com.enably.tk.ui.Component.Focusable#setTabIndex(int)
	 */
	public void setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;
	}
	
	/**
	 * @see com.enably.tk.ui.Component.Focusable#getFocusableId()
	 */
	public long getFocusableId() {
		return this.focusableId;
	}

}
