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

package com.itmill.toolkit.terminal.web;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
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
import com.itmill.toolkit.Application.WindowAttachEvent;
import com.itmill.toolkit.Application.WindowDetachEvent;
import com.itmill.toolkit.service.FileTypeResolver;
import com.itmill.toolkit.terminal.DownloadStream;
import com.itmill.toolkit.terminal.Paintable;
import com.itmill.toolkit.terminal.ParameterHandler;
import com.itmill.toolkit.terminal.ThemeResource;
import com.itmill.toolkit.terminal.URIHandler;
import com.itmill.toolkit.terminal.Paintable.RepaintRequestEvent;
import com.itmill.toolkit.terminal.web.ThemeSource.ThemeException;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.service.License;
import com.itmill.toolkit.service.License.InvalidLicenseFile;
import com.itmill.toolkit.service.License.LicenseFileHasAlreadyBeenRead;
import com.itmill.toolkit.service.License.LicenseFileHasNotBeenRead;
import com.itmill.toolkit.service.License.LicenseSignatureIsInvalid;
import com.itmill.toolkit.service.License.LicenseViolation;

/**
 * This servlet connects IT Mill Toolkit Application to Web. This servlet
 * replaces both WebAdapterServlet and AjaxAdapterServlet.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 4.0
 */

public class ApplicationServlet extends HttpServlet implements
		Application.WindowAttachListener, Application.WindowDetachListener,
		Paintable.RepaintRequestListener {

	/** Version number of this release. For example "4.0.0" */
	public static final String VERSION;

	/** Major version number. For example 4 in 4.1.0. */
	public static final int VERSION_MAJOR;

	/** Minor version number. For example 1 in 4.1.0. */
	public static final int VERSION_MINOR;

	/** Build number. For example 0-beta1 in 4.0.0-beta1. */
	public static final String VERSION_BUILD;

	/* Initialize version numbers from string replaced by build-script. */
	static {
		if ("@VERSION@".equals("@" + "VERSION" + "@"))
			VERSION = "4.0.0-INTERNAL-NONVERSIONED-DEBUG-BUILD";
		else
			VERSION = "@VERSION@";
		String[] digits = VERSION.split("\\.");
		VERSION_MAJOR = Integer.parseInt(digits[0]);
		VERSION_MINOR = Integer.parseInt(digits[1]);
		VERSION_BUILD = digits[2];
	}

	// Configurable parameter names
	private static final String PARAMETER_DEBUG = "Debug";

	private static final String PARAMETER_DEFAULT_THEME_JAR = "DefaultThemeJar";

	private static final String PARAMETER_THEMESOURCE = "ThemeSource";

	private static final String PARAMETER_THEME_CACHETIME = "ThemeCacheTime";

	private static final String PARAMETER_MAX_TRANSFORMERS = "MaxTransformers";

	private static final String PARAMETER_TRANSFORMER_CACHETIME = "TransformerCacheTime";

	private static int DEFAULT_THEME_CACHETIME = 1000 * 60 * 60 * 24;

	private static int DEFAULT_BUFFER_SIZE = 32 * 1024;

	private static int DEFAULT_MAX_TRANSFORMERS = 1;

	private static int MAX_BUFFER_SIZE = 64 * 1024;

	private static String SESSION_ATTR_VARMAP = "itmill-toolkit-varmap";

	static String SESSION_ATTR_CONTEXT = "itmill-toolkit-context";

	static String SESSION_ATTR_APPS = "itmill-toolkit-apps";

	private static String SESSION_BINDING_LISTENER = "itmill-toolkit-bindinglistener";

	// TODO Should default or base theme be the default?
	private static String DEFAULT_THEME = "default";

	private static String RESOURCE_URI = "/RES/";

	private static String AJAX_UIDL_URI = "/UIDL/";

	private static String THEME_DIRECTORY_PATH = "WEB-INF/lib/themes/";

	private static String THEME_LISTING_FILE = THEME_DIRECTORY_PATH
			+ "themes.txt";

	private static String DEFAULT_THEME_JAR_PREFIX = "itmill-toolkit-themes";

	private static String DEFAULT_THEME_JAR = "WEB-INF/lib/"
			+ DEFAULT_THEME_JAR_PREFIX + "-" + VERSION + ".jar";

	private static String DEFAULT_THEME_TEMP_FILE_PREFIX = "ITMILL_TMP_";

	private static String SERVER_COMMAND_PARAM = "SERVER_COMMANDS";

	private static int SERVER_COMMAND_STREAM_MAINTAIN_PERIOD = 15000;

	private static int SERVER_COMMAND_HEADER_PADDING = 2000;

	// Maximum delay between request for an user to be considered active (in ms)
	private static long ACTIVE_USER_REQUEST_INTERVAL = 1000 * 45;

	// Private fields
	private Class applicationClass;

	private Properties applicationProperties;

	private UIDLTransformerFactory transformerFactory;

	private CollectionThemeSource themeSource;

	private String resourcePath = null;

	private boolean debugMode = false;

	private int maxConcurrentTransformers;

	private long transformerCacheTime;

	private long themeCacheTime;

	private WeakHashMap applicationToDirtyWindowSetMap = new WeakHashMap();

	private WeakHashMap applicationToServerCommandStreamLock = new WeakHashMap();

	private static WeakHashMap applicationToLastRequestDate = new WeakHashMap();

	private List allWindows = new LinkedList();

	private WeakHashMap applicationToAjaxAppMgrMap = new WeakHashMap();

	private HashMap licenseForApplicationClass = new HashMap();

	private static HashSet licensePrintedForApplicationClass = new HashSet();

	/**
	 * Called by the servlet container to indicate to a servlet that the servlet
	 * is being placed into service.
	 * 
	 * @param servletConfig
	 *            object containing the servlet's configuration and
	 *            initialization parameters
	 * @throws ServletException
	 *             if an exception has occurred that interferes with the
	 *             servlet's normal operation.
	 */
	public void init(javax.servlet.ServletConfig servletConfig)
			throws javax.servlet.ServletException {
		super.init(servletConfig);

		// Get the application class name
		String applicationClassName = servletConfig
				.getInitParameter("application");
		if (applicationClassName == null) {
			Log.error("Application not specified in servlet parameters");
		}

		// Store the application parameters into Properties object
		this.applicationProperties = new Properties();
		for (Enumeration e = servletConfig.getInitParameterNames(); e
				.hasMoreElements();) {
			String name = (String) e.nextElement();
			this.applicationProperties.setProperty(name, servletConfig
					.getInitParameter(name));
		}

		// Override with server.xml parameters
		ServletContext context = servletConfig.getServletContext();
		for (Enumeration e = context.getInitParameterNames(); e
				.hasMoreElements();) {
			String name = (String) e.nextElement();
			this.applicationProperties.setProperty(name, context
					.getInitParameter(name));
		}

		// Get the debug window parameter
		String debug = getApplicationOrSystemProperty(PARAMETER_DEBUG, "false");
		// Enable application specific debug
		this.debugMode = debug.equals("true");

		// Get the maximum number of simultaneous transformers
		this.maxConcurrentTransformers = Integer
				.parseInt(getApplicationOrSystemProperty(
						PARAMETER_MAX_TRANSFORMERS, "-1"));
		if (this.maxConcurrentTransformers < 1)
			this.maxConcurrentTransformers = DEFAULT_MAX_TRANSFORMERS;

		// Get cache time for transformers
		this.transformerCacheTime = Integer
				.parseInt(getApplicationOrSystemProperty(
						PARAMETER_TRANSFORMER_CACHETIME, "-1")) * 1000;

		// Get cache time for theme resources
		this.themeCacheTime = Integer.parseInt(getApplicationOrSystemProperty(
				PARAMETER_THEME_CACHETIME, "-1")) * 1000;
		if (this.themeCacheTime < 0) {
			this.themeCacheTime = DEFAULT_THEME_CACHETIME;
		}

		// Add all specified theme sources
		this.themeSource = new CollectionThemeSource();
		List directorySources = getThemeSources();
		for (Iterator i = directorySources.iterator(); i.hasNext();) {
			this.themeSource.add((ThemeSource) i.next());
		}

		// Add the default theme source
		String[] defaultThemeFiles = new String[] { getApplicationOrSystemProperty(
				PARAMETER_DEFAULT_THEME_JAR, DEFAULT_THEME_JAR) };
		File f = findDefaultThemeJar(defaultThemeFiles);
		try {
			// Add themes.jar if exists
			if (f != null && f.exists())
				this.themeSource.add(new JarThemeSource(f, this, ""));
			else {
				Log.warn("Default theme JAR not found in: "
						+ Arrays.asList(defaultThemeFiles));
			}

		} catch (Exception e) {
			throw new ServletException("Failed to load default theme from "
					+ Arrays.asList(defaultThemeFiles), e);
		}

		// Check that at least one themesource was loaded
		if (this.themeSource.getThemes().size() <= 0) {
			throw new ServletException(
					"No themes found in specified themesources.");
		}

		// Initialize the transformer factory, if not initialized
		if (this.transformerFactory == null) {

			this.transformerFactory = new UIDLTransformerFactory(
					this.themeSource, this, this.maxConcurrentTransformers,
					this.transformerCacheTime);
		}

		// Load the application class using the same class loader
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
	 * Get an application or system property value.
	 * 
	 * @param parameterName
	 *            Name or the parameter
	 * @param defaultValue
	 *            Default to be used
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
			String clazzName = this.getClass().getName();
			pkgName = new String(clazzName.toCharArray(), 0, clazzName
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
	 * Get ThemeSources from given path. Construct the list of avalable themes
	 * in path using the following sources: 1. content of THEME_PATH directory
	 * (if available) 2. The themes listed in THEME_LIST_FILE 3. "themesource"
	 * application parameter - "org. millstone.webadapter. themesource" system
	 * property
	 * 
	 * @param THEME_DIRECTORY_PATH
	 * @return List
	 */
	private List getThemeSources() throws ServletException {

		List returnValue = new LinkedList();

		// Check the list file in theme directory
		List sourcePaths = new LinkedList();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					this.getServletContext().getResourceAsStream(
							THEME_LISTING_FILE)));
			String line = null;
			while ((line = reader.readLine()) != null) {
				sourcePaths.add(THEME_DIRECTORY_PATH + line.trim());
			}
			if (this.isDebugMode()) {
				Log.debug("Listed " + sourcePaths.size() + " themes in "
						+ THEME_LISTING_FILE + ". Loading " + sourcePaths);
			}
		} catch (Exception ignored) {
			// If the file reading fails, just skip to next method
		}

		// If no file was found or it was empty,
		// try to add themes filesystem directory if it is accessible
		if (sourcePaths.size() <= 0) {
			if (this.isDebugMode()) {
				Log.debug("No themes listed in " + THEME_LISTING_FILE
						+ ". Trying to read the content of directory "
						+ THEME_DIRECTORY_PATH);
			}

			try {
				String path = this.getServletContext().getRealPath(
						THEME_DIRECTORY_PATH);
				if (path != null) {
					File f = new File(path);
					if (f != null && f.exists())
						returnValue.add(new DirectoryThemeSource(f, this));
				}
			} catch (java.io.IOException je) {
				Log.info("Theme directory " + THEME_DIRECTORY_PATH
						+ " not available. Skipped.");
			} catch (ThemeException e) {
				throw new ServletException("Failed to load themes from "
						+ THEME_DIRECTORY_PATH, e);
			}
		}

		// Add the theme sources from application properties
		String paramValue = getApplicationOrSystemProperty(
				PARAMETER_THEMESOURCE, null);
		if (paramValue != null) {
			StringTokenizer st = new StringTokenizer(paramValue, ";");
			while (st.hasMoreTokens()) {
				sourcePaths.add(st.nextToken());
			}
		}

		// Construct appropriate theme source instances for each path
		for (Iterator i = sourcePaths.iterator(); i.hasNext();) {
			String source = (String) i.next();
			File sourceFile = new File(source);
			try {

				// Relative files are treated as streams (to support
				// resource inside WAR files)
				if (!sourceFile.isAbsolute()) {
					returnValue.add(new ServletThemeSource(this
							.getServletContext(), this, source));
				} else if (sourceFile.isDirectory()) {

					// Absolute directories are read from filesystem
					returnValue.add(new DirectoryThemeSource(sourceFile, this));
				} else {

					// Absolute JAR-files are read from filesystem
					returnValue.add(new JarThemeSource(sourceFile, this, ""));
				}
			} catch (Exception e) {
				// Any exception breaks the the init
				throw new ServletException("Invalid theme source: " + source, e);
			}
		}

		// Return the constructed list of theme sources
		return returnValue;
	}

	/**
	 * Receives standard HTTP requests from the public service method and
	 * dispatches them.
	 * 
	 * @param request
	 *            object that contains the request the client made of the
	 *            servlet
	 * @param response
	 *            object that contains the response the servlet returns to the
	 *            client
	 * @throws ServletException
	 *             if an input or output error occurs while the servlet is
	 *             handling the TRACE request
	 * @throws IOException
	 *             if the request for the TRACE cannot be handled
	 */
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// Transformer and output stream for the result
		UIDLTransformer transformer = null;
		HttpVariableMap variableMap = null;
		OutputStream out = response.getOutputStream();
		HashSet currentlyDirtyWindowsForThisApplication = new HashSet();
		WebApplicationContext appContext = null;
		Application application = null;
		try {

			// If the resource path is unassigned, initialize it
			if (resourcePath == null)
				resourcePath = request.getContextPath()
						+ request.getServletPath() + RESOURCE_URI;

			// Handle resource requests
			if (handleResourceRequest(request, response))
				return;

			// Handle server commands
			if (handleServerCommands(request, response))
				return;

			// Get the application
			application = getApplication(request);

			// Create application if it doesn't exist
			if (application == null)
				application = createApplication(request);

			// Is this a download request from application
			DownloadStream download = null;

			// Invoke context transaction listeners
			if (application != null) {
				appContext = (WebApplicationContext) application.getContext();
			}
			if (appContext != null) {
				appContext.startTransaction(application, request);
			}

			// Set the last application request date
			applicationToLastRequestDate.put(application, new Date());

			// The rest of the process is synchronized with the application
			// in order to guarantee that no parallel variable handling is
			// made
			synchronized (application) {

				// Handle UIDL requests?
				String resourceId = request.getPathInfo();
				if (resourceId != null && resourceId.startsWith(AJAX_UIDL_URI)) {

					getApplicationManager(application).handleXmlHttpRequest(
							request, response);

					return;
				}

				// Get the variable map
				variableMap = getVariableMap(application, request);
				if (variableMap == null)
					return;

				// Change all variables based on request parameters
				Map unhandledParameters = variableMap.handleVariables(request,
						application);

				// Check/handle client side feature checks
				WebBrowserProbe
						.handleProbeRequest(request, unhandledParameters);

				// Handle the URI if the application is still running
				if (application.isRunning())
					download = handleURI(application, request, response);

				// If this is not a download request
				if (download == null) {

					// Window renders are not cacheable
					response.setHeader("Cache-Control", "no-cache");
					response.setHeader("Pragma", "no-cache");
					response.setDateHeader("Expires", 0);

					// Find the window within the application
					Window window = null;
					if (application.isRunning())
						window = getApplicationWindow(request, application);

					// Handle the unhandled parameters if the application is
					// still running
					if (window != null && unhandledParameters != null
							&& !unhandledParameters.isEmpty()) {
						try {
							window.handleParameters(unhandledParameters);
						} catch (Throwable t) {
							application
									.terminalError(new ParameterHandlerErrorImpl(
											window, t));
						}
					}
					// Remove application if it has stopped
					if (!application.isRunning()) {
						endApplication(request, response, application);
						return;
					}

					// Return blank page, if no window found
					if (window == null) {
						response.setContentType("text/html");
						BufferedWriter page = new BufferedWriter(
								new OutputStreamWriter(out));
						page.write("<html><head><script>");
						page
								.write(ThemeFunctionLibrary
										.generateWindowScript(
												null,
												application,
												this,
												WebBrowserProbe
														.getTerminalType(request
																.getSession())));
						page.write("</script></head><body>");
						page
								.write("The requested window has been removed from application.");
						page.write("</body></html>");
						page.close();

						return;
					}

					// Get the terminal type for the window
					WebBrowser terminalType = (WebBrowser) window.getTerminal();

					// Set terminal type for the window, if not already set
					if (terminalType == null) {
						terminalType = WebBrowserProbe.getTerminalType(request
								.getSession());
						window.setTerminal(terminalType);
					}

					// Find theme and initialize TransformerType
					UIDLTransformerType transformerType = null;
					if (window.getTheme() != null) {
						Theme activeTheme;
						if ((activeTheme = this.themeSource
								.getThemeByName(window.getTheme())) != null) {
							transformerType = new UIDLTransformerType(
									terminalType, activeTheme);
						} else {
							Log
									.info("Theme named '"
											+ window.getTheme()
											+ "' not found. Using system default theme.");
						}
					}

					// Use default theme if selected theme was not found.
					if (transformerType == null) {
						Theme defaultTheme = this.themeSource
								.getThemeByName(ApplicationServlet.DEFAULT_THEME);
						if (defaultTheme == null) {
							throw new ServletException(
									"Default theme not found in the specified theme source(s).");
						}
						transformerType = new UIDLTransformerType(terminalType,
								defaultTheme);
					}

					transformer = this.transformerFactory
							.getTransformer(transformerType);

					// Set the response type
					response.setContentType(terminalType.getContentType());

					// Create UIDL writer
					WebPaintTarget paintTarget = transformer
							.getPaintTarget(variableMap);

					// Assure that the correspoding debug window will be
					// repainted property
					// by clearing it before the actual paint.
					DebugWindow debugWindow = (DebugWindow) application
							.getWindow(DebugWindow.WINDOW_NAME);
					if (debugWindow != null && debugWindow != window) {
						debugWindow.setWindowUIDL(window, "Painting...");
					}

					// Paint window
					window.paint(paintTarget);
					paintTarget.close();

					// For exception handling, memorize the current dirty status
					Collection dirtyWindows = (Collection) applicationToDirtyWindowSetMap
							.get(application);
					if (dirtyWindows == null) {
						dirtyWindows = new HashSet();
						applicationToDirtyWindowSetMap.put(application,
								dirtyWindows);
					}
					currentlyDirtyWindowsForThisApplication
							.addAll(dirtyWindows);

					// Window is now painted
					windowPainted(application, window);

					// Debug
					if (debugWindow != null && debugWindow != window) {
						debugWindow
								.setWindowUIDL(window, paintTarget.getUIDL());
					}

					// Set the function library state for this thread
					ThemeFunctionLibrary.setState(application, window,
							transformerType.getWebBrowser(), request
									.getSession(), this, transformerType
									.getTheme().getName());

				}
			}

			// For normal requests, transform the window
			if (download == null) {

				// Transform and output the result to browser
				// Note that the transform and transfer of the result is
				// not synchronized with the variable map. This allows
				// parallel transfers and transforms for better performance,
				// but requires that all calls from the XSL to java are
				// thread-safe
				transformer.transform(out);
			}

			// For download request, transfer the downloaded data
			else {

				handleDownload(download, request, response);
			}

		} catch (UIDLTransformerException te) {

			try {
				// Write the error report to client
				response.setContentType("text/html");
				BufferedWriter err = new BufferedWriter(new OutputStreamWriter(
						out));
				err
						.write("<html><head><title>Application Internal Error</title></head><body>");
				err.write("<h1>" + te.getMessage() + "</h1>");
				err.write(te.getHTMLDescription());
				err.write("</body></html>");
				err.close();
			} catch (Throwable t) {
				Log.except("Failed to write error page: " + t
						+ ". Original exception was: ", te);
			}

			// Add previously dirty windows to dirtyWindowList in order
			// to make sure that eventually they are repainted
			Application currentApplication = getApplication(request);
			for (Iterator iter = currentlyDirtyWindowsForThisApplication
					.iterator(); iter.hasNext();) {
				Window dirtyWindow = (Window) iter.next();
				addDirtyWindow(currentApplication, dirtyWindow);
			}

		} catch (Throwable e) {
			// Re-throw other exceptions
			throw new ServletException(e);
		} finally {

			// Release transformer
			if (transformer != null)
				transformerFactory.releaseTransformer(transformer);

			// Notify transaction end
			if (appContext != null && application != null) {
				appContext.endTransaction(application, request);
			}

			// Clean the function library state for this thread
			// for security reasons
			ThemeFunctionLibrary.cleanState();
		}
	}

	/**
	 * Handle the requested URI. An application can add handlers to do special
	 * processing, when a certain URI is requested. The handlers are invoked
	 * before any windows URIs are processed and if a DownloadStream is returned
	 * it is sent to the client.
	 * 
	 * @see com.itmill.toolkit.terminal.URIHandler
	 * 
	 * @param application
	 *            Application owning the URI
	 * @param request
	 *            HTTP request instance
	 * @param response
	 *            HTTP response to write to.
	 * @return boolean True if the request was handled and further processing
	 *         should be suppressed, false otherwise.
	 */
	private DownloadStream handleURI(Application application,
			HttpServletRequest request, HttpServletResponse response) {

		String uri = request.getPathInfo();

		// If no URI is available
		if (uri == null || uri.length() == 0 || uri.equals("/"))
			return null;

		// Remove the leading /
		while (uri.startsWith("/") && uri.length() > 0)
			uri = uri.substring(1);

		// Handle the uri
		DownloadStream stream = null;
		try {
			stream = application.handleURI(application.getURL(), uri);
		} catch (Throwable t) {
			application.terminalError(new URIHandlerErrorImpl(application, t));
		}

		return stream;
	}

	/**
	 * Handle the requested URI. An application can add handlers to do special
	 * processing, when a certain URI is requested. The handlers are invoked
	 * before any windows URIs are processed and if a DownloadStream is returned
	 * it is sent to the client.
	 * 
	 * @see com.itmill.toolkit.terminal.URIHandler
	 * 
	 * @param application
	 *            Application owning the URI
	 * @param request
	 *            HTTP request instance
	 * @param response
	 *            HTTP response to write to.
	 * @return boolean True if the request was handled and further processing
	 *         should be suppressed, false otherwise.
	 */
	private void handleDownload(DownloadStream stream,
			HttpServletRequest request, HttpServletResponse response) {

		// Download from given stream
		InputStream data = stream.getStream();
		if (data != null) {

			// Set content type
			response.setContentType(stream.getContentType());

			// Set cache headers
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
	 * Look for default theme JAR file.
	 * 
	 * @return Jar file or null if not found.
	 */
	private File findDefaultThemeJar(String[] fileList) {

		// Try to find the default theme JAR file based on the given path
		for (int i = 0; i < fileList.length; i++) {
			String path = this.getServletContext().getRealPath(fileList[i]);
			File file = null;
			if (path != null && (file = new File(path)).exists()) {
				return file;
			}
		}

		// If we do not have access to individual files, create a temporary
		// file from named resource.
		for (int i = 0; i < fileList.length; i++) {
			InputStream defaultTheme = this.getServletContext()
					.getResourceAsStream(fileList[i]);
			// Read the content to temporary file and return it
			if (defaultTheme != null) {
				return createTemporaryFile(defaultTheme, ".jar");
			}
		}

		// Try to find the default theme JAR file based on file naming scheme
		// NOTE: This is for backward compability with 3.0.2 and earlier.
		String path = this.getServletContext().getRealPath("/WEB-INF/lib");
		if (path != null) {

			File lib = new File(path);
			String[] files = lib.list();
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					if (files[i].toLowerCase().endsWith(".jar")
							&& files[i].startsWith(DEFAULT_THEME_JAR_PREFIX)) {
						return new File(lib, files[i]);
					}
				}
			}
		}

		// If no file was found return null
		return null;
	}

	/**
	 * Create a temporary file for given stream.
	 * 
	 * @param stream
	 *            Stream to be stored into temporary file.
	 * @param extension
	 *            File type extension
	 * @return File
	 */
	private File createTemporaryFile(InputStream stream, String extension) {
		File tmpFile;
		try {
			tmpFile = File.createTempFile(DEFAULT_THEME_TEMP_FILE_PREFIX,
					extension);
			FileOutputStream out = new FileOutputStream(tmpFile);
			byte[] buf = new byte[1024];
			int bytes = 0;
			while ((bytes = stream.read(buf)) > 0) {
				out.write(buf, 0, bytes);
			}
			out.close();
		} catch (IOException e) {
			System.err
					.println("Failed to create temporary file for default theme: "
							+ e);
			tmpFile = null;
		}

		return tmpFile;
	}

	/**
	 * Handle theme resource file requests. Resources supplied with the themes
	 * are provided by the WebAdapterServlet.
	 * 
	 * @param request
	 *            HTTP request
	 * @param response
	 *            HTTP response
	 * @return boolean True if the request was handled and further processing
	 *         should be suppressed, false otherwise.
	 */
	private boolean handleResourceRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {

		String resourceId = request.getPathInfo();

		// Check if this really is a resource request
		if (resourceId == null || !resourceId.startsWith(RESOURCE_URI))
			return false;

		// Check the resource type
		resourceId = resourceId.substring(RESOURCE_URI.length());
		InputStream data = null;
		// Get theme resources
		try {
			data = themeSource.getResource(resourceId);
		} catch (ThemeSource.ThemeException e) {
			Log.info(e.getMessage());
			data = null;
		}

		// Write the response
		try {
			if (data != null) {
				response.setContentType(FileTypeResolver
						.getMIMEType(resourceId));

				// Use default cache time for theme resources
				if (this.themeCacheTime > 0) {
					response.setHeader("Cache-Control", "max-age="
							+ this.themeCacheTime / 1000);
					response.setDateHeader("Expires", System
							.currentTimeMillis()
							+ this.themeCacheTime);
					response.setHeader("Pragma", "cache"); // Required to apply
					// caching in some
					// Tomcats
				}
				// Write the data to client
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

	/** Get the variable map for the session */
	private static synchronized HttpVariableMap getVariableMap(
			Application application, HttpServletRequest request) {

		HttpSession session = request.getSession();

		// Get the application to variablemap map
		Map varMapMap = (Map) session.getAttribute(SESSION_ATTR_VARMAP);
		if (varMapMap == null) {
			varMapMap = new WeakHashMap();
			session.setAttribute(SESSION_ATTR_VARMAP, varMapMap);
		}

		// Create a variable map, if it does not exists.
		HttpVariableMap variableMap = (HttpVariableMap) varMapMap
				.get(application);
		if (variableMap == null) {
			variableMap = new HttpVariableMap();
			varMapMap.put(application, variableMap);
		}

		return variableMap;
	}

	/** Get the current application URL from request */
	private URL getApplicationUrl(HttpServletRequest request)
			throws MalformedURLException {

		URL applicationUrl;
		try {
			URL reqURL = new URL((request.isSecure() ? "https://" : "http://")
					+ request.getServerName() + ":" + request.getServerPort()
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
	 * Get the existing application for given request. Looks for application
	 * instance for given request based on the requested URL.
	 * 
	 * @param request
	 *            HTTP request
	 * @return Application instance, or null if the URL does not map to valid
	 *         application.
	 */
	private Application getApplication(HttpServletRequest request)
			throws MalformedURLException {

		// Ensure that the session is still valid
		HttpSession session = request.getSession(false);
		if (session == null)
			return null;

		// Get application list for the session.
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

		// Remove stopped applications from the list
		if (application != null && !application.isRunning()) {
			applications.remove(application);
			application = null;
		}

		return application;
	}

	/**
	 * Create a new application.
	 * 
	 * @return New application instance
	 * @throws SAXException
	 * @throws LicenseViolation
	 * @throws InvalidLicenseFile
	 * @throws LicenseSignatureIsInvalid
	 * @throws LicenseFileHasNotBeenRead
	 */
	private Application createApplication(HttpServletRequest request)
			throws MalformedURLException, InstantiationException,
			IllegalAccessException, LicenseFileHasNotBeenRead,
			LicenseSignatureIsInvalid, InvalidLicenseFile, LicenseViolation,
			SAXException {

		Application application = null;

		// Get the application url
		URL applicationUrl = getApplicationUrl(request);

		// Get application list.
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

		// Create new application and start it
		try {
			application = (Application) this.applicationClass.newInstance();
			applications.add(application);

			// Listen to window add/removes (for web mode)
			application.addListener((Application.WindowAttachListener) this);
			application.addListener((Application.WindowDetachListener) this);

			// Set localte
			application.setLocale(request.getLocale());

			// Get application context for this session
			WebApplicationContext context = (WebApplicationContext) session
					.getAttribute(SESSION_ATTR_CONTEXT);
			if (context == null) {
				context = new WebApplicationContext(session);
				session.setAttribute(SESSION_ATTR_CONTEXT, context);
			}

			// Start application and check license
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

	private void initializeLicense(Application application) {

		License license = (License) licenseForApplicationClass.get(application
				.getClass());
		if (license == null) {
			license = new License();
			licenseForApplicationClass.put(application.getClass(), license);
		}
		application.setToolkitLicense(license);
	}

	private void checkLicense(Application application)
			throws LicenseFileHasNotBeenRead, LicenseSignatureIsInvalid,
			InvalidLicenseFile, LicenseViolation, SAXException {
		License license = application.getToolkitLicense();
		if (!license.hasBeenRead()) {
			InputStream lis;
			try {
				lis = getServletContext().getResource(
						"/WEB-INF/itmill-toolkit-license.xml").openStream();
				license.readLicenseFile(lis);
			} catch (MalformedURLException e) {
				// This should not happen
				throw new RuntimeException(e);
			} catch (IOException e) {
				// This should not happen
				throw new RuntimeException(e);
			} catch (LicenseFileHasAlreadyBeenRead e) {
				// This should not happen
				throw new RuntimeException(e);
			}
		}

		// For each application class, print license description - once
		if (!licensePrintedForApplicationClass.contains(applicationClass)) {
			licensePrintedForApplicationClass.add(applicationClass);
			if (license.shouldLimitsBePrintedOnInit())
				System.out.print(license.getDescription());
		}

		// Check license validity
		try {
			license.check(applicationClass, getNumberOfActiveUsers() + 1,
					VERSION_MAJOR, VERSION_MINOR, "IT Mill Toolkit", null);
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

	/**
	 * Get the number of active application-user pairs.
	 * 
	 * This returns total number of all applications in the server that are
	 * considered to be active. For an application to be active, it must have
	 * been accessed less than ACTIVE_USER_REQUEST_INTERVAL ms.
	 * 
	 * @return Number of active application instances in the server.
	 */
	private int getNumberOfActiveUsers() {

		Set apps = applicationToLastRequestDate.keySet();
		int active = 0;
		long now = System.currentTimeMillis();
		for (Iterator i = apps.iterator(); i.hasNext();) {
			Date lastReq = (Date) applicationToLastRequestDate.get(i.next());
			if (now - lastReq.getTime() < ACTIVE_USER_REQUEST_INTERVAL)
				active++;
		}

		return active;
	}

	/** End application */
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
	 * Get the existing application or create a new one. Get a window within an
	 * application based on the requested URI.
	 * 
	 * @param request
	 *            HTTP Request.
	 * @param application
	 *            Application to query for window.
	 * @return Window mathing the given URI or null if not found.
	 */
	private Window getApplicationWindow(HttpServletRequest request,
			Application application) throws ServletException {

		Window window = null;

		// Find the window where the request is handled
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

				// If the window has existed, and is now removed
				// send a blank page
				if (allWindows.contains(windowName))
					return null;

				// By default, we use main window
				window = application.getMainWindow();
			} else if (!window.isVisible()) {

				// Implicitly painting without actually invoking paint()
				window.requestRepaintRequests();

				// If the window is invisible send a blank page
				return null;
			}
		}

		// Create and open new debug window for application if requested
		if (this.debugMode
				&& application.getWindow(DebugWindow.WINDOW_NAME) == null)
			try {
				DebugWindow debugWindow = new DebugWindow(application, request
						.getSession(false), this);
				debugWindow.setWidth(370);
				debugWindow.setHeight(480);
				application.addWindow(debugWindow);
			} catch (Exception e) {
				throw new ServletException(
						"Failed to create debug window for application", e);
			}

		return window;
	}

	/**
	 * Get relative location of a theme resource.
	 * 
	 * @param theme
	 *            Theme name
	 * @param resource
	 *            Theme resource
	 * @return External URI specifying the resource
	 */
	public String getResourceLocation(String theme, ThemeResource resource) {

		if (resourcePath == null)
			return resource.getResourceId();
		return resourcePath + theme + "/" + resource.getResourceId();
	}

	/**
	 * Check if web adapter is in debug mode. Extra output is generated to log
	 * when debug mode is enabled.
	 * 
	 * @return Debug mode
	 */
	public boolean isDebugMode() {
		return debugMode;
	}

	/**
	 * Returns the theme source.
	 * 
	 * @return ThemeSource
	 */
	public ThemeSource getThemeSource() {
		return themeSource;
	}

	protected void addDirtyWindow(Application application, Window window) {
		synchronized (applicationToDirtyWindowSetMap) {
			HashSet dirtyWindows = (HashSet) applicationToDirtyWindowSetMap
					.get(application);
			if (dirtyWindows == null) {
				dirtyWindows = new HashSet();
				applicationToDirtyWindowSetMap.put(application, dirtyWindows);
			}
			dirtyWindows.add(window);
		}
	}

	protected void removeDirtyWindow(Application application, Window window) {
		synchronized (applicationToDirtyWindowSetMap) {
			HashSet dirtyWindows = (HashSet) applicationToDirtyWindowSetMap
					.get(application);
			if (dirtyWindows != null)
				dirtyWindows.remove(window);
		}
	}

	/**
	 * @see com.itmill.toolkit.Application.WindowAttachListener#windowAttached(Application.WindowAttachEvent)
	 */
	public void windowAttached(WindowAttachEvent event) {
		Window win = event.getWindow();
		win.addListener((Paintable.RepaintRequestListener) this);

		// Add to window names
		allWindows.add(win.getName());

		// Add window to dirty window references if it is visible
		// Or request the window to pass on the repaint requests
		if (win.isVisible())
			addDirtyWindow(event.getApplication(), win);
		else
			win.requestRepaintRequests();

	}

	/**
	 * @see com.itmill.toolkit.Application.WindowDetachListener#windowDetached(Application.WindowDetachEvent)
	 */
	public void windowDetached(WindowDetachEvent event) {
		event.getWindow().removeListener(
				(Paintable.RepaintRequestListener) this);

		// Add dirty window reference for closing the window
		addDirtyWindow(event.getApplication(), event.getWindow());
	}

	/**
	 * @see com.itmill.toolkit.terminal.Paintable.RepaintRequestListener#repaintRequested(Paintable.RepaintRequestEvent)
	 */
	public void repaintRequested(RepaintRequestEvent event) {

		Paintable p = event.getPaintable();
		Application app = null;
		if (p instanceof Window)
			app = ((Window) p).getApplication();

		if (app != null)
			addDirtyWindow(app, ((Window) p));

		Object lock = applicationToServerCommandStreamLock.get(app);
		if (lock != null)
			synchronized (lock) {
				lock.notifyAll();
			}
	}

	/** Get the list of dirty windows in application */
	protected Set getDirtyWindows(Application app) {
		HashSet dirtyWindows;
		synchronized (applicationToDirtyWindowSetMap) {
			dirtyWindows = (HashSet) applicationToDirtyWindowSetMap.get(app);
		}
		return dirtyWindows;
	}

	/** Remove a window from the list of dirty windows */
	private void windowPainted(Application app, Window window) {
		removeDirtyWindow(app, window);
	}

	/**
	 * Generate server commands stream. If the server commands are not
	 * requested, return false
	 */
	private boolean handleServerCommands(HttpServletRequest request,
			HttpServletResponse response) {

		// Server commands are allways requested with certain parameter
		if (request.getParameter(SERVER_COMMAND_PARAM) == null)
			return false;

		// Get the application
		Application application;
		try {
			application = getApplication(request);
		} catch (MalformedURLException e) {
			return false;
		}
		if (application == null)
			return false;

		// Create continuous server commands stream
		try {

			// Writer for writing the stream
			PrintWriter w = new PrintWriter(response.getOutputStream());

			// Print necessary http page headers and padding
			w.println("<html><head></head><body>");
			for (int i = 0; i < SERVER_COMMAND_HEADER_PADDING; i++)
				w.print(' ');

			// Clock for synchronizing the stream
			Object lock = new Object();
			synchronized (applicationToServerCommandStreamLock) {
				Object oldlock = applicationToServerCommandStreamLock
						.get(application);
				if (oldlock != null)
					synchronized (oldlock) {
						oldlock.notifyAll();
					}
				applicationToServerCommandStreamLock.put(application, lock);
			}
			while (applicationToServerCommandStreamLock.get(application) == lock
					&& application.isRunning()) {
				synchronized (application) {

					// Session expiration
					Date lastRequest = (Date) applicationToLastRequestDate
							.get(application);
					if (lastRequest != null
							&& lastRequest.getTime()
									+ request.getSession()
											.getMaxInactiveInterval() * 1000 < System
									.currentTimeMillis()) {

						// Session expired, close application
						application.close();
					} else {

						// Application still alive - keep updating windows
						Set dws = getDirtyWindows(application);
						if (dws != null && !dws.isEmpty()) {

							// For one of the dirty windows (in each
							// application)
							// request redraw
							Window win = (Window) dws.iterator().next();
							w
									.println("<script>\n"
											+ ThemeFunctionLibrary
													.getWindowRefreshScript(
															application,
															win,
															WebBrowserProbe
																	.getTerminalType(request
																			.getSession()))
											+ "</script>");

							removeDirtyWindow(application, win);

							// Windows that are closed immediately are "painted"
							// now
							if (win.getApplication() == null
									|| !win.isVisible())
								win.requestRepaintRequests();
						}
					}
				}

				// Send the generated commands and newline immediately to
				// browser
				w.println(" ");
				w.flush();
				response.flushBuffer();

				synchronized (lock) {
					try {
						lock.wait(SERVER_COMMAND_STREAM_MAINTAIN_PERIOD);
					} catch (InterruptedException ignored) {
					}
				}
			}
		} catch (IOException ignore) {

			// In case of an Exceptions the server command stream is
			// terminated
			synchronized (applicationToServerCommandStreamLock) {
				if (applicationToServerCommandStreamLock.get(application) == application)
					applicationToServerCommandStreamLock.remove(application);
			}
		}

		return true;
	}

	private class SessionBindingListener implements HttpSessionBindingListener {
		private LinkedList applications;

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

				// Close all applications
				Object[] apps = applications.toArray();
				for (int i = 0; i < apps.length; i++) {
					if (apps[i] != null) {

						// Close app
						((Application) apps[i]).close();

						// Stop application server commands stream
						Object lock = applicationToServerCommandStreamLock
								.get(apps[i]);
						if (lock != null)
							synchronized (lock) {
								lock.notifyAll();
							}
						applicationToServerCommandStreamLock.remove(apps[i]);

						// Remove application from applications list
						applications.remove(apps[i]);
					}
				}
			}
		}

	}

	/** Implementation of ParameterHandler.ErrorEvent interface. */
	public class ParameterHandlerErrorImpl implements
			ParameterHandler.ErrorEvent {

		private ParameterHandler owner;

		private Throwable throwable;

		private ParameterHandlerErrorImpl(ParameterHandler owner,
				Throwable throwable) {
			this.owner = owner;
			this.throwable = throwable;
		}

		/**
		 * @see com.itmill.toolkit.terminal.Terminal.ErrorEvent#getThrowable()
		 */
		public Throwable getThrowable() {
			return this.throwable;
		}

		/**
		 * @see com.itmill.toolkit.terminal.ParameterHandler.ErrorEvent#getParameterHandler()
		 */
		public ParameterHandler getParameterHandler() {
			return this.owner;
		}

	}

	/** Implementation of URIHandler.ErrorEvent interface. */
	public class URIHandlerErrorImpl implements URIHandler.ErrorEvent {

		private URIHandler owner;

		private Throwable throwable;

		private URIHandlerErrorImpl(URIHandler owner, Throwable throwable) {
			this.owner = owner;
			this.throwable = throwable;
		}

		/**
		 * @see com.itmill.toolkit.terminal.Terminal.ErrorEvent#getThrowable()
		 */
		public Throwable getThrowable() {
			return this.throwable;
		}

		/**
		 * @see com.itmill.toolkit.terminal.URIHandler.ErrorEvent#getURIHandler()
		 */
		public URIHandler getURIHandler() {
			return this.owner;
		}
	}

	/**
	 * Get AJAX application manager for an application.
	 * 
	 * If this application has not been running in ajax mode before, new manager
	 * is created and web adapter stops listening to changes.
	 * 
	 * @param application
	 * @return AJAX Application Manager
	 */
	private AjaxApplicationManager getApplicationManager(Application application) {
		AjaxApplicationManager mgr = (AjaxApplicationManager) applicationToAjaxAppMgrMap
				.get(application);

		// This application is going from Web to AJAX mode, create new manager
		if (mgr == null) {

			// Create new manager
			mgr = new AjaxApplicationManager(application);
			applicationToAjaxAppMgrMap.put(application, mgr);

			// Stop sending changes to this servlet because manager will take
			// control
			application.removeListener((Application.WindowAttachListener) this);
			application.removeListener((Application.WindowDetachListener) this);

			// Manager takes control over the application
			mgr.takeControl();
		}
		return mgr;
	}
}
