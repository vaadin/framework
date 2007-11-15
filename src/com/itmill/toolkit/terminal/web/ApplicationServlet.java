package com.itmill.toolkit.terminal.web;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

/**
 * This package and servlet is only provided for backward compatibility. Since
 * Toolkit version 5.0 you should use
 * com.itmill.toolkit.terminal.gwt.server.ApplicationServlet instead of this
 * class.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 5.0
 */
public class ApplicationServlet extends
		com.itmill.toolkit.terminal.gwt.server.ApplicationServlet {

	private static final long serialVersionUID = -1471357707917217303L;

	public void init(ServletConfig servletConfig) throws ServletException {
		System.err
				.println("Compatiblity class in use. Please use com.itmill.toolkit.terminal.gwt.server.ApplicationServlet instead. You probably need to update your web.xml.");
		super.init(servletConfig);
	}

}
