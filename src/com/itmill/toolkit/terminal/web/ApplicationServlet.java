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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
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
import com.itmill.toolkit.service.License;
import com.itmill.toolkit.service.License.InvalidLicenseFile;
import com.itmill.toolkit.service.License.LicenseFileHasNotBeenRead;
import com.itmill.toolkit.service.License.LicenseSignatureIsInvalid;
import com.itmill.toolkit.service.License.LicenseViolation;
import com.itmill.toolkit.terminal.DownloadStream;
import com.itmill.toolkit.terminal.Paintable;
import com.itmill.toolkit.terminal.ParameterHandler;
import com.itmill.toolkit.terminal.StreamResource;
import com.itmill.toolkit.terminal.ThemeResource;
import com.itmill.toolkit.terminal.URIHandler;
import com.itmill.toolkit.terminal.Paintable.RepaintRequestEvent;
import com.itmill.toolkit.terminal.web.ThemeSource.ThemeException;
import com.itmill.toolkit.terminal.web.WebBrowser;
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

public class ApplicationServlet extends HttpServlet implements
		Application.WindowAttachListener, Application.WindowDetachListener,
		Paintable.RepaintRequestListener {

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

	private static final int DEFAULT_THEME_CACHETIME = 1000 * 60 * 60 * 24;

	private static final int DEFAULT_BUFFER_SIZE = 32 * 1024;

	private static final int DEFAULT_MAX_TRANSFORMERS = 1;

	private static final int MAX_BUFFER_SIZE = 64 * 1024;

	// TODO: these should be moved to session object and stored directly into
	// session
	private static final String SESSION_ATTR_VARMAP = "itmill-toolkit-varmap";

	private static final String SESSION_ATTR_CONTEXT = "itmill-toolkit-context";

	protected static final String SESSION_ATTR_APPS = "itmill-toolkit-apps";

	private static final String SESSION_BINDING_LISTENER = "itmill-toolkit-bindinglistener";

	private static HashMap applicationToDirtyWindowSetMap = new HashMap();

	private static HashMap applicationToServerCommandStreamLock = new HashMap();

	private static HashMap applicationToLastRequestDate = new HashMap();

	private static HashMap applicationToAjaxAppMgrMap = new HashMap();

	// License for ApplicationServlets
	private static HashMap licenseForApplicationClass = new HashMap();

	private static HashMap licensePrintedForApplicationClass = new HashMap();

	// TODO Should default or base theme be the default?
	protected static final String DEFAULT_THEME = "base";

	private static final String RESOURCE_URI = "/RES/";

	private static final String AJAX_UIDL_URI = "/UIDL/";

	private static final String THEME_DIRECTORY_PATH = "/WEB-INF/lib/themes/";

	private static final String THEME_LISTING_FILE = THEME_DIRECTORY_PATH
			+ "themes.txt";

	private static final String DEFAULT_THEME_JAR_PREFIX = "itmill-toolkit-themes";

	private static final String DEFAULT_THEME_JAR = "/WEB-INF/lib/"
			+ DEFAULT_THEME_JAR_PREFIX + "-" + VERSION + ".jar";

	private static final String DEFAULT_THEME_TEMP_FILE_PREFIX = "ITMILL_TMP_";

	private static final String SERVER_COMMAND_PARAM = "SERVER_COMMANDS";

	private static final int SERVER_COMMAND_STREAM_MAINTAIN_PERIOD = 15000;

	private static final int SERVER_COMMAND_HEADER_PADDING = 2000;

	// Maximum delay between request for an user to be considered active (in ms)
	private static final long ACTIVE_USER_REQUEST_INTERVAL = 1000 * 45;

	// Private fields
	private Class applicationClass;

	private Properties applicationProperties;

	private UIDLTransformerFactory transformerFactory;

	private CollectionThemeSource themeSource;

	private String resourcePath = null;

	private String debugMode = "";

	private int maxConcurrentTransformers;

	private long transformerCacheTime;

	private long themeCacheTime;

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

		// Gets the maximum number of simultaneous transformers
		this.maxConcurrentTransformers = Integer
				.parseInt(getApplicationOrSystemProperty(
						PARAMETER_MAX_TRANSFORMERS, "-1"));
		if (this.maxConcurrentTransformers < 1)
			this.maxConcurrentTransformers = DEFAULT_MAX_TRANSFORMERS;

		// Gets cache time for transformers
		this.transformerCacheTime = Integer
				.parseInt(getApplicationOrSystemProperty(

				PARAMETER_TRANSFORMER_CACHETIME, "-1")) * 1000;

		// Gets cache time for theme resources
		this.themeCacheTime = Integer.parseInt(getApplicationOrSystemProperty(
				PARAMETER_THEME_CACHETIME, "-1")) * 1000;
		if (this.themeCacheTime < 0) {
			this.themeCacheTime = DEFAULT_THEME_CACHETIME;
		}

		// Adds all specified theme sources
		this.themeSource = new CollectionThemeSource();
		List directorySources = getThemeSources();
		for (Iterator i = directorySources.iterator(); i.hasNext();) {
			this.themeSource.add((ThemeSource) i.next());
		}

		// Adds the default theme source
		String[] defaultThemeFiles = new String[] { getApplicationOrSystemProperty(
				PARAMETER_DEFAULT_THEME_JAR, DEFAULT_THEME_JAR) };
		File f = findDefaultThemeJar(defaultThemeFiles);
		boolean defaultThemeFound = false;
		try {
			// Adds themes.jar if exists
			if (f != null && f.exists()) {
				this.themeSource.add(new JarThemeSource(f, this, ""));
				defaultThemeFound = true;
			}
		} catch (Exception e) {
			throw new ServletException("Failed to load default theme from "
					+ Arrays.asList(defaultThemeFiles), e);
		}

		// Checks that at least one themesource was loaded
		if (this.themeSource.getThemes().size() <= 0) {
			throw new ServletException(
					"No themes found in specified themesources. "
							+ Theme.MESSAGE_CONFIGURE_HELP);
		}

		// Warn if default theme not found
		if (this.themeSource.getThemeByName(DEFAULT_THEME) == null) {
			if (!defaultThemeFound)
				Log.warn("Default theme JAR not found in: "
						+ Arrays.asList(defaultThemeFiles));
		}

		// Initializes the transformer factory, if not initialized
		if (this.transformerFactory == null) {
			this.transformerFactory = new UIDLTransformerFactory(
					this.themeSource, this, this.maxConcurrentTransformers,
					this.transformerCacheTime);
		}

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
	 * Gets ThemeSources from given path. Construct the list of avalable themes
	 * in path using the following sources:
	 * <p>
	 * 1. Content of <code>THEME_PATH</code> directory (if available).
	 * </p>
	 * <p>
	 * 2. The themes listed in <code>THEME_LIST_FILE</code>.
	 * </p>
	 * <p>
	 * 3. "themesource" application parameter - "ThemeSource" system property.
	 * </p>
	 * 
	 * @return the List
	 * @throws ServletException
	 *             if an exception has occurred that interferes with the
	 *             servlet's normal operation.
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
			if (this.isDebugMode(null)) {
				Log.debug("Listed " + sourcePaths.size() + " themes in "
						+ THEME_LISTING_FILE + ". Loading " + sourcePaths);
			}
		} catch (Exception ignored) {
			// If the file reading fails, just skip to next method
		}

		// If no file was found or it was empty,
		// try to add themes filesystem directory if it is accessible
		if (sourcePaths.size() <= 0) {
			if (this.isDebugMode(null)) {
				Log.debug("No themes listed in " + THEME_LISTING_FILE
						+ ". Trying to read the content of directory "
						+ THEME_DIRECTORY_PATH);
			}

			try {
				String path = getResourcePath(getServletContext(),
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

		// Adds the theme sources from application properties
		String paramValue = getApplicationOrSystemProperty(
				PARAMETER_THEMESOURCE, null);
		if (paramValue != null) {
			StringTokenizer st = new StringTokenizer(paramValue, ";");
			while (st.hasMoreTokens()) {
				sourcePaths.add(st.nextToken());
			}
		}

		// Constructs appropriate theme source instances for each path
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

		// Returns the constructed list of theme sources
		return returnValue;
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
		UIDLTransformer transformer = null;
		HttpVariableMap variableMap = null;
		OutputStream out = response.getOutputStream();
		HashMap currentlyDirtyWindowsForThisApplication = new HashMap();
		Application application = null;
		try {

			// Handles resource requests
			if (handleResourceRequest(request, response))
				return;

			// Handles server commands
			if (handleServerCommands(request, response))
				return;

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
							request, response, themeSource);
					return;
				}

				// Gets the variable map
				variableMap = getVariableMap(application, request);
				if (variableMap == null)
					return;

				// Change all variables based on request parameters
				Map unhandledParameters = variableMap.handleVariables(request,
						application);

				// Check/handle client side feature checks
				WebBrowserProbe
						.handleProbeRequest(request, unhandledParameters);

				// If rendering mode is not defined or detecting requested
				// try to detect it
				WebBrowser wb = WebBrowserProbe.getTerminalType(request
						.getSession());

				boolean detect = false;
				if (unhandledParameters.get("renderingMode") != null) {
					detect = ((String) ((Object[]) unhandledParameters
							.get("renderingMode"))[0]).equals("detect");
				}
				if (detect
						|| wb.getRenderingMode() == WebBrowser.RENDERING_MODE_UNDEFINED) {
					String themeName = application.getTheme();
					if (themeName == null)
						themeName = DEFAULT_THEME;
					if (unhandledParameters.get("theme") != null) {
						themeName = (String) ((Object[]) unhandledParameters
								.get("theme"))[0];
					}

					Theme theme = themeSource.getThemeByName(themeName);
					if (theme == null)
						throw new ServletException(
								"Failed to load theme with name " + themeName
										+ ". " + Theme.MESSAGE_CONFIGURE_HELP);

					String renderingMode = theme.getPreferredMode(wb,
							themeSource);
					if (Theme.MODE_AJAX.equals(renderingMode)) {
						wb.setRenderingMode(WebBrowser.RENDERING_MODE_AJAX);
					} else {
						wb.setRenderingMode(WebBrowser.RENDERING_MODE_HTML);
					}
				}
				if (unhandledParameters.get("renderingMode") != null) {
					String renderingMode = (String) ((Object[]) unhandledParameters
							.get("renderingMode"))[0];
					if (renderingMode.equals("html")) {
						wb.setRenderingMode(WebBrowser.RENDERING_MODE_HTML);
					} else if (renderingMode.equals("ajax")) {
						wb.setRenderingMode(WebBrowser.RENDERING_MODE_AJAX);
					}
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
						window = getApplicationWindow(request, application,
								unhandledParameters);

					// Handles the unhandled parameters if the application is
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

					// Sets terminal type for the window, if not already set
					if (window.getTerminal() == null) {
						window.setTerminal(wb);
					}

					// Finds theme
					String themeName = window.getTheme() != null ? window
							.getTheme() : DEFAULT_THEME;
					if (unhandledParameters.get("theme") != null) {
						themeName = (String) ((Object[]) unhandledParameters
								.get("theme"))[0];
					}
					Theme theme = themeSource.getThemeByName(themeName);
					if (theme == null)
						throw new ServletException("Theme (named '" + themeName
								+ "') can not be found");

					// If in ajax rendering mode, print an html page for it
					if (wb.getRenderingMode() == WebBrowser.RENDERING_MODE_AJAX) {
						writeAjaxPage(request, response, out,
								unhandledParameters, window, wb, theme);
						return;
					}

					// If other than html or ajax mode is requested
					if (wb.getRenderingMode() == WebBrowser.RENDERING_MODE_UNDEFINED
							&& !(window instanceof DebugWindow)) {
						// TODO More informal message should be given to browser
						response.setContentType("text/html");
						BufferedWriter page = new BufferedWriter(
								new OutputStreamWriter(out));
						page.write("<html><head></head><body>");
						page.write("Unsupported browser.");
						page.write("</body></html>");
						page.close();

						return;
					}

					// Initialize Transformer
					UIDLTransformerType transformerType = new UIDLTransformerType(
							wb, theme);

					transformer = this.transformerFactory
							.getTransformer(transformerType);

					// Sets the response type
					response.setContentType(wb.getContentType());

					// Creates UIDL writer
					WebPaintTarget paintTarget = transformer
							.getPaintTarget(variableMap);

					// Assures that the correspoding debug window will be
					// repainted property
					// by clearing it before the actual paint.
					DebugWindow debugWindow = (DebugWindow) application
							.getWindow(DebugWindow.WINDOW_NAME);
					if (debugWindow != null && debugWindow != window) {
						debugWindow.setWindowUIDL(window, "Painting...");
					}

					// Paints window
					window.paint(paintTarget);
					paintTarget.close();

					// For exception handling, memorize the current dirty status
					HashMap dirtyWindows = (HashMap) applicationToDirtyWindowSetMap
							.get(application);

					if (dirtyWindows == null) {
						dirtyWindows = new HashMap();
						applicationToDirtyWindowSetMap.put(application,
								dirtyWindows);
					}
					currentlyDirtyWindowsForThisApplication
							.putAll((Map) dirtyWindows);

					// Window is now painted
					windowPainted(application, window);

					// Debug
					if (debugWindow != null && debugWindow != window) {
						debugWindow
								.setWindowUIDL(window, paintTarget.getUIDL());
					}

					// Sets the function library state for this thread
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
			// Print stacktrace
			te.printStackTrace();

			try {
				// Writes the error report to client
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

			// Adds previously dirty windows to dirtyWindowList in order
			// to make sure that eventually they are repainted
			Application currentApplication = getApplication(request);
			for (Iterator iter = currentlyDirtyWindowsForThisApplication
					.keySet().iterator(); iter.hasNext();) {
				Window dirtyWindow = (Window) iter.next();
				addDirtyWindow(currentApplication, dirtyWindow);
			}

		} catch (Throwable e) {
			// Print stacktrace
			e.printStackTrace();
			// Re-throw other exceptions
			throw new ServletException(e);
		} finally {

			// Releases transformer
			if (transformer != null)
				transformerFactory.releaseTransformer(transformer);

			// Notifies transaction end
			if (application != null)
				((WebApplicationContext) application.getContext())
						.endTransaction(application, request);

			// Cleans the function library state for this thread
			// for security reasons
			ThemeFunctionLibrary.cleanState();
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
			Map unhandledParameters, Window window, WebBrowser terminalType,
			Theme theme) throws IOException, MalformedURLException {
		response.setContentType("text/html");
		BufferedWriter page = new BufferedWriter(new OutputStreamWriter(out));
	
		page
				.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" "
						+ "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");
	
		page
				.write("<html><head>\n<title>" + window.getCaption()
						+ "</title>\n");

		page.write("<link rel=\"stylesheet\" href=\"" + resourcePath + theme.getName() +
				"/css/compiledstyle.css\" type=\"text/css\" />\n");
		page.write("<script type=\"text/javascript\" src=\"" + resourcePath + theme.getName() + 
				"/script/compiledjavascript.js\"></script>");

		page.write("</head><body class=\"itmtk\">\n");
		page.write("<noscript>Your browser claims to have a Javascript engine, but for some reason it is turned off. Eithter turn it on or use <a href=\"?renderingMode=detect&amp;WA_NOSCRIPT=1\">degraded mode</a>.</noscript>\n");

		page.write("<div id=\"ajax-wait\">Loading...</div>\n");
	
		page.write("<div id=\"ajax-window\"></div>\n");
	
		page.write("<script type=\"text/javascript\">\n");
	
		String[] urlParts = getApplicationUrl(request).toString().split("\\/");
		String appUrl = "";
		// don't use server and port in uri. It may cause problems with some
		// virtual server configurations which lose the server name
		for (int i = 3; i < urlParts.length; i++)
			appUrl += "/" + urlParts[i];
		if (appUrl.endsWith("/"))
			appUrl = appUrl.substring(0, appUrl.length() - 1);
		page.write("itmill.tmp = new itmill.Client("
				+ "document.getElementById('ajax-window')," + "\"" + appUrl
				+ "/UIDL/" + "\",\"" + resourcePath
				+ theme.getName() + "/"
	
				+ "client/\",document.getElementById('ajax-wait'));\n");
	
		String themeObjName = "itmill.themes."
				+ theme.getName().substring(0, 1).toUpperCase()
				+ theme.getName().substring(1);
		page.write(" (new " + themeObjName + "(\"" + resourcePath + theme.getName()
				+ "/\")).registerTo(itmill.tmp);\n");
		// }
	
		if (isDebugMode(unhandledParameters))
			page.write("itmill.tmp.debugEnabled =true;\n");
		page.write("itmill.tmp.start();\n");
		page.write("delete itmill.tmp;\n");
	
		page.write("</script>\n");
	
		page.write("</body></html>\n");
		page.close();
	}

	/**
	 * Writes javascript for this theme and terminal
	 * 
	 * @param response
	 *            the HTTP response to write to.
	 * @param terminalType
	 * @param theme
	 * @throws IOException
	 *             if the writing failed due to input/output error.
	 */
	private void writeJavascript(HttpServletResponse response,
			WebBrowser terminalType, Theme theme) throws IOException, MalformedURLException {
		response.setContentType("text/javascript");
		BufferedWriter page = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));
		getThemeResourcesWithType(".js",terminalType, theme, page);
		page.close();
	}

	/**
	 * Writes javascript for this theme and terminal
	 * 
	 * @param response
	 *            the HTTP response to write to.
	 * @param terminalType
	 * @param theme
	 * @throws IOException
	 *             if the writing failed due to input/output error.
	 */
	private void writeCss(HttpServletResponse response, WebBrowser terminalType, 
			Theme theme) throws IOException, MalformedURLException {
		response.setContentType("text/css");
		BufferedWriter page = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));
		getThemeResourcesWithType(".css",terminalType, theme, page);
		page.close();
	}

	/**
	 * Catenates all themes required files that which ends with type into a StringBuffer.
	 * Used to serve browser only a single javascript or css file
	 * 
	 * @param terminalType
	 * @param theme
	 * @throws IOException
	 *             if the writing failed due to input/output error.
	 * @return StringBuffer containing all themes javascript 
	 */
	private void getThemeResourcesWithType(String type, WebBrowser terminalType,
			Theme theme, BufferedWriter out) throws IOException {
		Vector themes = new Vector();
		themes.add(theme);
		while (theme.getParent() != null) {
			String parentName = theme.getParent();
			theme = themeSource.getThemeByName(parentName);
			themes.add(theme);
		}
		
		for (int k = themes.size() - 1; k >= 0; k--) {
			theme = (Theme) themes.get(k);
			Collection files = theme.getFileNames(terminalType, Theme.MODE_AJAX);
			for (Iterator i = files.iterator(); i.hasNext();) {
				String file = (String) i.next();
				if (file.endsWith(type)) {
					try {
						InputStreamReader in = new InputStreamReader(themeSource.getResource(theme.getName() + "/" + file));
						
						char[] b = new char[DEFAULT_BUFFER_SIZE];
						int read = 0;
						while((read = in.read(b, 0, DEFAULT_BUFFER_SIZE)) > 0)
							out.write(b,0,read);
					} catch (ThemeException e) {
						e.printStackTrace();
					}
				}
			}
		}
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
	 * Looks for default theme JAR file.
	 * 
	 * @param fileList
	 * @return Jar file or null if not found.
	 */
	private File findDefaultThemeJar(String[] fileList) {

		// Try to find the default theme JAR file based on the given path
		for (int i = 0; i < fileList.length; i++) {
			String path = getResourcePath(getServletContext(), fileList[i]);
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
		String path = getResourcePath(getServletContext(), "/WEB-INF/lib");
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
	 * Creates a temporary file for given stream.
	 * 
	 * @param stream
	 *            the Stream to be stored into temporary file.
	 * @param extension
	 *            the File type extension.
	 * @return the temporary File.
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
			HttpServletResponse response) throws ServletException {

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
		
		if (resourceId.endsWith("compiledstyle.css") || resourceId.endsWith("compiledjavascript.js")) {
			String[] parts = resourceId.split("/");
			
			Theme t = themeSource.getThemeByName(parts[2]);
			try {
				if(resourceId.endsWith("compiledstyle.css")) {
					writeCss(response, WebBrowserProbe.getTerminalType(request
							.getSession()), t);
				} else {
					writeJavascript(response, WebBrowserProbe.getTerminalType(request
							.getSession()), t);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		}
		if(resourceId.endsWith("compiledjavascript.js")) {
			return true;
		}

		// Checks the resource type
		resourceId = resourceId.substring(RESOURCE_URI.length());
		InputStream data = null;
		// Gets theme resources
		try {
			data = themeSource.getResource(resourceId);
		} catch (ThemeSource.ThemeException e) {
			Log.info(e.getMessage());
			data = null;
		}

		// Writes the response
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
	 * Gets the variable map for the session.
	 * 
	 * @param application
	 * @param request
	 *            the HTTP request.
	 * @return the variable map.
	 * 
	 */
	private static synchronized HttpVariableMap getVariableMap(
			Application application, HttpServletRequest request) {

		HttpSession session = request.getSession();

		// Gets the application to variablemap map
		Map varMapMap = (Map) session.getAttribute(SESSION_ATTR_VARMAP);
		if (varMapMap == null) {
			varMapMap = new WeakHashMap();
			session.setAttribute(SESSION_ATTR_VARMAP, varMapMap);
		}

		// Creates a variable map, if it does not exists.
		HttpVariableMap variableMap = (HttpVariableMap) varMapMap
				.get(application);
		if (variableMap == null) {
			variableMap = new HttpVariableMap();
			varMapMap.put(application, variableMap);
		}

		return variableMap;
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

			// Listens to window add/removes (for web mode)
			application.addListener((Application.WindowAttachListener) this);
			application.addListener((Application.WindowDetachListener) this);

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
			Application application, Map params) throws ServletException {

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
		// Creates and open new debug window for application if requested
		Window debugWindow = application.getWindow(DebugWindow.WINDOW_NAME);
		if (debugWindow == null) {
			if (isDebugMode(params)
					&& WebBrowserProbe.getTerminalType(request.getSession())
							.getRenderingMode() != WebBrowser.RENDERING_MODE_AJAX) {
				try {
					debugWindow = new DebugWindow(application, request
							.getSession(false), this);
					debugWindow.setWidth(370);
					debugWindow.setHeight(480);
					application.addWindow(debugWindow);
				} catch (Exception e) {
					throw new ServletException(
							"Failed to create debug window for application", e);
				}
			}
		} else if (window != debugWindow) {
			if (isDebugMode(params))
				debugWindow.requestRepaint();
			else
				application.removeWindow(debugWindow);
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
	 * Returns the theme source.
	 * 
	 * @return ThemeSource
	 */
	public ThemeSource getThemeSource() {
		return themeSource;
	}

	/**
	 * 
	 * @param application
	 * @param window
	 */
	protected void addDirtyWindow(Application application, Window window) {
		synchronized (applicationToDirtyWindowSetMap) {
			HashMap dirtyWindows = (HashMap) applicationToDirtyWindowSetMap
					.get(application);
			if (dirtyWindows == null) {
				dirtyWindows = new HashMap();
				applicationToDirtyWindowSetMap.put(application, dirtyWindows);
			}
			dirtyWindows.put(window, Boolean.TRUE);
		}
	}

	/**
	 * 
	 * @param application
	 * @param window
	 */
	protected void removeDirtyWindow(Application application, Window window) {
		synchronized (applicationToDirtyWindowSetMap) {
			HashMap dirtyWindows = (HashMap) applicationToDirtyWindowSetMap
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

		// Adds dirty window reference for closing the window
		addDirtyWindow(event.getApplication(), event.getWindow());
	}

	/**
	 * Receives repaint request events.
	 * 
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

	/**
	 * Gets the list of dirty windows in application.
	 * 
	 * @param app
	 * @return
	 */
	protected Map getDirtyWindows(Application app) {
		HashMap dirtyWindows;
		synchronized (applicationToDirtyWindowSetMap) {
			dirtyWindows = (HashMap) applicationToDirtyWindowSetMap.get(app);
		}
		return (Map) dirtyWindows;
	}

	/**
	 * Removes a window from the list of dirty windows.
	 * 
	 * @param app
	 * @param window
	 */
	private void windowPainted(Application app, Window window) {
		removeDirtyWindow(app, window);
	}

	/**
	 * Generates server commands stream. If the server commands are not
	 * requested, return false.
	 * 
	 * @param request
	 *            the HTTP request instance.
	 * @param response
	 *            the HTTP response to write to.
	 */
	private boolean handleServerCommands(HttpServletRequest request,
			HttpServletResponse response) {

		// Server commands are allways requested with certain parameter
		if (request.getParameter(SERVER_COMMAND_PARAM) == null)
			return false;

		// Gets the application
		Application application;
		try {
			application = getApplication(request);
		} catch (MalformedURLException e) {
			return false;
		}
		if (application == null)
			return false;

		// Creates continuous server commands stream
		try {

			// Writer for writing the stream
			PrintWriter w = new PrintWriter(response.getOutputStream());

			// Prints necessary http page headers and padding
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
					Date lastRequest;
					synchronized (applicationToLastRequestDate) {
						lastRequest = (Date) applicationToLastRequestDate
								.get(application);
					}
					if (lastRequest != null
							&& lastRequest.getTime()
									+ request.getSession()
											.getMaxInactiveInterval() * 1000 < System
									.currentTimeMillis()) {

						// Session expired, close application
						application.close();
					} else {

						// Application still alive - keep updating windows
						Map dws = getDirtyWindows(application);
						if (dws != null && !dws.isEmpty()) {

							// For one of the dirty windows (in each
							// application)
							// request redraw
							Window win = (Window) dws.keySet().iterator()
									.next();
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

				// Sends the generated commands and newline immediately to
				// browser
				// TODO why space in here? why not plain ln?
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

						// Stops application server commands stream
						Object lock = applicationToServerCommandStreamLock
								.get(apps[i]);
						if (lock != null)
							synchronized (lock) {
								lock.notifyAll();
							}

						// Remove application from hashmaps
						synchronized (applicationToServerCommandStreamLock) {
							applicationToServerCommandStreamLock
									.remove(apps[i]);
						}
						synchronized (applicationToDirtyWindowSetMap) {
							applicationToDirtyWindowSetMap.remove(apps[i]);
						}
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
	private AjaxApplicationManager getApplicationManager(Application application) {
		AjaxApplicationManager mgr = (AjaxApplicationManager) applicationToAjaxAppMgrMap
				.get(application);

		// This application is going from Web to AJAX mode, create new manager
		if (mgr == null) {
			// Creates new manager
			mgr = new AjaxApplicationManager(application);
			applicationToAjaxAppMgrMap.put(application, mgr);

			// Stops sending changes to this servlet because manager will take
			// control
			application.removeListener((Application.WindowAttachListener) this);
			application.removeListener((Application.WindowDetachListener) this);

			// Deregister all window listeners
			for (Iterator wins = application.getWindows().iterator(); wins
					.hasNext();)
				((Window) wins.next())
						.removeListener((Paintable.RepaintRequestListener) this);

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