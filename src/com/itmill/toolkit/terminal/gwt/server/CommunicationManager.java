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
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// wrapped to prevent conflicts with possible developer required Apache JARs
import com.itmill.toolkit.external.org.apache.commons.fileupload.FileItemIterator;
import com.itmill.toolkit.external.org.apache.commons.fileupload.FileItemStream;
import com.itmill.toolkit.external.org.apache.commons.fileupload.FileUploadException;
import com.itmill.toolkit.external.org.apache.commons.fileupload.ProgressListener;
import com.itmill.toolkit.external.org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.Application.WindowAttachEvent;
import com.itmill.toolkit.Application.WindowDetachEvent;
import com.itmill.toolkit.terminal.DownloadStream;
import com.itmill.toolkit.terminal.Paintable;
import com.itmill.toolkit.terminal.URIHandler;
import com.itmill.toolkit.terminal.UploadStream;
import com.itmill.toolkit.terminal.VariableOwner;
import com.itmill.toolkit.terminal.Paintable.RepaintRequestEvent;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.FrameWindow;
import com.itmill.toolkit.ui.Upload;
import com.itmill.toolkit.ui.Window;

/**
 * Application manager processes changes and paints for single application
 * instance.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 5.0
 */
public class CommunicationManager implements Paintable.RepaintRequestListener,
		Application.WindowAttachListener, Application.WindowDetachListener {

	private static String GET_PARAM_REPAINT_ALL = "repaintAll";

	private static int DEFAULT_BUFFER_SIZE = 32 * 1024;

	private static int MAX_BUFFER_SIZE = 64 * 1024;

	private HashSet dirtyPaintabletSet = new HashSet();

	private WeakHashMap paintableIdMap = new WeakHashMap();

	private WeakHashMap idPaintableMap = new WeakHashMap();

	private int idSequence = 0;

	private Application application;

	private Set removedWindows = new HashSet();

	private JsonPaintTarget paintTarget;

	private List locales;

	private int pendingLocalesIndex;

	private ApplicationServlet applicationServlet;

	public CommunicationManager(Application application,
			ApplicationServlet applicationServlet) {
		this.application = application;
		this.applicationServlet = applicationServlet;
		requireLocale(application.getLocale().toString());
	}

	/**
	 * 
	 * 
	 */
	public void takeControl() {
		application.addListener((Application.WindowAttachListener) this);
		application.addListener((Application.WindowDetachListener) this);

	}

	/**
	 * 
	 * 
	 */
	public void releaseControl() {
		application.removeListener((Application.WindowAttachListener) this);
		application.removeListener((Application.WindowDetachListener) this);
	}

	/**
	 * Handles file upload request submitted via Upload component.
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	public void handleFileUpload(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload();

		UploadProgressListener pl = new UploadProgressListener();

		upload.setProgressListener(pl);

		// Parse the request
		FileItemIterator iter;

		try {
			iter = upload.getItemIterator(request);
			/*
			 * ATM this loop is run only once as we are uploading one file per
			 * request.
			 */
			while (iter.hasNext()) {
				FileItemStream item = iter.next();
				String name = item.getFieldName();
				final String filename = item.getName();
				final String mimeType = item.getContentType();
				final InputStream stream = item.openStream();
				if (item.isFormField()) {
					// ignored, upload requests contian only files
				} else {
					String pid = name.split("_")[0];
					Upload uploadComponent = (Upload) idPaintableMap.get(pid);
					if (uploadComponent == null) {
						throw new FileUploadException(
								"Upload component not found");
					}
					synchronized (application) {
						// put upload component into receiving state
						uploadComponent.startUpload();
					}
					UploadStream upstream = new UploadStream() {

						public String getContentName() {
							return filename;
						}

						public String getContentType() {
							return mimeType;
						}

						public InputStream getStream() {
							return stream;
						}

						public String getStreamName() {
							return "stream";
						}

					};

					// tell UploadProgressListener which component is receiving
					// file
					pl.setUpload(uploadComponent);

					uploadComponent.receiveUpload(upstream);
				}
			}
		} catch (FileUploadException e) {
			e.printStackTrace();
		}

		// Send short response to acknowledge client that request was done
		response.setContentType("text/html");
		OutputStream out = response.getOutputStream();
		PrintWriter outWriter = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(out, "UTF-8")));
		outWriter.print("<html><body>download handled</body></html>");
		outWriter.flush();
		out.close();
	}

	/**
	 * Handles UIDL request
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	public void handleUidlRequest(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		// repaint requested or session has timed out and new one is created
		boolean repaintAll = (request.getParameter(GET_PARAM_REPAINT_ALL) != null)
				|| request.getSession().isNew();

		// If repaint is requested, clean all ids
		if (repaintAll) {
			idPaintableMap.clear();
			paintableIdMap.clear();
		}

		OutputStream out = response.getOutputStream();
		PrintWriter outWriter = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(out, "UTF-8")));

		try {

			// Is this a download request from application
			DownloadStream download = null;

			// The rest of the process is synchronized with the application
			// in order to guarantee that no parallel variable handling is
			// made
			synchronized (application) {

				// Change all variables based on request parameters
				Map unhandledParameters = handleVariables(request, application);

				// Handles the URI if the application is still running
				if (application.isRunning())
					download = handleURI(application, request, response);

				// If this is not a download request
				if (download == null) {

					// Finds the window within the application
					Window window = null;
					if (application.isRunning())
						window = getApplicationWindow(request, application);

					// Handles the unhandled parameters if the application is
					// still running
					if (window != null && unhandledParameters != null
							&& !unhandledParameters.isEmpty())
						window.handleParameters(unhandledParameters);

					// Removes application if it has stopped
					if (!application.isRunning()) {
						endApplication(request, response, application);
						return;
					}

					// Returns if no window found
					if (window == null)
						return;

					// Sets the response type
					response.setContentType("application/json; charset=UTF-8");
					// some dirt to prevent cross site scripting
					outWriter.print(")/*{");

					outWriter.print("\"changes\":[");

					paintTarget = new JsonPaintTarget(this, outWriter,
							!repaintAll);

					// Paints components
					Set paintables;
					if (repaintAll) {
						paintables = new LinkedHashSet();
						paintables.add(window);

						// Reset sent locales
						locales = null;
						requireLocale(application.getLocale().toString());

					} else
						paintables = getDirtyComponents();
					if (paintables != null) {

						// Creates "working copy" of the current state.
						List currentPaintables = new ArrayList(paintables);

						// Sorts the paintable so that parent windows
						// are always painted before child windows
						Collections.sort(currentPaintables, new Comparator() {

							public int compare(Object o1, Object o2) {

								// If first argumement is now window
								// the second is "smaller" if it is.
								if (!(o1 instanceof Window)) {
									return (o2 instanceof Window) ? 1 : 0;
								}

								// Now, if second is not window the
								// first is smaller.
								if (!(o2 instanceof Window)) {
									return -1;
								}

								// Both are windows.
								String n1 = ((Window) o1).getName();
								String n2 = ((Window) o2).getName();
								if (o1 instanceof FrameWindow) {
									if (((FrameWindow) o1).getFrameset()
											.getFrame(n2) != null) {
										return -1;
									} else if (!(o2 instanceof FrameWindow)) {
										return -1;
									}
								}
								if (o2 instanceof FrameWindow) {
									if (((FrameWindow) o2).getFrameset()
											.getFrame(n1) != null) {
										return 1;
									} else if (!(o1 instanceof FrameWindow)) {
										return 1;
									}
								}

								return 0;
							}
						});

						for (Iterator i = currentPaintables.iterator(); i
								.hasNext();) {
							Paintable p = (Paintable) i.next();

							// TODO CLEAN
							if (p instanceof Window) {
								Window w = (Window) p;
								if (w.getTerminal() == null)
									w.setTerminal(application.getMainWindow()
											.getTerminal());
							}
							/*
							 * This does not seem to happen in tk5, but remember
							 * this case: else if (p instanceof Component) { if
							 * (((Component) p).getParent() == null ||
							 * ((Component) p).getApplication() == null) { //
							 * Component requested repaint, but is no // longer
							 * attached: skip paintablePainted(p); continue; } }
							 */
							paintTarget.startTag("change");
							paintTarget.addAttribute("format", "uidl");
							String pid = getPaintableId(p);
							paintTarget.addAttribute("pid", pid);

							// Track paints to identify empty paints
							paintTarget.setTrackPaints(true);
							p.paint(paintTarget);

							// If no paints add attribute empty
							if (paintTarget.getNumberOfPaints() <= 0) {
								paintTarget.addAttribute("visible", false);
							}
							paintTarget.endTag("change");
							paintablePainted(p);
						}
					}

					paintTarget.close();
					outWriter.print("]"); // close changes

					outWriter.print(", \"meta\" : {");
					boolean metaOpen = false;

					// add meta instruction for client to set focus if it is set
					Paintable f = (Paintable) application.consumeFocus();
					if (f != null) {
						if (metaOpen)
							outWriter.write(",");
						outWriter.write("\"focus\":\"" + getPaintableId(f)
								+ "\"");
					}

					outWriter.print("}, \"resources\" : {");

					// Precache custom layouts
					String themeName = window.getTheme();
					if (request.getParameter("theme") != null) {
						themeName = request.getParameter("theme");
					}
					if (themeName == null)
						themeName = "default";

					// TODO We should only precache the layouts that are not
					// cached already
					int resourceIndex = 0;
					for (Iterator i = paintTarget.getPreCachedResources()
							.iterator(); i.hasNext();) {
						String resource = (String) i.next();
						InputStream is = null;
						try {
							is = applicationServlet
									.getServletContext()
									.getResourceAsStream(
											"/"
													+ ApplicationServlet.THEME_DIRECTORY_PATH
													+ themeName + "/"
													+ resource);
						} catch (Exception e) {
							e.printStackTrace();
							Log.info(e.getMessage());
						}
						if (is != null) {

							outWriter.print((resourceIndex++ > 0 ? ", " : "")
									+ "\"" + resource + "\" : ");
							StringBuffer layout = new StringBuffer();

							try {
								InputStreamReader r = new InputStreamReader(is);
								char[] buffer = new char[20000];
								int charsRead = 0;
								while ((charsRead = r.read(buffer)) > 0)
									layout.append(buffer, 0, charsRead);
								r.close();
							} catch (java.io.IOException e) {
								Log.info("Resource transfer failed:  "
										+ request.getRequestURI() + ". ("
										+ e.getMessage() + ")");
							}
							outWriter.print("\""
									+ JsonPaintTarget.escapeJSON(layout
											.toString()) + "\"");
						}
					}
					outWriter.print("}");

					printLocaleDeclarations(outWriter);

					outWriter.flush();
					outWriter.close();
					out.flush();
				} else {

					// For download request, transfer the downloaded data
					handleDownload(download, request, response);
				}
			}

			out.flush();
			out.close();

		} catch (Throwable e) {
			// Writes the error report to client
			OutputStreamWriter w = new OutputStreamWriter(out);
			PrintWriter err = new PrintWriter(w);
			err
					.write("<html><head><title>Application Internal Error</title></head><body>");
			err.write("<h1>" + e.toString() + "</h1><pre>\n");
			e.printStackTrace(new PrintWriter(err));
			err.write("\n</pre></body></html>");
			err.close();
		} finally {

		}

	}

	private Map handleVariables(HttpServletRequest request,
			Application application2) {

		Map params = new HashMap(request.getParameterMap());
		String changes = (String) ((params.get("changes") instanceof String[]) ? ((String[]) params
				.get("changes"))[0]
				: params.get("changes"));
		params.remove("changes");
		if (changes != null) {
			String[] ca = changes.split("\u0001");
			for (int i = 0; i < ca.length; i++) {
				String[] vid = ca[i].split("_");
				VariableOwner owner = (VariableOwner) idPaintableMap
						.get(vid[0]);
				if (owner != null) {
					Map m;
					if (i + 2 >= ca.length
							|| !vid[0].equals(ca[i + 2].split("_")[0])) {
						if (ca.length > i + 1) {
							m = new SingleValueMap(vid[1],
									convertVariableValue(vid[2].charAt(0),
											ca[++i]));
						} else {
							m = new SingleValueMap(vid[1],
									convertVariableValue(vid[2].charAt(0), ""));
						}
					} else {
						m = new HashMap();
						m.put(vid[1], convertVariableValue(vid[2].charAt(0),
								ca[++i]));
					}
					while (i + 1 < ca.length
							&& vid[0].equals(ca[i + 1].split("_")[0])) {
						vid = ca[++i].split("_");
						m.put(vid[1], convertVariableValue(vid[2].charAt(0),
								ca[++i]));
					}
					owner.changeVariables(request, m);
				}
			}
		}

		return params;
	}

	private Object convertVariableValue(char variableType, String strValue) {
		Object val = null;
		switch (variableType) {
		case 'a':
			val = strValue.split(",");
			break;
		case 's':
			val = strValue;
			break;
		case 'i':
			val = Integer.valueOf(strValue);
			break;
		case 'l':
			val = Long.valueOf(strValue);
		case 'f':
			val = Float.valueOf(strValue);
			break;
		case 'd':
			val = Double.valueOf(strValue);
			break;
		case 'b':
			val = Boolean.valueOf(strValue);
			break;
		}

		return val;
	}

	private void printLocaleDeclarations(PrintWriter outWriter) {
		/*
		 * ----------------------------- Sending Locale sensitive date
		 * -----------------------------
		 */

		// Store JVM default locale for later restoration
		// (we'll have to change the default locale for a while)
		Locale jvmDefault = Locale.getDefault();

		// Send locale informations to client
		outWriter.print(", \"locales\":[");
		for (; pendingLocalesIndex < locales.size(); pendingLocalesIndex++) {

			Locale l = generateLocale((String) locales.get(pendingLocalesIndex));
			// Locale name
			outWriter.print("{\"name\":\"" + l.toString() + "\",");

			/*
			 * Month names (both short and full)
			 */
			DateFormatSymbols dfs = new DateFormatSymbols(l);
			String[] short_months = dfs.getShortMonths();
			String[] months = dfs.getMonths();
			outWriter.print("\"smn\":[\""
					+ // ShortMonthNames
					short_months[0] + "\",\"" + short_months[1] + "\",\""
					+ short_months[2] + "\",\"" + short_months[3] + "\",\""
					+ short_months[4] + "\",\"" + short_months[5] + "\",\""
					+ short_months[6] + "\",\"" + short_months[7] + "\",\""
					+ short_months[8] + "\",\"" + short_months[9] + "\",\""
					+ short_months[10] + "\",\"" + short_months[11] + "\""
					+ "],");
			outWriter.print("\"mn\":[\""
					+ // MonthNames
					months[0] + "\",\"" + months[1] + "\",\"" + months[2]
					+ "\",\"" + months[3] + "\",\"" + months[4] + "\",\""
					+ months[5] + "\",\"" + months[6] + "\",\"" + months[7]
					+ "\",\"" + months[8] + "\",\"" + months[9] + "\",\""
					+ months[10] + "\",\"" + months[11] + "\"" + "],");

			/*
			 * Weekday names (both short and full)
			 */
			String[] short_days = dfs.getShortWeekdays();
			String[] days = dfs.getWeekdays();
			outWriter.print("\"sdn\":[\""
					+ // ShortDayNames
					short_days[1] + "\",\"" + short_days[2] + "\",\""
					+ short_days[3] + "\",\"" + short_days[4] + "\",\""
					+ short_days[5] + "\",\"" + short_days[6] + "\",\""
					+ short_days[7] + "\"" + "],");
			outWriter.print("\"dn\":[\""
					+ // DayNames
					days[1] + "\",\"" + days[2] + "\",\"" + days[3] + "\",\""
					+ days[4] + "\",\"" + days[5] + "\",\"" + days[6] + "\",\""
					+ days[7] + "\"" + "],");

			/*
			 * First day of week (0 = sunday, 1 = monday)
			 */
			Calendar cal = new GregorianCalendar(l);
			outWriter.print("\"fdow\":" + (cal.getFirstDayOfWeek() - 1) + ",");

			/*
			 * Date formatting (MM/DD/YYYY etc.)
			 */
			// Force our locale as JVM default for a while (SimpleDateFormat
			// uses JVM default)
			Locale.setDefault(l);
			String df = new SimpleDateFormat().toPattern();
			int timeStart = df.indexOf("H");
			if (timeStart < 0)
				timeStart = df.indexOf("h");
			int ampm_first = df.indexOf("a");
			// E.g. in Korean locale AM/PM is before h:mm
			// TODO should take that into consideration on client-side as well,
			// now always h:mm a
			if (ampm_first > 0 && ampm_first < timeStart)
				timeStart = ampm_first;
			String dateformat = df.substring(0, timeStart - 1);

			outWriter.print("\"df\":\"" + dateformat.trim() + "\",");

			/*
			 * Time formatting (24 or 12 hour clock and AM/PM suffixes)
			 */
			String timeformat = df.substring(timeStart, df.length()); // Doesn't
			// return
			// second
			// or
			// milliseconds
			// We use timeformat to determine 12/24-hour clock
			boolean twelve_hour_clock = timeformat.indexOf("a") > -1;
			// TODO there are other possibilities as well, like 'h' in french
			// (ignore them, too complicated)
			String hour_min_delimiter = timeformat.indexOf(".") > -1 ? "."
					: ":";
			// outWriter.print("\"tf\":\"" + timeformat + "\",");
			outWriter.print("\"thc\":" + twelve_hour_clock + ",");
			outWriter.print("\"hmd\":\"" + hour_min_delimiter + "\"");
			if (twelve_hour_clock) {
				String[] ampm = dfs.getAmPmStrings();
				outWriter.print(",\"ampm\":[\"" + ampm[0] + "\",\"" + ampm[1]
						+ "\"]");
			}
			outWriter.print("}");
			if (pendingLocalesIndex < locales.size() - 1)
				outWriter.print(",");
		}
		outWriter.print("]"); // Close locales

		// Restore JVM default locale
		Locale.setDefault(jvmDefault);
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

		// Find the window where the request is handled
		String path = request.getPathInfo();

		// Remove UIDL from the path
		path = path.substring("/UIDL".length());

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

			// By default, we use main window
			if (window == null)
				window = application.getMainWindow();
		}

		return window;
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
	 *         further processing should be suppressed, otherwise
	 *         <code>false</code>.
	 * @see com.itmill.toolkit.terminal.URIHandler
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
	 * Handles the requested URI. An application can add handlers to do special
	 * processing, when a certain URI is requested. The handlers are invoked
	 * before any windows URIs are processed and if a DownloadStream is returned
	 * it is sent to the client.
	 * 
	 * @param stream
	 *            the downloadable stream.
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
	 * Ends the Application.
	 * 
	 * @param request
	 *            the HTTP request instance.
	 * @param response
	 *            the HTTP response to write to.
	 * @param application
	 *            the Application to end.
	 * @throws IOException
	 *             if the writing failed due to input/output error.
	 */
	private void endApplication(HttpServletRequest request,
			HttpServletResponse response, Application application)
			throws IOException {

		String logoutUrl = application.getLogoutURL();
		if (logoutUrl == null)
			logoutUrl = application.getURL().toString();
		// clients JS app is still running, send a special json file to
		// tell client that application has quit and where to point browser now
		// Set the response type
		response.setContentType("application/json; charset=UTF-8");
		ServletOutputStream out = response.getOutputStream();
		PrintWriter outWriter = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(out, "UTF-8")));
		outWriter.print(")/*{");
		outWriter.print("\"redirect\":{");
		outWriter.write("\"url\":\"" + logoutUrl + "\"}");
		outWriter.flush();
		outWriter.close();
		out.flush();
	}

	/**
	 * Gets the Paintable Id.
	 * 
	 * @param paintable
	 * @return the paintable Id.
	 */
	public synchronized String getPaintableId(Paintable paintable) {

		String id = (String) paintableIdMap.get(paintable);
		if (id == null) {
			id = "PID" + Integer.toString(idSequence++);
			paintableIdMap.put(paintable, id);
			idPaintableMap.put(id, paintable);
		}

		return id;
	}

	public synchronized boolean hasPaintableId(Paintable paintable) {

		return paintableIdMap.containsKey(paintable);
	}

	/**
	 * 
	 * @return
	 */
	public synchronized Set getDirtyComponents() {
		HashSet resultset = new HashSet(dirtyPaintabletSet);

		// The following algorithm removes any components that would be painted
		// as
		// a direct descendant of other components from the dirty components
		// list.
		// The result is that each component should be painted exactly once and
		// any unmodified components will be painted as "cached=true".

		for (Iterator i = dirtyPaintabletSet.iterator(); i.hasNext();) {
			Paintable p = (Paintable) i.next();
			if (p instanceof Component) {
				if (dirtyPaintabletSet.contains(((Component) p).getParent()))
					resultset.remove(p);
			}
		}

		return resultset;
	}

	/**
	 * @see com.itmill.toolkit.terminal.Paintable.RepaintRequestListener#repaintRequested(com.itmill.toolkit.terminal.Paintable.RepaintRequestEvent)
	 */
	public void repaintRequested(RepaintRequestEvent event) {
		Paintable p = event.getPaintable();
		dirtyPaintabletSet.add(p);

	}

	/**
	 * 
	 * @param p
	 */
	public void paintablePainted(Paintable p) {
		dirtyPaintabletSet.remove(p);
		p.requestRepaintRequests();
	}

	/**
	 * @see com.itmill.toolkit.Application.WindowAttachListener#windowAttached(com.itmill.toolkit.Application.WindowAttachEvent)
	 */
	public void windowAttached(WindowAttachEvent event) {
		event.getWindow().addListener(this);
		dirtyPaintabletSet.add(event.getWindow());
	}

	/**
	 * @see com.itmill.toolkit.Application.WindowDetachListener#windowDetached(com.itmill.toolkit.Application.WindowDetachEvent)
	 */
	public void windowDetached(WindowDetachEvent event) {
		event.getWindow().removeListener(this);
		// Notify client of the close operation
		removedWindows.add(event.getWindow());
	}

	/**
	 * 
	 * @return
	 */
	public synchronized Set getRemovedWindows() {
		return Collections.unmodifiableSet(removedWindows);

	}

	/**
	 * 
	 * @param w
	 */
	private void removedWindowNotified(Window w) {
		this.removedWindows.remove(w);
	}

	private final class SingleValueMap implements Map {
		private final String name;

		private final Object value;

		private SingleValueMap(String name, Object value) {
			this.name = name;
			this.value = value;
		}

		public void clear() {
			throw new UnsupportedOperationException();
		}

		public boolean containsKey(Object key) {
			if (name == null)
				return key == null;
			return name.equals(key);
		}

		public boolean containsValue(Object v) {
			if (value == null)
				return v == null;
			return value.equals(v);
		}

		public Set entrySet() {
			Set s = new HashSet();
			s.add(new Map.Entry() {

				public Object getKey() {
					return name;
				}

				public Object getValue() {
					return value;
				}

				public Object setValue(Object value) {
					throw new UnsupportedOperationException();
				}
			});
			return s;
		}

		public Object get(Object key) {
			if (!name.equals(key))
				return null;
			return value;
		}

		public boolean isEmpty() {
			return false;
		}

		public Set keySet() {
			Set s = new HashSet();
			s.add(name);
			return s;
		}

		public Object put(Object key, Object value) {
			throw new UnsupportedOperationException();
		}

		public void putAll(Map t) {
			throw new UnsupportedOperationException();
		}

		public Object remove(Object key) {
			throw new UnsupportedOperationException();
		}

		public int size() {
			return 1;
		}

		public Collection values() {
			LinkedList s = new LinkedList();
			s.add(value);
			return s;

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

	public void requireLocale(String value) {
		if (locales == null) {
			locales = new ArrayList();
			locales.add(application.getLocale().toString());
			pendingLocalesIndex = 0;
		}
		if (!locales.contains(value))
			locales.add(value);
	}

	private Locale generateLocale(String value) {
		String[] temp = value.split("_");
		if (temp.length == 1)
			return new Locale(temp[0]);
		else if (temp.length == 2)
			return new Locale(temp[0], temp[1]);
		else
			return new Locale(temp[0], temp[1], temp[2]);
	}

	/*
	 * Upload progress listener notifies upload component once when Jakarta
	 * FileUpload can determine content length. Used to detect files total size,
	 * uploads progress can be tracked inside upload.
	 */
	private class UploadProgressListener implements ProgressListener {
		Upload uploadComponent;

		boolean updated = false;

		public void setUpload(Upload u) {
			uploadComponent = u;
		}

		public void update(long bytesRead, long contentLength, int items) {
			if (!updated && uploadComponent != null) {
				uploadComponent.setUploadSize(contentLength);
				updated = true;
			}
		}
	}

}
