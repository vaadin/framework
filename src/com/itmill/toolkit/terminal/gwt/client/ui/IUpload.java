package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IUpload extends FormPanel implements Paintable, ClickListener {
	
	FileUpload fu = new FileUpload();
	
	Panel panel = new FlowPanel();
	
	ApplicationConnection client;

	private String paintableId;

	private Button b;
	
	public IUpload() {
		super();
		setEncoding(FormPanel.ENCODING_MULTIPART);
	    setMethod(FormPanel.METHOD_POST);
		setWidget(panel);
		panel.add(new Label("UPLOAD component incomplete"));
		panel.add(fu);
		b = new Button("Upload");
		b.addClickListener(this);
		panel.add(b);
		
	    addFormHandler(new FormHandler() {
	      public void onSubmitComplete(FormSubmitCompleteEvent event) {
	    	  if(client != null) {
	    		  // request update
	    		  client.sendPendingVariableChanges();
	    	  }
	      }

	      public void onSubmit(FormSubmitEvent event) {
	        if (fu.getFilename().length() == 0) {
	          event.setCancelled(true);
	        }
	      }
	    });
	}

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		this.client = client;
		this.paintableId = uidl.getId();
		if(uidl.hasAttribute("caption"))
			b.setText(uidl.getStringAttribute("caption"));

	}

	public void onClick(Widget sender) {
		this.submit();
	}

	
}
