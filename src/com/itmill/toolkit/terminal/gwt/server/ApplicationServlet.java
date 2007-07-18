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

package com.itmill.toolkit.terminal.gwt.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.WeakHashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.xml.sax.SAXException;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.service.FileTypeResolver;
import com.itmill.toolkit.service.License;
import com.itmill.toolkit.service.License.InvalidLicenseFile;
import com.itmill.toolkit.service.License.LicenseFileHasNotBeenRead;
import com.itmill.toolkit.service.License.LicenseSignatureIsInvalid;
import com.itmill.toolkit.service.License.LicenseViolation;
import com.itmill.toolkit.terminal.DownloadStream;
import com.itmill.toolkit.terminal.Paintable;
import com.itmill.toolkit.terminal.ParameterHandler;
import com.itmill.toolkit.terminal.ThemeResource;
import com.itmill.toolkit.terminal.URIHandler;
import com.itmill.toolkit.ui.Window;

/**
 * This servlet connects IT Mill Toolkit Application to Web. This servlet
 * replaces both WebAdapterServlet and AjaxAdapterServlet.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 4.0
 */

public class ApplicationServlet extends HttpServlet {

	private static final long serialVersionUID = -4937882979845826574L;

	/**
	 * Version number of this release. For example "4.0.0".
	 */
	public static final String VERSION;

	/**
	 * Major version number. For example 4 in 4.1.0.
	 */
	public static final int VERSION_MAJOR;

	/**
	 * Minor version number. For example 1 in 4.1.0.
	 */
	public static final int VERSION_MINOR;

	/**
	 * Builds number. For example 0-beta1 in 4.0.0-beta1.
	 */
	public static final String VERSION_BUILD;

	/* Initialize version numbers from string replaced by build-script. */
	static {
		if ("@VERSION@".equals("@" + "VERSION" + "@"))
			VERSION = "4.9.9-INTERNAL-NONVERSIONED-DEBUG-BUILD";
		else
			VERSION = "@VERSION@";
		String[] digits = VERSION.split("\\.");
		VERSION_MAJOR = Integer.parseInt(digits[0]);
		VERSION_MINOR = Integer.parseInt(digits[1]);
		VERSION_BUILD = digits[2];
	}

	// Configurable parameter names
	private static final String PARAMETER_DEBUG = "Debug";

	private static final int DEFAULT_BUFFER_SIZE = 32 * 1024;

	private static final int MAX_BUFFER_SIZE = 64 * 1024;

	// TODO: these should be moved to session object and stored directly into
	// session
	private static final String SESSION_ATTR_VARMAP = "itmill-toolkit-varmap";

	private static final String SESSION_ATTR_CONTEXT = "itmill-toolkit-context";

	protected static final String SESSION_ATTR_APPS = "itmill-toolkit-apps";

	private static final String SESSION_BINDING_LISTENER = "itmill-toolkit-bindinglistener";

	private static HashMap applicationToLastRequestDate = new HashMap();

	private static HashMap applicationToAjaxAppMgrMap = new HashMap();

	// License for ApplicationServlets
	private static HashMap licenseForApplicationClass = new HashMap();

	private static HashMap licensePrintedForApplicationClass = new HashMap();

	// TODO Should default or base theme be the default?
	protected static final String DEFAULT_THEME = "base";

	private static final String RESOURCE_URI = "/RES/";

	private static final String AJAX_UIDL_URI = "/UIDL/";

	static final String THEME_DIRECTORY_PATH = "/theme/";

	// Maximum delay between request for an user to be considered active (in ms)
	private static final long ACTIVE_USER_REQUEST_INTERVAL = 1000 * 45;
	
	private static final int DEFAULT_THEME_CACHETIME = 1000 * 60 * 60 * 24;
	// Private fields
	private Class applicationClass;

	private Properties applicationProperties;

	private String resourcePath = null;

	private String debugMode = "";

	/**
	 * Called by the servlet container to indicate to a servlet that the servlet
	 * is being placed into service.
	 * 
	 * @param servletConfig
	 *            the object containing the servlet's configuration and
	 *            initialization parameters
	 * @throws javax.servlet.ServletException
	 *             if an exception has occurred that interferes with the
	 *             servlet's normal operation.
	 */
	public void init(javax.servlet.ServletConfig servletConfig)
			throws javax.servlet.ServletException {
		super.init(servletConfig);

		// Gets the application class name
		String applicationClassName = servletConfig
				.getInitParameter("application");
		if (applicationClassName == null) {
			Log.error("Application not specified in servlet parameters");
		}

		// Stores the application parameters into Properties object
		this.applicationProperties = new Properties();
		for (Enumeration e = servletConfig.getInitParameterNames(); e
				.hasMoreElements();) {
			String name = (String) e.nextElement();
			this.applicationProperties.setProperty(name, servletConfig
					.getInitParameter(name));
		}

		// Overrides with server.xml parameters
		ServletContext context = servletConfig.getServletContext();
		for (Enumeration e = context.getInitParameterNames(); e
				.hasMoreElements();) {
			String name = (String) e.nextElement();
			this.applicationProperties.setProperty(name, context
					.getInitParameter(name));
		}

		// Gets the debug window parameter
		String debug = getApplicationOrSystemProperty(PARAMETER_DEBUG, "")
				.toLowerCase();

		// Enables application specific debug
		if (!"".equals(debug) && !"true".equals(debug)
				&& !"false".equals(debug))
			throw new ServletException(
					"If debug parameter is given for an application, it must be 'true' or 'false'");
		this.debugMode = debug;

		// Loads the application class using the same class loader
		// as the servlet itself
		ClassLoader loader = this.getClass().getClassLoader();
		try {
			this.applicationClass = loader.loadClass(applicationClassName);
		} catch (ClassNotFoundException e) {
			throw new ServletException("Failed to load application class: "
					+ applicationClassName);
		}
	}

	/**
	 * Gets an application or system property value.
	 * 
	 * @param parameterName
	 *            the Name or the parameter.
	 * @param defaultValue
	 *            the Default to be used.
	 * @return String value or default if not found
	 */
	private String getApplicationOrSystemProperty(String parameterName,
			String defaultValue) {

		// Try application properties
		String val = this.applicationProperties.getProperty(parameterName);
		if (val != null) {
			return val;
		}

		// Try lowercased application properties for backward compability with
		// 3.0.2 and earlier
		val = this.applicationProperties.getProperty(parameterName
				.toLowerCase());
		if (val != null) {
			return val;
		}

		// Try system properties
		String pkgName;
		Package pkg = this.getClass().getPackage();
		if (pkg != null) {
			pkgName = pkg.getName();
		} else {
			String className = this.getClass().getName();
			pkgName = new String(className.toCharArray(), 0, className
					.lastIndexOf('.'));
		}
		val = System.getProperty(pkgName + "." + parameterName);
		if (val != null) {
			return val;
		}

		// Try lowercased system properties
		val = System.getProperty(pkgName + "." + parameterName.toLowerCase());
		if (val != null) {
			return val;
		}

		return defaultValue;
	}

	/**
	 * Receives standard HTTP requests from the public service method and
	 * dispatches them.
	 * 
	 * @param request
	 *            the object that contains the request the client made of the
	 *            servlet.
	 * @param response
	 *            the object that contains the response the servlet returns to
	 *            the client.
	 * @throws ServletException
	 *             if an input or output error occurs while the servlet is
	 *             handling the TRACE request.
	 * @throws IOException
	 *             if the request for the TRACE cannot be handled.
	 */
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// Transformer and output stream for the result
		OutputStream out = response.getOutputStream();
		Application application = null;
		try {

			// Gets the application
			application = getApplication(request);

			// Creates application if it doesn't exist
			if (application == null)
				application = createApplication(request);

			// Sets the last application request date
			synchronized (applicationToLastRequestDate) {
				applicationToLastRequestDate.put(application, new Date());
			}

			// Invokes context transaction listeners
			((WebApplicationContext) application.getContext())
					.startTransaction(application, request);

			// Is this a download request from application
			DownloadStream download = null;

			// The rest of the process is synchronized with the application
			// in order to guarantee that no parallel variable handling is
			// made
			synchronized (application) {

				// Handles AJAX UIDL requests
				String resourceId = request.getPathInfo();
				if (resourceId != null && resourceId.startsWith(AJAX_UIDL_URI)) {
					getApplicationManager(application).handleUidlRequest(
							request, response);
					return;
				}

				
				// Handles the URI if the application is still running
				if (application.isRunning())
					download = handleURI(application, request, response);

				// If this is not a download request
				if (download == null) {

					// Window renders are not cacheable
					response.setHeader("Cache-Control", "no-cache");
					response.setHeader("Pragma", "no-cache");
					response.setDateHeader("Expires", 0);

					// Finds the window within the application
					Window window = null;
					if (application.isRunning())
						window = getApplicationWindow(request, application);

					// Removes application if it has stopped
					if (!application.isRunning()) {
						endApplication(request, response, application);
						return;
					}

					// Returns blank page, if no window found
					if (window == null) {
						response.setContentType("text/html");
						BufferedWriter page = new BufferedWriter(
								new OutputStreamWriter(out));
						page.write("<html><head><script>");
						// WAS GENERATE WINDOW SCRIPT
						page.write("</script></head><body>");
						page
								.write("The requested window has been removed from application.");
						page.write("</body></html>");
						page.close();

						return;
					}

					// Sets terminal type for the window, if not already set
					if (window.getTerminal() == null) {
						// TODO !!!!
						window.setTerminal(new WebBrowser());
					}

					// Finds theme
					String themeName = window.getTheme() != null ? window
							.getTheme() : DEFAULT_THEME;
					if (request.getParameter("theme") != null) {
						themeName = request.getParameter("theme");
					}
					
					// Handles resource requests
					if (handleResourceRequest(request, response, themeName))
						return;

						writeAjaxPage(request, response, out,
								window, themeName);
				}
			}

			// For normal requests, transform the window
			if (download != null) 

				handleDownload(download, request, response);
		


		} catch (Throwable e) {
			// Print stacktrace
			e.printStackTrace();
			// Re-throw other exceptions
			throw new ServletException(e);
		} finally {

			// Notifies transaction end
			if (application != null)
				((WebApplicationContext) application.getContext())
						.endTransaction(application, request);
		}
	}

	/**
	 * 
	 * @param request
	 *            the HTTP request.
	 * @param response
	 *            the HTTP response to write to.
	 * @param out
	 * @param unhandledParameters
	 * @param window
	 * @param terminalType
	 * @param theme
	 * @throws IOException
	 *             if the writing failed due to input/output error.
	 * @throws MalformedURLException
	 *             if the application is denied access the persistent data store
	 *             represented by the given URL.
	 */
	private void writeAjaxPage(HttpServletRequest request,
			HttpServletResponse response, OutputStream out,
			 Window window, String themeName) throws IOException, MalformedURLException {
		response.setContentType("text/html");
		BufferedWriter page = new BufferedWriter(new OutputStreamWriter(out));

		page
				.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" "
						+ "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");

		
		
		page.write("<html>\n<head>\n<title>IT Mill Toolkit 5</title>\n" +
				"<meta name='gwt:module' content='../com.itmill.toolkit.terminal.gwt.Client=com.itmill.toolkit.terminal.gwt.Client'>\n" +
				"<script type=\"text/javascript\">\n" +
				"	var itmtk = {\n" +
				"		appUri:'");
		
		String[] urlParts = getApplicationUrl(request).toString().split("\\/");
		String appUrl = "";
		// don't use server and port in uri. It may cause problems with some
		// virtual server configurations which lose the server name
		for (int i = 3; i < urlParts.length; i++)
			appUrl += "/" + urlParts[i];
		if (appUrl.endsWith("/"))
			appUrl = appUrl.substring(0, appUrl.length() - 1);
		
		page.write(appUrl);
		
		page.write("'\n};\n" +
				"</script>\n" +
				"<link REL=\"stylesheet\" TYPE=\"text/css\" HREF=\""+request.getContextPath() + THEME_DIRECTORY_PATH+themeName+"/style.css\">" + 
				"</head>\n<body>\n<script language=\"javascript\" src=\"/tk/com.itmill.toolkit.terminal.gwt.Client/gwt.js\"></script>\n" +
				"	<iframe id=\"__gwt_historyFrame\" style=\"width:0;height:0;border:0\"></iframe>\n" +
				"	<div id=\"itmtk-ajax-window\"></div>" +
				"	<div id=\"itmtk-loki\" style=\"width: 100%; position: absolute; left: 0px; bottom: 0; height: 0px; border-top: 1px solid gray; background-color: #f6f6f6; overflow: scroll; font-size: x-small;color:red !important;\"" +
				"></div>\n" +
				"<div id='itm-loki-exp' style='right: 0; bottom: 0px; position: absolute; padding-left: 5px; padding-right: 5px; border-left: 1px solid gray; border-top: 1px solid gray; background-color: #f6f6f6;' onclick='itm_loki_exp()'>console</div><script language='JavaScript'>itm_loki_exp = function() {var l=document.getElementById('itmtk-loki'); var e=document.getElementById('itm-loki-exp'); if (e.style.bottom=='400px') {e.style.bottom='0px'; l.style.height='0px'; e.innerHTML='console';} else {e.style.bottom='400px'; l.style.height='400px'; e.innerHTML='-';}}</script>"+
				"	<div style=\"position: absolute; right: 5px; top: 5px; color: gray;\"><strong>IT Mill Toolkit 5 Prototype</strong></div>\n" + 
				"	</body>\n" + 
				"</html>\n");
	

		page.close();
	}

	/**
	 * Handles the requested URI. An application can add handlers to do special
	 * processing, when a certain URI is requested. The handlers are invoked
	 * before any windows URIs are processed and if a DownloadStream is returned
	 * it is sent to the client.
	 * 
	 * @param application
	 *            the Application owning the URI.
	 * @param request
	 *            the HTTP request instance.
	 * @param response
	 *            the HTTP response to write to.
	 * @return boolean <code>true</code> if the request was handled and
	 *         further processing should be suppressed, <code>false</code>
	 *         otherwise.
	 * @see com.itmill.toolkit.terminal.URIHandler
	 */
	private DownloadStream handleURI(Application application,
			HttpServletRequest request, HttpServletResponse response) {

		String uri = request.getPathInfo();

		// If no URI is available
		if (uri == null || uri.length() == 0 || uri.equals("/"))
			return null;

		// Removes the leading /
		while (uri.startsWith("/") && uri.length() > 0)
			uri = uri.substring(1);

		// Handles the uri
		DownloadStream stream = null;
		try {
			stream = application.handleURI(application.getURL(), uri);
		} catch (Throwable t) {
			application.terminalError(new URIHandlerErrorImpl(application, t));
		}

		return stream;
	}

	/**
	 * Handles the requested URI. An application can add handlers to do special
	 * processing, when a certain URI is requested. The handlers are invoked
	 * before any windows URIs are processed and if a DownloadStream is returned
	 * it is sent to the client.
	 * 
	 * @param stream
	 *            the download stream.
	 * 
	 * @param request
	 *            the HTTP request instance.
	 * @param response
	 *            the HTTP response to write to.
	 * 
	 * @see com.itmill.toolkit.terminal.URIHandler
	 */
	private void handleDownload(DownloadStream stream,
			HttpServletRequest request, HttpServletResponse response) {

		// Download from given stream
		InputStream data = stream.getStream();
		if (data != null) {

			// Sets content type
			response.setContentType(stream.getContentType());

			// Sets cache headers
			long cacheTime = stream.getCacheTime();
			if (cacheTime <= 0) {
				response.setHeader("Cache-Control", "no-cache");
				response.setHeader("Pragma", "no-cache");
				response.setDateHeader("Expires", 0);
			} else {
				response.setHeader("Cache-Control", "max-age=" + cacheTime
						/ 1000);
				response.setDateHeader("Expires", System.currentTimeMillis()
						+ cacheTime);
				response.setHeader("Pragma", "cache"); // Required to apply
				// caching in some
				// Tomcats
			}

			// Copy download stream parameters directly
			// to HTTP headers.
			Iterator i = stream.getParameterNames();
			if (i != null) {
				while (i.hasNext()) {
					String param = (String) i.next();
					response.setHeader((String) param, stream
							.getParameter(param));
				}
			}

			int bufferSize = stream.getBufferSize();
			if (bufferSize <= 0 || bufferSize > MAX_BUFFER_SIZE)
				bufferSize = DEFAULT_BUFFER_SIZE;
			byte[] buffer = new byte[bufferSize];
			int bytesRead = 0;

			try {
				OutputStream out = response.getOutputStream();

				while ((bytesRead = data.read(buffer)) > 0) {
					out.write(buffer, 0, bytesRead);
					out.flush();
				}
				out.close();
			} catch (IOException ignored) {
			}

		}

	}
	
	/**
	 * Handles theme resource file requests. Resources supplied with the themes
	 * are provided by the WebAdapterServlet.
	 * 
	 * @param request
	 *            the HTTP request.
	 * @param response
	 *            the HTTP response.
	 * @return boolean <code>true</code> if the request was handled and
	 *         further processing should be suppressed, <code>false</code>
	 *         otherwise.
	 * @throws ServletException
	 *             if an exception has occurred that interferes with the
	 *             servlet's normal operation.
	 */
	private boolean handleResourceRequest(HttpServletRequest request,
			HttpServletResponse response, String themeName) throws ServletException {

		// If the resource path is unassigned, initialize it
		if (resourcePath == null) {
			resourcePath = request.getContextPath() + request.getServletPath()
					+ RESOURCE_URI;
			// WebSphere Application Server related fix
			resourcePath = resourcePath.replaceAll("//", "/");
		}

		String resourceId = request.getPathInfo();

		// Checks if this really is a resource request
		if (resourceId == null || !resourceId.startsWith(RESOURCE_URI))
			return false;

		// Checks the resource type
		resourceId = resourceId.substring(RESOURCE_URI.length());
		InputStream data = null;
		
		// Gets theme resources
		try {
			data = getServletContext().getResourceAsStream(THEME_DIRECTORY_PATH + themeName + "/" + resourceId);
		} catch (Exception e) {
			Log.info(e.getMessage());
			data = null;
		}

		// Writes the response
		try {
			if (data != null) {
				response.setContentType(FileTypeResolver
						.getMIMEType(resourceId));

				// Use default cache time for theme resources
					response.setHeader("Cache-Control", "max-age="
							+ DEFAULT_THEME_CACHETIME / 1000);
					response.setDateHeader("Expires", System
							.currentTimeMillis()
							+ DEFAULT_THEME_CACHETIME);
					response.setHeader("Pragma", "cache"); // Required to apply
					// caching in some
					// Tomcats
		
				// Writes the data to client
				byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
				int bytesRead = 0;
				OutputStream out = response.getOutputStream();
				while ((bytesRead = data.read(buffer)) > 0) {
					out.write(buffer, 0, bytesRead);
				}
				out.close();
				data.close();
			} else {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}

		} catch (java.io.IOException e) {
			Log.info("Resource transfer failed:  " + request.getRequestURI()
					+ ". (" + e.getMessage() + ")");
		}

		return true;
	}


	/**
	 * Gets the current application URL from request.
	 * 
	 * @param request
	 *            the HTTP request.
	 * @throws MalformedURLException
	 *             if the application is denied access to the persistent data
	 *             store represented by the given URL.
	 */
	private URL getApplicationUrl(HttpServletRequest request)
			throws MalformedURLException {

		URL applicationUrl;
		try {
			URL reqURL = new URL(
					(request.isSecure() ? "https://" : "http://")
							+ request.getServerName()
							+ ((request.isSecure() && request.getServerPort() == 443)
									|| (!request.isSecure() && request
											.getServerPort() == 80) ? "" : ":"
									+ request.getServerPort())
							+ request.getRequestURI());
			String servletPath = request.getContextPath()
					+ request.getServletPath();
			if (servletPath.length() == 0
					|| servletPath.charAt(servletPath.length() - 1) != '/')
				servletPath = servletPath + "/";
			applicationUrl = new URL(reqURL, servletPath);
		} catch (MalformedURLException e) {
			Log.error("Error constructing application url "
					+ request.getRequestURI() + " (" + e + ")");
			throw e;
		}

		return applicationUrl;
	}

	/**
	 * Gets the existing application for given request. Looks for application
	 * instance for given request based on the requested URL.
	 * 
	 * @param request
	 *            the HTTP request.
	 * @return Application instance, or null if the URL does not map to valid
	 *         application.
	 * @throws MalformedURLException
	 *             if the application is denied access to the persistent data
	 *             store represented by the given URL.
	 */
	private Application getApplication(HttpServletRequest request)
			throws MalformedURLException {

		// Ensures that the session is still valid
		HttpSession session = request.getSession(false);
		if (session == null)
			return null;

		// Gets application list for the session.
		LinkedList applications = (LinkedList) session
				.getAttribute(SESSION_ATTR_APPS);
		if (applications == null)
			return null;

		// Search for the application (using the application URI) from the list
		Application application = null;
		for (Iterator i = applications.iterator(); i.hasNext()
				&& application == null;) {
			Application a = (Application) i.next();
			String aPath = a.getURL().getPath();
			String servletPath = request.getContextPath()
					+ request.getServletPath();
			if (servletPath.length() < aPath.length())
				servletPath += "/";
			if (servletPath.equals(aPath))
				application = a;
		}

		// Removes stopped applications from the list
		if (application != null && !application.isRunning()) {
			applications.remove(application);
			application = null;
		}

		return application;
	}

	/**
	 * Creates a new application.
	 * 
	 * @param request
	 *            the HTTP request.
	 * @return the New application instance.
	 * @throws MalformedURLException
	 *             if the application is denied access to the persistent data
	 *             store represented by the given URL.
	 * @throws InstantiationException
	 *             if a new instance of the class cannot be instantiated.
	 * @throws IllegalAccessException
	 *             if it does not have access to the property accessor method.
	 * @throws LicenseFileHasNotBeenRead
	 *             if the license file has not been read.
	 * @throws LicenseSignatureIsInvalid
	 *             if the license file has been changed or signature is
	 *             otherwise invalid.
	 * @throws InvalidLicenseFile
	 *             if the license file is not of correct XML format.
	 * @throws LicenseViolation
	 * 
	 * @throws SAXException
	 *             the Error parsing the license file.
	 */
	private Application createApplication(HttpServletRequest request)
			throws MalformedURLException, InstantiationException,
			IllegalAccessException, LicenseFileHasNotBeenRead,
			LicenseSignatureIsInvalid, InvalidLicenseFile, LicenseViolation,
			SAXException {

		Application application = null;

		// Gets the application url
		URL applicationUrl = getApplicationUrl(request);

		// Gets application list.
		HttpSession session = request.getSession();
		if (session == null)
			return null;
		LinkedList applications = (LinkedList) session
				.getAttribute(SESSION_ATTR_APPS);
		if (applications == null) {
			applications = new LinkedList();
			session.setAttribute(SESSION_ATTR_APPS, applications);
			HttpSessionBindingListener sessionBindingListener = new SessionBindingListener(
					applications);
			session.setAttribute(SESSION_BINDING_LISTENER,
					sessionBindingListener);
		}

		// Creates new application and start it
		try {
			application = (Application) this.applicationClass.newInstance();
			applications.add(application);

			// Sets locale
			application.setLocale(request.getLocale());

			// Gets application context for this session
			WebApplicationContext context = (WebApplicationContext) session
					.getAttribute(SESSION_ATTR_CONTEXT);
			if (context == null) {
				context = new WebApplicationContext(session);
				session.setAttribute(SESSION_ATTR_CONTEXT, context);
			}

			// Starts application and check license
			initializeLicense(application);
			application.start(applicationUrl, this.applicationProperties,
					context);
			checkLicense(application);

		} catch (IllegalAccessException e) {
			Log.error("Illegal access to application class "
					+ this.applicationClass.getName());
			throw e;
		} catch (InstantiationException e) {
			Log.error("Failed to instantiate application class: "
					+ this.applicationClass.getName());
			throw e;
		}

		return application;
	}

	/**
	 * 
	 * @param application
	 */
	private void initializeLicense(Application application) {
		License license;
		synchronized (licenseForApplicationClass) {
			license = (License) licenseForApplicationClass.get(application
					.getClass());
			if (license == null) {
				license = new License();
				licenseForApplicationClass.put(application.getClass(), license);
			}
		}
		application.setToolkitLicense(license);
	}

	/**
	 * 
	 * @param application
	 * @throws LicenseFileHasNotBeenRead
	 *             if the license file has not been read.
	 * @throws LicenseSignatureIsInvalid
	 *             if the license file has been changed or signature is
	 *             otherwise invalid.
	 * @throws InvalidLicenseFile
	 *             if the license file is not of correct XML format.
	 * @throws LicenseViolation
	 * 
	 * @throws SAXException
	 *             the Error parsing the license file.
	 */
	private void checkLicense(Application application)
			throws LicenseFileHasNotBeenRead, LicenseSignatureIsInvalid,
			InvalidLicenseFile, LicenseViolation, SAXException {
		License license = application.getToolkitLicense();

		if (!license.hasBeenRead())
			// Lock threads that have not yet read license
			synchronized (license) {
				if (!license.hasBeenRead()) {
					InputStream lis;
					try {
						URL url = getServletContext().getResource(
								"/WEB-INF/itmill-toolkit-license.xml");
						if (url == null) {
							throw new RuntimeException(
									"License file could not be read. "
											+ "You can install it to "
											+ "WEB-INF/itmill-toolkit-license.xml.");
						}
						lis = url.openStream();
						license.readLicenseFile(lis);
					} catch (MalformedURLException e) {
						// This should not happen
						throw new RuntimeException(e);
					} catch (IOException e) {
						// This should not happen
						throw new RuntimeException(e);
					}

					// For each application class, print license description -
					// once
					if (!licensePrintedForApplicationClass
							.containsKey(applicationClass)) {
						licensePrintedForApplicationClass.put(applicationClass,
								Boolean.TRUE);
						if (license.shouldLimitsBePrintedOnInit()) {
							System.out.println(license
									.getDescription(application.getClass()
											.toString()));
						}
					}

					// Checks license validity
					try {
						license.check(applicationClass, VERSION_MAJOR,
								VERSION_MINOR, "IT Mill Toolkit", null);
					} catch (LicenseFileHasNotBeenRead e) {
						application.close();
						throw e;
					} catch (LicenseSignatureIsInvalid e) {
						application.close();
						throw e;
					} catch (InvalidLicenseFile e) {
						application.close();
						throw e;
					} catch (LicenseViolation e) {
						application.close();
						throw e;
					}
				}
			}

		// Checks concurrent user limit
		try {
			license.checkConcurrentUsers(getNumberOfActiveUsers() + 1);
		} catch (LicenseViolation e) {
			application.close();
			throw e;
		}
	}

	/**
	 * Gets the number of active application-user pairs.
	 * 
	 * This returns total number of all applications in the server that are
	 * considered to be active. For an application to be active, it must have
	 * been accessed less than ACTIVE_USER_REQUEST_INTERVAL ms.
	 * 
	 * @return the Number of active application instances in the server.
	 */
	private int getNumberOfActiveUsers() {
		int active = 0;

		synchronized (applicationToLastRequestDate) {
			Set apps = applicationToLastRequestDate.keySet();
			long now = System.currentTimeMillis();
			for (Iterator i = apps.iterator(); i.hasNext();) {
				Date lastReq = (Date) applicationToLastRequestDate
						.get(i.next());
				if (now - lastReq.getTime() < ACTIVE_USER_REQUEST_INTERVAL)
					active++;
			}
		}

		return active;
	}

	/**
	 * Ends the application.
	 * 
	 * @param request
	 *            the HTTP request.
	 * @param response
	 *            the HTTP response to write to.
	 * @param application
	 *            the application to end.
	 * @throws IOException
	 *             if the writing failed due to input/output error.
	 */
	private void endApplication(HttpServletRequest request,
			HttpServletResponse response, Application application)
			throws IOException {

		String logoutUrl = application.getLogoutURL();
		if (logoutUrl == null)
			logoutUrl = application.getURL().toString();

		HttpSession session = request.getSession();
		if (session != null) {
			LinkedList applications = (LinkedList) session
					.getAttribute(SESSION_ATTR_APPS);
			if (applications != null)
				applications.remove(application);
		}

		response.sendRedirect(response.encodeRedirectURL(logoutUrl));
	}

	/**
	 * Gets the existing application or create a new one. Get a window within an
	 * application based on the requested URI.
	 * 
	 * @param request
	 *            the HTTP Request.
	 * @param application
	 *            the Application to query for window.
	 * @return Window mathing the given URI or null if not found.
	 * @throws ServletException
	 *             if an exception has occurred that interferes with the
	 *             servlet's normal operation.
	 */
	private Window getApplicationWindow(HttpServletRequest request,
			Application application) throws ServletException {

		Window window = null;

		// Finds the window where the request is handled
		String path = request.getPathInfo();

		// Main window as the URI is empty
		if (path == null || path.length() == 0 || path.equals("/"))
			window = application.getMainWindow();

		// Try to search by window name
		else {
			String windowName = null;
			if (path.charAt(0) == '/')
				path = path.substring(1);
			int index = path.indexOf('/');
			if (index < 0) {
				windowName = path;
				path = "";
			} else {
				windowName = path.substring(0, index);
				path = path.substring(index + 1);
			}
			window = application.getWindow(windowName);

			if (window == null) {
				// By default, we use main window
				window = application.getMainWindow();
			} else if (!window.isVisible()) {
				// Implicitly painting without actually invoking paint()
				window.requestRepaintRequests();

				// If the window is invisible send a blank page
				return null;
			}
		}

		return window;
	}

	/**
	 * Gets relative location of a theme resource.
	 * 
	 * @param theme
	 *            the Theme name.
	 * @param resource
	 *            the Theme resource.
	 * @return External URI specifying the resource
	 */
	public String getResourceLocation(String theme, ThemeResource resource) {

		if (resourcePath == null)
			return resource.getResourceId();
		return resourcePath + theme + "/" + resource.getResourceId();
	}

	/**
	 * Checks if web adapter is in debug mode. Extra output is generated to log
	 * when debug mode is enabled.
	 * 
	 * @param parameters
	 * @return <code>true</code> if the web adapter is in debug mode.
	 *         otherwise <code>false</code>.
	 */
	public boolean isDebugMode(Map parameters) {
		if (parameters != null) {
			Object[] debug = (Object[]) parameters.get("debug");
			if (debug != null && !"false".equals(debug[0].toString())
					&& !"false".equals(debugMode))
				return true;
		}
		return "true".equals(debugMode);
	}

	/**
	 * 
	 * SessionBindingListener performs Application cleanups after sessions are
	 * expired. For each session exists one SessionBindingListener. It contains
	 * references to all applications related to single session.
	 * 
	 * @author IT Mill Ltd.
	 * @version
	 * @VERSION@
	 * @since 4.0
	 */

	private class SessionBindingListener implements HttpSessionBindingListener {
		private LinkedList applications;

		/**
		 * 
		 * @param applications
		 */
		protected SessionBindingListener(LinkedList applications) {
			this.applications = applications;
		}

		/**
		 * @see javax.servlet.http.HttpSessionBindingListener#valueBound(HttpSessionBindingEvent)
		 */
		public void valueBound(HttpSessionBindingEvent arg0) {
			// We are not interested in bindings
		}

		/**
		 * @see javax.servlet.http.HttpSessionBindingListener#valueUnbound(HttpSessionBindingEvent)
		 */
		public void valueUnbound(HttpSessionBindingEvent event) {
			// If the binding listener is unbound from the session, the
			// session must be closing
			if (event.getName().equals(SESSION_BINDING_LISTENER)) {
				// Close all applications related to given session
				Object[] apps = applications.toArray();
				for (int i = 0; i < apps.length; i++) {
					if (apps[i] != null) {
						// Close application
						((Application) apps[i]).close();

						synchronized (applicationToLastRequestDate) {
							applicationToLastRequestDate.remove(apps[i]);
						}
						synchronized (applicationToAjaxAppMgrMap) {
							applicationToAjaxAppMgrMap.remove(apps[i]);
						}
						// Remove application from applications list
						applications.remove(apps[i]);
					}
				}
			}
		}
	}

	/**
	 * Implementation of ParameterHandler.ErrorEvent interface.
	 */
	public class ParameterHandlerErrorImpl implements
			ParameterHandler.ErrorEvent {

		private ParameterHandler owner;

		private Throwable throwable;

		/**
		 * 
		 * @param owner
		 * @param throwable
		 */
		private ParameterHandlerErrorImpl(ParameterHandler owner,
				Throwable throwable) {
			this.owner = owner;
			this.throwable = throwable;
		}

		/**
		 * Gets the contained throwable.
		 * 
		 * @see com.itmill.toolkit.terminal.Terminal.ErrorEvent#getThrowable()
		 */
		public Throwable getThrowable() {
			return this.throwable;
		}

		/**
		 * Gets the source ParameterHandler.
		 * 
		 * @see com.itmill.toolkit.terminal.ParameterHandler.ErrorEvent#getParameterHandler()
		 */
		public ParameterHandler getParameterHandler() {
			return this.owner;
		}

	}

	/**
	 * Implementation of URIHandler.ErrorEvent interface.
	 */
	public class URIHandlerErrorImpl implements URIHandler.ErrorEvent {

		private URIHandler owner;

		private Throwable throwable;

		/**
		 * 
		 * @param owner
		 * @param throwable
		 */
		private URIHandlerErrorImpl(URIHandler owner, Throwable throwable) {
			this.owner = owner;
			this.throwable = throwable;
		}

		/**
		 * Gets the contained throwable.
		 * 
		 * @see com.itmill.toolkit.terminal.Terminal.ErrorEvent#getThrowable()
		 */
		public Throwable getThrowable() {
			return this.throwable;
		}

		/**
		 * Gets the source URIHandler.
		 * 
		 * @see com.itmill.toolkit.terminal.URIHandler.ErrorEvent#getURIHandler()
		 */
		public URIHandler getURIHandler() {
			return this.owner;
		}
	}

	/**
	 * Gets AJAX application manager for an application.
	 * 
	 * If this application has not been running in ajax mode before, new manager
	 * is created and web adapter stops listening to changes.
	 * 
	 * @param application
	 * @return AJAX Application Manager
	 */
	private CommunicationManager getApplicationManager(Application application) {
		CommunicationManager mgr = (CommunicationManager) applicationToAjaxAppMgrMap
				.get(application);

		// This application is going from Web to AJAX mode, create new manager
		if (mgr == null) {
			// Creates new manager
			mgr = new CommunicationManager(application, this);
			applicationToAjaxAppMgrMap.put(application, mgr);

			// Manager takes control over the application
			mgr.takeControl();
		}

		return mgr;
	}

	/**
	 * Gets resource path using different implementations. Required fo
	 * supporting different servlet container implementations (application
	 * servers).
	 * 
	 * @param servletContext
	 * @param path
	 *            the resource path.
	 * @return the resource path.
	 */
	protected static String getResourcePath(ServletContext servletContext,
			String path) {
		String resultPath = null;
		resultPath = servletContext.getRealPath(path);
		if (resultPath != null) {
			return resultPath;
		} else {
			try {
				URL url = servletContext.getResource(path);
				resultPath = url.getFile();
			} catch (Exception e) {
				// ignored
			}
		}
		return resultPath;
	}

}