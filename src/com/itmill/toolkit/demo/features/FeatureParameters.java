/* *************************************************************************
 
                               IT Mill Toolkit 

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
   20540, Turku                          email:  info@itmill.com
   Finland                               company www: www.itmill.com
   
   Primary source for information and releases: www.itmill.com

   ********************************************************************** */

package com.itmill.toolkit.demo.features;

import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import com.itmill.toolkit.terminal.DownloadStream;
import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.terminal.ParameterHandler;
import com.itmill.toolkit.terminal.URIHandler;
import com.itmill.toolkit.ui.*;

public class FeatureParameters
	extends Feature
	implements URIHandler, ParameterHandler {

	private Label context = new Label();
	private Label relative = new Label();
	private Table params = new Table();

	public FeatureParameters() {
		super();
		params.addContainerProperty("Values", String.class, "");
	}

	protected Component getDemoComponent() {

		OrderedLayout l = new OrderedLayout();

		Label info =
			new Label(
				"To test this feature, try to "
					+ "add some get parameters to URL. For example if you have "
					+ "the feature browser installed in your local host, try url: ");
		info.setCaption("Usage info");
		l.addComponent(info);
		try {
			URL u1 = new URL(getApplication().getURL(),"test/uri?test=1&test=2");
			URL u2 = new URL(getApplication().getURL(),"foo/bar?mary=john&count=3");
			
			l.addComponent(
				new Link(u1.toString(),new ExternalResource(u1)));
			l.addComponent(new Label("Or this: "));
			l.addComponent(
				new Link(u2.toString(),new ExternalResource(u2)));
		} catch (Exception e) {
			System.out.println(
				"Couldn't get hostname for this machine: " + e.toString());
			e.printStackTrace();
		}

		// URI 
		Panel p1 = new Panel("URI Handler");
		context.setCaption("Last URI handler context");
		p1.addComponent(context);
		relative.setCaption("Last relative URI");
		p1.addComponent(relative);
		l.addComponent(p1);

		// Parameters
		Panel p2 = new Panel("Parameter Handler");
		params.setCaption("Last parameters");
		params.setColumnHeaderMode(Table.COLUMN_HEADER_MODE_ID);
		params.setRowHeaderMode(Table.ROW_HEADER_MODE_ID);
		p2.addComponent(params);
		l.addComponent(p2);

		return l;
	}

	protected String getDescriptionXHTML() {
		return "This is a demonstration of how URL parameters can be recieved and handled."
			+ "Parameters and URL:s can be received trough the windows by registering "
			+ "URIHandler and ParameterHandler classes window.";
	}

	protected String getImage() {
		return "parameters.jpg";
	}

	protected String getTitle() {
		return "Parameters";
	}

	/** Add URI and parametes handlers to window.
	 * @see com.itmill.toolkit.ui.Component#attach()
	 */
	public void attach() {
		super.attach();
		getWindow().addURIHandler(this);
		getWindow().addParameterHandler(this);
	}

	/** Remove all handlers from window
	 * @see com.itmill.toolkit.ui.Component#detach()
	 */
	public void detach() {
		super.detach();
		getWindow().removeURIHandler(this);
		getWindow().removeParameterHandler(this);
	}

	/** Update URI
	 * @see com.itmill.toolkit.terminal.URIHandler#handleURI(URL, String)
	 */
	public DownloadStream handleURI(URL context, String relativeUri) {
		this.context.setValue(context.toString());
		this.relative.setValue(relativeUri);
		return null;
	}

	/** Update parameters table
	 * @see com.itmill.toolkit.terminal.ParameterHandler#handleParameters(Map)
	 */
	public void handleParameters(Map parameters) {
		params.removeAllItems();
		for (Iterator i = parameters.keySet().iterator(); i.hasNext();) {
			String name = (String) i.next();
			String[] values = (String[]) parameters.get(name);
			String v = "";
			for (int j = 0; j < values.length; j++) {
				if (v.length() > 0)
					v += ", ";
				v += "'" + values[j] + "'";
			}
			params.addItem(new Object[] { v }, name);
		}
	}
}
