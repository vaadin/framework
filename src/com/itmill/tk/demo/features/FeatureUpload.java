/* *************************************************************************
 
   								Millstone(TM) 
   				   Open Sourced User Interface Library for
   		 		       Internet Development with Java

             Millstone is a registered trademark of IT Mill Ltd
                  Copyright (C) 2000-2005 IT Mill Ltd
                     
   *************************************************************************

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   license version 2.1 as published by the Free Software Foundation.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

   *************************************************************************
   
   For more information, contact:
   
   IT Mill Ltd                           phone: +358 2 4802 7180
   Ruukinkatu 2-4                        fax:  +358 2 4802 7181
   20540, Turku                          email: info@itmill.com
   Finland                               company www: www.itmill.com
   
   Primary source for MillStone information and releases: www.millstone.org

   ********************************************************************** */

package com.itmill.tk.demo.features;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.itmill.tk.terminal.StreamResource;
import com.itmill.tk.ui.Component;
import com.itmill.tk.ui.Label;
import com.itmill.tk.ui.Link;
import com.itmill.tk.ui.OrderedLayout;
import com.itmill.tk.ui.Panel;
import com.itmill.tk.ui.Upload;
import com.itmill.tk.ui.Upload.FinishedEvent;

public class FeatureUpload
	extends Feature
	implements Upload.FinishedListener {

	Buffer buffer = new Buffer();

	Panel status = new Panel("Uploaded file:");

	public FeatureUpload() {
		super();
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		// Example panel
		Panel show = new Panel("Upload component");

		Upload up = new Upload("Upload a file:", buffer);
		up.addListener(this);

		show.addComponent(up);
		status.setVisible(false);
		l.addComponent(status);
		l.addComponent(show);

		// Properties
		PropertyPanel p = new PropertyPanel(up);
		l.addComponent(p);

			return l;
	}

	protected String getExampleSrc() {
		return "Upload u = new Upload(\"Upload a file:\", uploadReceiver);\n\n"
			+ "public class uploadReceiver \n"
			+ "implements Upload.receiver, Upload.FinishedListener { \n"
			+ "\n"
			+ " java.io.File file;\n"
			+ " java.io.FileOutputStream fos;\n"
			+ " public uploadReceiver() {\n"
			+ " }";

	}

	protected String getDescriptionXHTML() {
		return "This demonstrates the use of the Upload component together with the Link component. "
			+ "This implementation does not actually store the file to disk, it only keeps it in a buffer. "
			+ "The example given on the example-tab on the other hand stores the file to disk and binds the link to that file.<br/>"
			+ "<br/>On the demo tab you can try out how the different properties affect the presentation of the component.";
	}

	protected String getImage() {
		return "filetransfer.jpg";
	}

	protected String getTitle() {
		return "Upload";
	}

	public void uploadFinished(FinishedEvent event) {
		status.removeAllComponents();
		if (buffer.getStream() == null)
			status.addComponent(
				new Label("Upload finished, but output buffer is null!!"));
		else {
			status.addComponent(
				new Label(
					"<b>Name:</b> " + event.getFilename(),
					Label.CONTENT_XHTML));
			status.addComponent(
				new Label(
					"<b>Mimetype:</b> " + event.getMIMEType(),
					Label.CONTENT_XHTML));
			status.addComponent(
				new Label(
					"<b>Size:</b> " + event.getLength() + " bytes.",
					Label.CONTENT_XHTML));

			status.addComponent(
				new Link(
					"Download " + buffer.getFileName(),
					new StreamResource(
						buffer,
						buffer.getFileName(),
						getApplication())));
						
			status.setVisible(true);
		}
	}

	public class Buffer
		implements StreamResource.StreamSource, Upload.Receiver {
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
		 * @see com.itmill.tk.ui.Upload.Receiver#receiveUpload(String, String)
		 */
		public OutputStream receiveUpload(String filename, String MIMEType) {
			fileName = filename;
			mimeType = MIMEType;
			outputBuffer = new ByteArrayOutputStream();
			return outputBuffer;
		}

		/**
		 * Returns the fileName.
		 * @return String
		 */
		public String getFileName() {
			return fileName;
		}

		/**
		 * Returns the mimeType.
		 * @return String
		 */
		public String getMimeType() {
			return mimeType;
		}

	}
}