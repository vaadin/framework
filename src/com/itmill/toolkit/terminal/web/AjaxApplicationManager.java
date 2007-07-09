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
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.Application.WindowAttachEvent;
import com.itmill.toolkit.Application.WindowDetachEvent;
import com.itmill.toolkit.terminal.DownloadStream;
import com.itmill.toolkit.terminal.PaintTarget;
import com.itmill.toolkit.terminal.Paintable;
import com.itmill.toolkit.terminal.URIHandler;
import com.itmill.toolkit.terminal.Paintable.RepaintRequestEvent;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.FrameWindow;
import com.itmill.toolkit.ui.Window;

/**
 * Application manager processes changes and paints for single application
 * instance.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.1
 */
public class AjaxApplicationManager implements
		Paintable.RepaintRequestListener, Application.WindowAttachListener,
		Application.WindowDetachListener {

	private static String GET_PARAM_REPAINT_ALL = "repaintAll";

	private static int DEFAULT_BUFFER_SIZE = 32 * 1024;

	private static int MAX_BUFFER_SIZE = 64 * 1024;

	private WeakHashMap applicationToVariableMapMap = new WeakHashMap();

	private HashSet dirtyPaintabletSet = new HashSet();

	// TODO THIS TEMPORARY HACK IS ONLY HERE TO MAKE GWT DEVEL EASIER
    static WeakHashMap paintableIdMap = new WeakHashMap();

	private int idSequence = 0;

	private Application application;

	private Set removedWindows = new HashSet();

	private PaintTarget paintTarget;
	
	private List locales;
	
	private int pendingLocalesIndex;

	public AjaxApplicationManager(Application application) {
		this.application = application;
		requireLocale(application.getLocale().toString());
	}

	/**
	 * 
	 * @return
	 */
	private AjaxVariableMap getVariableMap() {
		AjaxVariableMap vm = (AjaxVariableMap) applicationToVariableMapMap
				.get(application);
		if (vm == null) {
			vm = new AjaxVariableMap();
			applicationToVariableMapMap.put(application, vm);
		}
		return vm;
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

	
	public void handleUidlRequest(HttpServletRequest request,
			HttpServletResponse response, ThemeSource themeSource) throws IOException {
		handleUidlRequest(request,
				response, themeSource, false); 
		
	}

	
	/**
	 * 
	 * @param request
	 *            the HTTP Request.
	 * @param response
	 *            the HTTP Response.
	 * @throws IOException
	 *             if the writing failed due to input/output error.
	 */
	public void handleUidlRequest(HttpServletRequest request,
			HttpServletResponse response, ThemeSource themeSource, boolean isJson) 
		throws IOException {

		// repaint requested or sesssion has timed out and new one is created
		boolean repaintAll = (request.getParameter(GET_PARAM_REPAINT_ALL) != null)
				|| request.getSession().isNew();

		OutputStream out = response.getOutputStream();
		PrintWriter outWriter = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(out, "UTF-8")));
		
		outWriter.print(")/*{"); // some dirt to prevent cross site scripting vulnerabilities

		try {

			// Is this a download request from application
			DownloadStream download = null;

			// The rest of the process is synchronized with the application
			// in order to guarantee that no parallel variable handling is
			// made
			synchronized (application) {

				// Change all variables based on request parameters
				Map unhandledParameters = getVariableMap().handleVariables(
						request, application);

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
					outWriter.print("\"changes\":[");
					
					paintTarget = new AjaxJsonPaintTarget(getVariableMap(), 
							this, outWriter);

					// Paints components
					Set paintables;
					if (repaintAll) {
						paintables = new LinkedHashSet();
						paintables.add(window);
						
						// Reset sent locales
						locales = null;
						requireLocale(application.getLocale().toString());

						// Adds all non-native windows
						for (Iterator i = window.getApplication().getWindows()
								.iterator(); i.hasNext();) {
							Window w = (Window) i.next();
							if (!"native".equals(w.getStyle()) && w != window)
								paintables.add(w);
						}
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

							paintTarget.startTag("change");
							paintTarget.addAttribute("format", "uidl");
							String pid = getPaintableId(p);
							paintTarget.addAttribute("pid", pid);

							// Track paints to identify empty paints
							((AjaxPaintTarget) paintTarget).setTrackPaints(true);
							p.paint(paintTarget);

							// If no paints add attribute empty
							if (((AjaxPaintTarget) paintTarget).getNumberOfPaints() <= 0) {
								paintTarget.addAttribute("visible", false);
							}
							paintTarget.endTag("change");
							paintablePainted(p);
						}
					}

					((AjaxPaintTarget) paintTarget).close();
					outWriter.print("]"); // close changes


					// Render the removed windows
					// TODO refactor commented area to send some meta instructions to close window
//					Set removed = new HashSet(getRemovedWindows());
//					if (removed.size() > 0) {
//						for (Iterator i = removed.iterator(); i.hasNext();) {
//							Window w = (Window) i.next();
//							paintTarget.startTag("change");
//							paintTarget.addAttribute("format", "uidl");
//							String pid = getPaintableId(w);
//							paintTarget.addAttribute("pid", pid);
//							paintTarget.addAttribute("windowname", w.getName());
//							paintTarget.addAttribute("visible", false);
//							paintTarget.endTag("change");
//							removedWindowNotified(w);
//
//						}
//					}


					
                	outWriter.print(", \"meta\" : {");
                	boolean metaOpen = false;

					
                    // .. or initializion (first uidl-request)
                    if(application.ajaxInit()) {
                    	outWriter.print("\"appInit\":true");
                    }
                    // add meta instruction for client to set focus if it is set
                    Paintable f = (Paintable) application.consumeFocus();
                    if(f != null) {
                    	if(metaOpen)
                    		outWriter.append(",");
                    	outWriter.write("\"focus\":\""+ getPaintableId(f) +"\"");
                    }

                	outWriter.print("}, \"resources\" : {");

                    // Precache custom layouts
                    // TODO Does not support theme-get param or different themes in different windows -> Allways preload layouts with the theme specified by the applications
                    String themeName = application.getTheme() != null ? application.getTheme() : ApplicationServlet.DEFAULT_THEME;
                    // TODO We should only precache the layouts that are not cached already
                	int resourceIndex = 0;
                    for (Iterator i=((AjaxPaintTarget) paintTarget).getPreCachedResources().iterator(); i.hasNext();) {
                    	String resource = (String) i.next();
                    	InputStream is = null;
                    	try {
                			is = themeSource.getResource(themeName + "/" +  resource);
                		} catch (ThemeSource.ThemeException e) {
                			Log.info(e.getMessage());
                		}
                    	if (is != null) {
                    		
                        	outWriter.print((resourceIndex++ > 0 ? ", " : "") + "\""+resource + "\" : ");
                    		StringBuffer layout = new StringBuffer();

                    		try {
                        		InputStreamReader r = new InputStreamReader(is);
                    				char[] buffer = new char[20000];
                    				int charsRead = 0;
                    				while ((charsRead = r.read(buffer)) > 0)
                    					layout.append(buffer, 0, charsRead);
                    				r.close();
                    		} catch (java.io.IOException e) {
                    			Log.info("Resource transfer failed:  " + request.getRequestURI()
                    					+ ". (" + e.getMessage() + ")");
                    		}
                    		outWriter.print("\"" + AjaxJsonPaintTarget.escapeJSON(layout.toString()) + "\"");
                    	}
                    }
                	outWriter.print("}");
                	
                	
                	/* -----------------------------
                	 * Sending Locale sensitive date
                	 * -----------------------------
                	 */
                	
                	// Store JVM default locale for later restoration
                	// (we'll have to change the default locale for a while)
            		Locale jvmDefault = Locale.getDefault();
                	
                    // Send locale informations to client
            		outWriter.print(", \"locales\":[");
                	for(;pendingLocalesIndex < locales.size(); pendingLocalesIndex++) {
                		
                		Locale l = generateLocale((String) locales.get(pendingLocalesIndex));
	                	// Locale name
	                	outWriter.print("{\"name\":\"" + l.toString() + "\",");
	                	
	                	/*
	                	 * Month names (both short and full)
	                	 */
	                	DateFormatSymbols dfs = new DateFormatSymbols(l);
	                	String[] short_months = dfs.getShortMonths();
	                	String[] months = dfs.getMonths();
	                  	outWriter.print("\"smn\":[\"" + // ShortMonthNames
	                  			short_months[0] + "\",\"" +
	                  			short_months[1] + "\",\"" +
	                  			short_months[2] + "\",\"" +
	                  			short_months[3] + "\",\"" +
	                  			short_months[4] + "\",\"" +
	                  			short_months[5] + "\",\"" +
	                  			short_months[6] + "\",\"" +
	                  			short_months[7] + "\",\"" +
	                  			short_months[8] + "\",\"" +
	                  			short_months[9] + "\",\"" +
	                  			short_months[10] + "\",\"" +
	                  			short_months[11] + "\"" +
	                  			"],");
	                  	outWriter.print("\"mn\":[\"" + // MonthNames
	                  			months[0] + "\",\"" +
	                  			months[1] + "\",\"" +
	                  			months[2] + "\",\"" +
	                  			months[3] + "\",\"" +
	                  			months[4] + "\",\"" +
	                  			months[5] + "\",\"" +
	                  			months[6] + "\",\"" +
	                  			months[7] + "\",\"" +
	                  			months[8] + "\",\"" +
	                  			months[9] + "\",\"" +
	                  			months[10] + "\",\"" +
	                  			months[11] + "\"" +
	                  			"],");
	
	                    /*
	                     * Weekday names (both short and full)
	                     */
	                  	String[] short_days = dfs.getShortWeekdays();
	                 	String[] days = dfs.getWeekdays();
	                    outWriter.print("\"sdn\":[\"" + // ShortDayNames
	                  			short_days[1] + "\",\"" +
	                  			short_days[2] + "\",\"" +
	                  			short_days[3] + "\",\"" +
	                  			short_days[4] + "\",\"" +
	                  			short_days[5] + "\",\"" +
	                  			short_days[6] + "\",\"" +
	                  			short_days[7] + "\"" +
	                  			"],");
	                  	outWriter.print("\"dn\":[\"" + // DayNames
	                  			days[1] + "\",\"" +
	                  			days[2] + "\",\"" +
	                  			days[3] + "\",\"" +
	                  			days[4] + "\",\"" +
	                  			days[5] + "\",\"" +
	                  			days[6] + "\",\"" +
	                  			days[7] + "\"" +
	                  			"],");
	                  	
	                  	/*
	                  	 * First day of week (0 = sunday, 1 = monday)
	                  	 */
	                  	Calendar cal = new GregorianCalendar(l);
	                  	outWriter.print("\"fdow\":" + (cal.getFirstDayOfWeek() - 1) + ",");
	                  	
	                  	/*
	                  	 * Date formatting (MM/DD/YYYY etc.)
	                  	 */
	                  	// Force our locale as JVM default for a while (SimpleDateFormat uses JVM default)
	                  	Locale.setDefault(l);
	                   	String df = new SimpleDateFormat().toPattern();
	                   	// TODO we suppose all formats separate date and time with a whitespace
	                   	String dateformat = df.substring(0,df.indexOf(" "));
	                  	outWriter.print("\"df\":\"" + dateformat + "\",");
	                  	
	                  	/*
	                  	 * Time formatting (24 or 12 hour clock and AM/PM suffixes)
	                  	 */
	                  	String timeformat = df.substring(df.indexOf(" ")+1, df.length()); // Doesn't return second or milliseconds
	                  	// We use timeformat to determine 12/24-hour clock
	                  	boolean twelve_hour_clock = timeformat.contains("a");
	                  	// TODO there are other possibilities as well, like 'h' in french (ignore them, too complicated)
	                  	String hour_min_delimiter = timeformat.contains(".")? "." : ":";
	                  	//outWriter.print("\"tf\":\"" + timeformat + "\",");
	                  	outWriter.print("\"thc\":" + twelve_hour_clock + ",");
	                  	outWriter.print("\"hmd\":\"" + hour_min_delimiter + "\"");
	                  	if(twelve_hour_clock) {
	                  		String[] ampm = dfs.getAmPmStrings();
	                  		outWriter.print(",\"ampm\":[\""+ampm[0]+"\",\""+ampm[1]+"\"]");
	                  	}
	                  	outWriter.print("}");
	                  	if(pendingLocalesIndex < locales.size()-1)
	                  		outWriter.print(",");
                	}
                	outWriter.print("]"); // Close locales
                	
                  	// Restore JVM default locale
                  	Locale.setDefault(jvmDefault);
                
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
		// clients JS app is still running, send a special xml file to
		// tell client that application is quit and where to point browser now
		// Set the response type
		response.setContentType("application/xml; charset=UTF-8");
		ServletOutputStream out = response.getOutputStream();
		out.println("<redirect url=\"" + logoutUrl + "\">");
		out.println("</redirect>");
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
		}

		return id;
	}

	/**
	 * 
	 * @return
	 */
	public synchronized Set getDirtyComponents() {

		// Remove unnecessary repaints from the list
		Object[] paintables = dirtyPaintabletSet.toArray();
		for (int i = 0; i < paintables.length; i++) {
			if (paintables[i] instanceof Component) {
				Component c = (Component) paintables[i];

				// Check if any of the parents of c already exist in the list
				Component p = c.getParent();
				while (p != null) {
					if (dirtyPaintabletSet.contains(p)) {

						// Remove component c from the dirty paintables as its
						// parent is also dirty
						dirtyPaintabletSet.remove(c);
						p = null;
					} else
						p = p.getParent();
				}
			}
		}

		return Collections.unmodifiableSet(dirtyPaintabletSet);
	}

	/**
	 * Clears the Dirty Components.
	 * 
	 */
	public synchronized void clearDirtyComponents() {
		dirtyPaintabletSet.clear();
	}

	/**
	 * @see com.itmill.toolkit.terminal.Paintable.RepaintRequestListener#repaintRequested(com.itmill.toolkit.terminal.Paintable.RepaintRequestEvent)
	 */
	public void repaintRequested(RepaintRequestEvent event) {
		Paintable p = event.getPaintable();
		dirtyPaintabletSet.add(p);

		// For FrameWindows we mark all frames (windows) dirty
		if (p instanceof FrameWindow) {
			FrameWindow fw = (FrameWindow) p;
			repaintFrameset(fw.getFrameset());
		}
	}

	/**
	 * Recursively request repaint for all frames in frameset.
	 * 
	 * @param fs
	 *            the Framewindow.Frameset.
	 */
	private void repaintFrameset(FrameWindow.Frameset fs) {
		List frames = fs.getFrames();
		for (Iterator i = frames.iterator(); i.hasNext();) {
			FrameWindow.Frame f = (FrameWindow.Frame) i.next();
			if (f instanceof FrameWindow.Frameset) {
				repaintFrameset((FrameWindow.Frameset) f);
			} else {
				Window w = f.getWindow();
				if (w != null) {
					w.requestRepaint();
				}
			}
		}
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
	 * 
	 * @param paintable
	 * @return
	 */
	public boolean isDirty(Paintable paintable) {
		return (dirtyPaintabletSet.contains(paintable));
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
		if(locales == null) {
			locales = new ArrayList();
			locales.add(application.getLocale().toString());
			pendingLocalesIndex = 0;
		}
		if(!locales.contains(value))
				locales.add(value);
	}
	
	private Locale generateLocale(String value) {
		String[] temp = value.split("_");
		if(temp.length == 1)
			return new Locale(temp[0]);
		else if(temp.length == 2)
			return new Locale(temp[0], temp[1]);
		else
			return new Locale(temp[0], temp[1], temp[2]);
	}
}
