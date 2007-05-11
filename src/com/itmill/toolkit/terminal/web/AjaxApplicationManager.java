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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
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
import com.itmill.toolkit.terminal.Identifiable;
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

	private WeakHashMap paintableIdMap = new WeakHashMap();

	private int idSequence = 0;

	private Application application;

	private Set removedWindows = new HashSet();

	private AjaxPaintTarget paintTarget;

	public AjaxApplicationManager(Application application) {
		this.application = application;
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
			HttpServletResponse response, ThemeSource themeSource)
			throws IOException {

		// repaint requested or sesssion has timed out and new one is created
		boolean repaintAll = (request.getParameter(GET_PARAM_REPAINT_ALL) != null)
				|| request.getSession().isNew();

		OutputStream out = response.getOutputStream();
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
					response.setContentType("application/xml; charset=UTF-8");

					paintTarget = new AjaxPaintTarget(getVariableMap(), this,
							out);

					// Render the removed windows
					Set removed = new HashSet(getRemovedWindows());
					if (removed.size() > 0) {
						for (Iterator i = removed.iterator(); i.hasNext();) {
							Window w = (Window) i.next();
							paintTarget.startTag("change");
							paintTarget.addAttribute("format", "uidl");
							String pid = getPaintableId(w);
							paintTarget.addAttribute("pid", pid);
							paintTarget.addAttribute("windowname", w.getName());
							paintTarget.addAttribute("visible", false);
							paintTarget.endTag("change");
							removedWindowNotified(w);

						}
					}

					// Paints components
					Set paintables;
					if (repaintAll) {
						paintables = new LinkedHashSet();
						paintables.add(window);

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

					// add meta instruction for client to set focus if it is set
					Paintable f = (Paintable) application.consumeFocus();
					// .. or initializion (first uidl-request)
					boolean init = application.ajaxInit();
					if (init || f != null) {
						paintTarget.startTag("meta");
						if (init)
							paintTarget.addAttribute("appInit", true);
						if (f != null) {
							paintTarget.startTag("focus");
							paintTarget.addAttribute("pid", getPaintableId(f));
							paintTarget.endTag("focus");
						}
						paintTarget.endTag("meta");
					}

					// Precache custom layouts
					// TODO Does not support theme-get param or different themes
					// in different windows -> Allways preload layouts with the
					// theme specified by the applications
					String themeName = application.getTheme() != null ? application
							.getTheme()
							: ApplicationServlet.DEFAULT_THEME;
					// TODO We should only precache the layouts that are not
					// cached already
					for (Iterator i = paintTarget.preCachedResources.iterator(); i
							.hasNext();) {
						String resource = (String) i.next();
						InputStream is = null;
						try {
							is = themeSource.getResource(themeName + "/"
									+ resource);
						} catch (ThemeSource.ThemeException e) {
							Log.info(e.getMessage());
						}
						if (is != null) {
							paintTarget.startTag("precache");
							paintTarget.addAttribute("resource", resource);
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
							paintTarget.addCharacterData(layout.toString());
							paintTarget.endTag("precache");
						}
					}

					paintTarget.close();
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
			String UIID = null;
			// try to get PID using unique user interface identity (UUID)
			if (paintable instanceof Identifiable)
				UIID = ((Identifiable) paintable).getUIID();
			if (UIID != null)
				id = "PID" + UIID;
			else {
				// UUID not set, get PID using growing sequence number
				id = "PID" + Integer.toString(idSequence++);
			}
		}
		paintableIdMap.put(paintable, id);

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
}
