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

package com.itmill.toolkit;

import com.itmill.toolkit.service.ApplicationContext;
import com.itmill.toolkit.service.License;
import com.itmill.toolkit.terminal.*;
import com.itmill.toolkit.ui.AbstractComponent;
import com.itmill.toolkit.ui.Component;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Component.Focusable;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * <p>
 * Base class required for all IT Mill Toolkit applications. This class provides
 * all the basic services required by the toolkit. These services allow external
 * discovery and manipulation of the user,
 * {@link com.itmill.toolkit.ui.Window windows} and themes, and starting and
 * stopping the application.
 * </p>
 * 
 * <p>
 * As mentioned, all IT Mill Toolkit applications must inherit this class.
 * However, this is almost all of what one needs to do to create a fully
 * functional application. The only thing a class inheriting the
 * <code>Application</code> needs to do is implement the <code>init</code>
 * method where it creates the windows it needs to perform its function. Note
 * that all applications must have at least one window: the main window. The
 * first unnamed window constructed by an application automatically becomes the
 * main window which behaves just like other windows with one exception: when
 * accessing windows using URLs the main window corresponds to the application
 * URL whereas other windows correspond to a URL gotten by catenating the
 * window's name to the application URL.
 * </p>
 * 
 * <p>
 * See the class <code>com.itmill.toolkit.demo.HelloWorld</code> for a simple
 * example of a fully working application.
 * </p>
 * 
 * <p>
 * <strong>Window access.</strong> <code>Application</code> provides methods
 * to list, add and remove the windows it contains.
 * </p>
 * 
 * <p>
 * <strong>Execution control.</strong> This class includes method to start and
 * finish the execution of the application. Being finished means basically that
 * no windows will be available from the application anymore.
 * </p>
 * 
 * <p>
 * <strong>Theme selection.</strong> The theme selection process allows a theme
 * to be specified at three different levels. When a window's theme needs to be
 * found out, the window itself is queried for a preferred theme. If the window
 * does not prefer a specific theme, the application containing the window is
 * queried. If neither the application prefers a theme, the default theme for
 * the {@link com.itmill.toolkit.terminal.Terminal terminal} is used. The
 * terminal always defines a default theme.
 * </p>
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public abstract class Application implements URIHandler, Terminal.ErrorListener {

	/**
	 * Random window name generator.
	 */
	private static Random nameGenerator = new Random();

	/**
	 * Application context the application is running in.
	 */
	private ApplicationContext context;

	/**
	 * The current user or <code>null</code> if no user has logged in.
	 */
	private Object user;

	/**
	 * Mapping from window name to window instance.
	 */
	private Hashtable windows = new Hashtable();

	/**
	 * Main window of the application.
	 */
	private Window mainWindow = null;

	/**
	 * The application's URL.
	 */
	private URL applicationUrl;

	/**
	 * Name of the theme currently used by the application.
	 */
	private String theme = null;

	/**
	 * Application status.
	 */
	private boolean applicationIsRunning = false;

	/**
	 * Application properties.
	 */
	private Properties properties;

	/**
	 * Default locale of the application.
	 */
	private Locale locale;

	/**
	 * List of listeners listening user changes.
	 */
	private LinkedList userChangeListeners = null;

	/**
	 * Window attach listeners.
	 */
	private LinkedList windowAttachListeners = null;

	/**
	 * Window detach listeners.
	 */
	private LinkedList windowDetachListeners = null;

	/**
	 * License for running this application.
	 */
	private License license = null;

	/**
	 * Application resource mapping: key <-> resource.
	 */
	private Hashtable resourceKeyMap = new Hashtable();

	private Hashtable keyResourceMap = new Hashtable();

	private long lastResourceKeyNumber = 0;

	/**
	 * URL the user is redirected to on application close or null if application
	 * is just closed
	 */
	private String logoutURL = null;

	private Focusable pendingFocus;

	/**
	 * <p>
	 * Gets a window by name. Returns <code>null</code> if the application is
	 * not running or it does not contain a window corresponding to the name.
	 * </p>
	 * 
	 * @param name
	 *            the name of the window.
	 * @return the window associated with the given URI or <code>null</code>
	 */
	public Window getWindow(String name) {

		// For closed app, do not give any windows
		if (!isRunning())
			return null;

		// Gets the window by name
		Window window = (Window) windows.get(name);

		return window;
	}

	/**
	 * Adds a new window to the application.
	 * 
	 * <p>
	 * This implicitly invokes the
	 * {@link com.itmill.toolkit.ui.Window#setApplication(Application)} method.
	 * </p>
	 * 
	 * @param window
	 *            the new <code>Window</code> to add.
	 * @throws IllegalArgumentException
	 *             if a window with the same name as the new window already
	 *             exists in the application.
	 * @throws NullPointerException
	 *             if the given <code>Window</code> or its name is
	 *             <code>null</code>.
	 */
	public void addWindow(Window window) throws IllegalArgumentException,
			NullPointerException {

		// Nulls can not be added to application
		if (window == null)
			return;

		// Gets the naming proposal from window
		String name = window.getName();

		// Checks that the application does not already contain
		// window having the same name
		if (name != null && windows.containsKey(name)) {

			// If the window is already added
			if (window == windows.get(name))
				return;

			// Otherwise complain
			throw new IllegalArgumentException("Window with name '"
					+ window.getName()
					+ "' is already present in the application");
		}

		// If the name of the window is null, the window is automatically named
		if (name == null) {
			boolean accepted = false;
			while (!accepted) {

				// Try another name
				name = String.valueOf(Math.abs(nameGenerator.nextInt()));
				if (!windows.containsKey(name))
					accepted = true;
			}
			window.setName(name);
		}

		// Adds the window to application
		windows.put(name, window);
		window.setApplication(this);

		// Fires the window attach event
		if (windowAttachListeners != null) {
			Object[] listeners = windowAttachListeners.toArray();
			WindowAttachEvent event = new WindowAttachEvent(window);
			for (int i = 0; i < listeners.length; i++) {
				((WindowAttachListener) listeners[i]).windowAttached(event);
			}
		}

		// If no main window is set, declare the window to be main window
		if (getMainWindow() == null)
			setMainWindow(window);
	}

	/**
	 * Removes the specified window from the application.
	 * 
	 * @param window
	 *            the window to be removed.
	 */
	public void removeWindow(Window window) {
		if (window != null && windows.contains(window)) {

			// Removes the window from application
			windows.remove(window.getName());

			// If the window was main window, clear it
			if (getMainWindow() == window)
				setMainWindow(null);

			// Removes the application from window
			if (window.getApplication() == this)
				window.setApplication(null);

			// Fires the window detach event
			if (windowDetachListeners != null) {
				Object[] listeners = windowDetachListeners.toArray();
				WindowDetachEvent event = new WindowDetachEvent(window);
				for (int i = 0; i < listeners.length; i++) {
					((WindowDetachListener) listeners[i]).windowDetached(event);
				}
			}
		}
	}

	/**
	 * Gets the user of the application.
	 * 
	 * @return the User of the application.
	 */
	public Object getUser() {
		return user;
	}

	/**
	 * <p>
	 * Sets the user of the application instance. An application instance may
	 * have a user associated to it. This can be set in login procedure or
	 * application initialization.
	 * </p>
	 * <p>
	 * A component performing the user login procedure can assign the user
	 * property of the application and make the user object available to other
	 * components of the application.
	 * </p>
	 * 
	 * @param user
	 *            the new user.
	 */
	public void setUser(Object user) {
		Object prevUser = this.user;
		if (user != prevUser && (user == null || !user.equals(prevUser))) {
			this.user = user;
			if (userChangeListeners != null) {
				Object[] listeners = userChangeListeners.toArray();
				UserChangeEvent event = new UserChangeEvent(this, user,
						prevUser);
				for (int i = 0; i < listeners.length; i++) {
					((UserChangeListener) listeners[i])
							.applicationUserChanged(event);
				}
			}
		}
	}

	/**
	 * Gets the URL of the application.
	 * 
	 * @return the application's URL.
	 */
	public URL getURL() {
		return applicationUrl;
	}

	/**
	 * Ends the Application. In effect this will cause the application stop
	 * returning any windows when asked.
	 */
	public void close() {
		applicationIsRunning = false;
	}

	/**
	 * Starts the application on the given URL.After this call the application
	 * corresponds to the given URL and it will return windows when asked for
	 * them.
	 * 
	 * @param applicationUrl
	 *            the URL the application should respond to.
	 * @param applicationProperties
	 *            the Application properties as specified by the adapter.
	 * @param context
	 *            the context application will be running in.
	 * 
	 */
	public void start(URL applicationUrl, Properties applicationProperties,
			ApplicationContext context) {
		this.applicationUrl = applicationUrl;
		this.properties = applicationProperties;
		this.context = context;
		init();
		applicationIsRunning = true;
	}

	/**
	 * Tests if the application is running or if it has been finished.
	 * 
	 * @return <code>true</code> if the application is running,
	 *         <code>false</code> if not.
	 */
	public boolean isRunning() {
		return applicationIsRunning;
	}

	/**
	 * Gets the set of windows contained by the application.
	 * 
	 * @return the Unmodifiable collection of windows.
	 */
	public Collection getWindows() {
		return Collections.unmodifiableCollection(windows.values());
	}

	/**
	 * <p>
	 * Main initializer of the application. The <code>init</code> method is
	 * called by the framework when the application is started, and it should
	 * perform whatever initialization operations the application needs, such as
	 * creating windows and adding components to them.
	 * </p>
	 */
	public abstract void init();

	/**
	 * Gets the application's theme. The application's theme is the default
	 * theme used by all the windows in it that do not explicitly specify a
	 * theme. If the application theme is not explicitly set, the
	 * <code>null</code> is returned.
	 * 
	 * @return the name of the application's theme.
	 */
	public String getTheme() {
		return theme;
	}

	/**
	 * Sets the application's theme.
	 * <p>
	 * Note that this theme can be overridden by the windows. <code>null</code>
	 * implies the default terminal theme.
	 * </p>
	 * 
	 * @param theme
	 *            the new theme for this application.
	 */
	public void setTheme(String theme) {

		// Collect list of windows not having the current or future theme
		LinkedList toBeUpdated = new LinkedList();
		String myTheme = this.getTheme();
		for (Iterator i = getWindows().iterator(); i.hasNext();) {
			Window w = (Window) i.next();
			String windowTheme = w.getTheme();
			if ((windowTheme == null)
					|| (!theme.equals(windowTheme) && windowTheme
							.equals(myTheme))) {
				toBeUpdated.add(w);
			}
		}

		// Updates the theme
		this.theme = theme;

		// Ask windows to update themselves
		for (Iterator i = toBeUpdated.iterator(); i.hasNext();)
			((Window) i.next()).requestRepaint();
	}

	/**
	 * Gets the mainWindow of the application.
	 * 
	 * @return the main window.
	 */
	public Window getMainWindow() {
		return mainWindow;
	}

	/**
	 * <p>
	 * Sets the mainWindow. If the main window is not explicitly set, the main
	 * window defaults to first created window. Setting window as a main window
	 * of this application also adds the window to this application.
	 * </p>
	 * 
	 * @param mainWindow
	 *            the mainWindow to set.
	 */
	public void setMainWindow(Window mainWindow) {

		addWindow(mainWindow);
		this.mainWindow = mainWindow;
	}

	/**
	 * Returns an enumeration of all the names in this application.
	 * 
	 * @return an enumeration of all the keys in this property list, including
	 *         the keys in the default property list.
	 * 
	 */
	public Enumeration getPropertyNames() {
		return this.properties.propertyNames();
	}

	/**
	 * Searches for the property with the specified name in this application.
	 * This method returns <code>null</code> if the property is not found.
	 * 
	 * @param name
	 *            the name of the property.
	 * @return the value in this property list with the specified key value.
	 */
	public String getProperty(String name) {
		return this.properties.getProperty(name);
	}

	/**
	 * Adds new resource to the application. The resource can be accessed by the
	 * user of the application.
	 * 
	 * @param resource
	 *            the resource to add.
	 */
	public void addResource(ApplicationResource resource) {

		// Check if the resource is already mapped
		if (resourceKeyMap.containsKey(resource))
			return;

		// Generate key
		String key = String.valueOf(++lastResourceKeyNumber);

		// Add the resource to mappings
		resourceKeyMap.put(resource, key);
		keyResourceMap.put(key, resource);
	}

	/**
	 * Removes the resource from the application.
	 * 
	 * @param resource
	 *            the resource to remove.
	 */
	public void removeResource(ApplicationResource resource) {
		Object key = resourceKeyMap.get(resource);
		if (key != null) {
			resourceKeyMap.remove(resource);
			keyResourceMap.remove(key);
		}
	}

	/**
	 * Gets the relative uri of the resource.
	 * 
	 * @param resource
	 *            the resource to get relative location.
	 * @return the relative uri of the resource.
	 */
	public String getRelativeLocation(ApplicationResource resource) {

		// Gets the key
		String key = (String) resourceKeyMap.get(resource);

		// If the resource is not registered, return null
		if (key == null)
			return null;

		String filename = resource.getFilename();
		if (filename == null)
			return "APP/" + key + "/";
		else
			return "APP/" + key + "/" + filename;
	}

	/*
	 * @see com.itmill.toolkit.terminal.URIHandler#handleURI(URL, String)
	 */
	public DownloadStream handleURI(URL context, String relativeUri) {

		// If the relative uri is null, we are ready
		if (relativeUri == null)
			return null;

		// Resolves the prefix
		String prefix = relativeUri;
		int index = relativeUri.indexOf('/');
		if (index >= 0)
			prefix = relativeUri.substring(0, index);

		// Handles the resource requests
		if (prefix.equals("APP")) {

			// Handles the resource request
			int next = relativeUri.indexOf('/', index + 1);
			if (next < 0)
				return null;
			String key = relativeUri.substring(index + 1, next);
			ApplicationResource resource = (ApplicationResource) keyResourceMap
					.get(key);
			if (resource != null)
				return resource.getStream();

			// Resource requests override uri handling
			return null;
		}

		// If the uri is in some window, handle the window uri
		Window window = getWindow(prefix);
		if (window != null) {
			URL windowContext;
			try {
				windowContext = new URL(context, prefix + "/");
				String windowUri = relativeUri.length() > prefix.length() + 1 ? relativeUri
						.substring(prefix.length() + 1)
						: "";
				return window.handleURI(windowContext, windowUri);
			} catch (MalformedURLException e) {
				return null;
			}
		}

		// If the uri was not pointing to a window, handle the
		// uri in main window
		window = getMainWindow();
		if (window != null)
			return window.handleURI(context, relativeUri);

		return null;
	}

	/**
	 * Gets the default locale for this application.
	 * 
	 * @return the locale of this application.
	 */
	public Locale getLocale() {
		if (this.locale != null)
			return this.locale;
		return Locale.getDefault();
	}

	/**
	 * Sets the default locale for this application.
	 * 
	 * @param locale
	 *            the Locale object.
	 * 
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	/**
	 * <p>
	 * An event that characterizes a change in the current selection.
	 * </p>
	 * Application user change event sent when the setUser is called to change
	 * the current user of the application.
	 * 
	 * @version
	 * @VERSION@
	 * @since 3.0
	 */
	public class UserChangeEvent extends java.util.EventObject {

		/**
		 * Serial generated by eclipse.
		 */
		private static final long serialVersionUID = 3544951069307188281L;

		/**
		 * New user of the application.
		 */
		private Object newUser;

		/**
		 * Previous user of the application.
		 */
		private Object prevUser;

		/**
		 * Contructor for user change event.
		 * 
		 * @param source
		 *            the application source.
		 * @param newUser
		 *            the new User.
		 * @param prevUser
		 *            the previous User.
		 */
		public UserChangeEvent(Application source, Object newUser,
				Object prevUser) {
			super(source);
			this.newUser = newUser;
			this.prevUser = prevUser;
		}

		/**
		 * Gets the new user of the application.
		 * 
		 * @return the new User.
		 */
		public Object getNewUser() {
			return newUser;
		}

		/**
		 * Gets the previous user of the application.
		 * 
		 * @return the previous Toolkit user, if user has not changed ever on
		 *         application it returns <code>null</code>
		 */
		public Object getPreviousUser() {
			return prevUser;
		}

		/**
		 * Gets the application where the user change occurred.
		 * 
		 * @return the Application.
		 */
		public Application getApplication() {
			return (Application) getSource();
		}
	}

	/**
	 * The <code>UserChangeListener</code> interface for listening application
	 * user changes.
	 * 
	 * @version
	 * @VERSION@
	 * @since 3.0
	 */
	public interface UserChangeListener extends EventListener {

		/**
		 * The <code>applicationUserChanged</code> method Invoked when the
		 * application user has changed.
		 * 
		 * @param event
		 *            the change event.
		 */
		public void applicationUserChanged(Application.UserChangeEvent event);
	}

	/**
	 * Adds the user change listener.
	 * 
	 * @param listener
	 *            the user change listener to add.
	 */
	public void addListener(UserChangeListener listener) {
		if (userChangeListeners == null)
			userChangeListeners = new LinkedList();
		userChangeListeners.add(listener);
	}

	/**
	 * Removes the user change listener.
	 * 
	 * @param listener
	 *            the user change listener to remove.
	 */
	public void removeListener(UserChangeListener listener) {
		if (userChangeListeners == null)
			return;
		userChangeListeners.remove(listener);
		if (userChangeListeners.isEmpty())
			userChangeListeners = null;
	}

	/**
	 * Window detach event.
	 */
	public class WindowDetachEvent extends EventObject {

		/**
		 * Serial generated by eclipse.
		 */
		private static final long serialVersionUID = 3544669568644691769L;

		private Window window;

		/**
		 * Creates a event.
		 * 
		 * @param window
		 *            the Detached window.
		 */
		public WindowDetachEvent(Window window) {
			super(Application.this);
			this.window = window;
		}

		/**
		 * Gets the detached window.
		 * 
		 * @return the detached window.
		 */
		public Window getWindow() {
			return window;
		}

		/**
		 * Gets the application from which the window was detached.
		 * 
		 * @return the Application.
		 */
		public Application getApplication() {
			return (Application) getSource();
		}
	}

	/**
	 * Window attach event.
	 */
	public class WindowAttachEvent extends EventObject {

		/**
		 * Serial generated by eclipse.
		 */
		private static final long serialVersionUID = 3977578104367822392L;

		private Window window;

		/**
		 * Creates a event.
		 * 
		 * @param window
		 *            the Attached window.
		 */
		public WindowAttachEvent(Window window) {
			super(Application.this);
			this.window = window;
		}

		/**
		 * Gets the attached window.
		 * 
		 * @return the attached window.
		 */
		public Window getWindow() {
			return window;
		}

		/**
		 * Gets the application to which the window was attached.
		 * 
		 * @return the Application.
		 */
		public Application getApplication() {
			return (Application) getSource();
		}
	}

	/**
	 * Window attach listener interface.
	 */
	public interface WindowAttachListener {

		/**
		 * Window attached
		 * 
		 * @param event
		 *            the window attach event.
		 */
		public void windowAttached(WindowAttachEvent event);
	}

	/**
	 * Window detach listener interface.
	 */
	public interface WindowDetachListener {

		/**
		 * Window detached.
		 * 
		 * @param event
		 *            the window detach event.
		 */
		public void windowDetached(WindowDetachEvent event);
	}

	/**
	 * Adds the window attach listener.
	 * 
	 * @param listener
	 *            the window attach listener to add.
	 */
	public void addListener(WindowAttachListener listener) {
		if (windowAttachListeners == null)
			windowAttachListeners = new LinkedList();
		windowAttachListeners.add(listener);
	}

	/**
	 * Adds the window detach listener.
	 * 
	 * @param listener
	 *            the window detach listener to add.
	 */
	public void addListener(WindowDetachListener listener) {
		if (windowDetachListeners == null)
			windowDetachListeners = new LinkedList();
		windowDetachListeners.add(listener);
	}

	/**
	 * Removes the window attach listener.
	 * 
	 * @param listener
	 *            the window attach listener to remove.
	 */
	public void removeListener(WindowAttachListener listener) {
		if (windowAttachListeners != null) {
			windowAttachListeners.remove(listener);
			if (windowAttachListeners.isEmpty())
				windowAttachListeners = null;
		}
	}

	/**
	 * Removes the window detach listener.
	 * 
	 * @param listener
	 *            the window detach listener to remove.
	 */
	public void removeListener(WindowDetachListener listener) {
		if (windowDetachListeners != null) {
			windowDetachListeners.remove(listener);
			if (windowDetachListeners.isEmpty())
				windowDetachListeners = null;
		}
	}

	/**
	 * Returns the URL user is redirected to on application close.If the URL is
	 * <code>null</code>, the application is closed normally as defined by
	 * the application running environment.
	 * <p>
	 * Desctop application just closes the application window and
	 * web-application redirects the browser to application main URL.
	 * </p>
	 * 
	 * @return the URL.
	 */
	public String getLogoutURL() {
		return logoutURL;
	}

	/**
	 * Sets the URL user is redirected to on application close. If the URL is
	 * <code>null</code>, the application is closed normally as defined by
	 * the application running environment: Desctop application just closes the
	 * application window and web-application redirects the browser to
	 * application main URL.
	 * 
	 * @param logoutURL
	 *            the logoutURL to set.
	 */
	public void setLogoutURL(String logoutURL) {
		this.logoutURL = logoutURL;
	}

	/**
	 * <p>
	 * Invoked by the terminal on any exception that occurs in application and
	 * is thrown by the <code>setVariable</code> to the terminal. The default
	 * implementation sets the exceptions as <code>ComponentErrors</code> to
	 * the component that initiated the exception.
	 * </p>
	 * <p>
	 * You can safely override this method in your application in order to
	 * direct the errors to some other destination (for example log).
	 * </p>
	 * 
	 * @param event
	 *            the change event.
	 * @see com.itmill.toolkit.terminal.Terminal.ErrorListener#terminalError(com.itmill.toolkit.terminal.Terminal.ErrorEvent)
	 */
	public void terminalError(Terminal.ErrorEvent event) {

		// Finds the original source of the error/exception
		Object owner = null;
		if (event instanceof VariableOwner.ErrorEvent) {
			owner = ((VariableOwner.ErrorEvent) event).getVariableOwner();
		} else if (event instanceof URIHandler.ErrorEvent) {
			owner = ((URIHandler.ErrorEvent) event).getURIHandler();
		} else if (event instanceof ParameterHandler.ErrorEvent) {
			owner = ((ParameterHandler.ErrorEvent) event).getParameterHandler();
		}

		// Shows the error in AbstractComponent
		if (owner instanceof AbstractComponent) {
			Throwable e = event.getThrowable();
			if (e instanceof ErrorMessage)
				((AbstractComponent) owner).setComponentError((ErrorMessage) e);
			else
				((AbstractComponent) owner)
						.setComponentError(new SystemError(e));
		}
	}

	/**
	 * Gets the application context.
	 * <p>
	 * The application context is the environment where the application is
	 * running in.
	 * </p>
	 * 
	 * @return the application context.
	 */
	public ApplicationContext getContext() {
		return context;
	}

	/**
	 * Gets the license this application is running on.
	 * <p>
	 * The license is initialized by the <code>ApplicationServlet</code> class
	 * before application is started. The license-file can not be found in
	 * <code>WEB-INF/itmill-toolkit-license.xml</code>, you can set its
	 * source in application <code>init</code> method.
	 * </p>
	 * 
	 * @return the License this application is currently using.
	 */
	public License getToolkitLicense() {
		return license;
	}

	/**
	 * Sets the license this application is currently using.
	 * <p>
	 * The license is initialized by the <code>ApplicationServlet</code>
	 * before application is started. Changing the license after application
	 * <code>init</code> method has no effect.
	 * </p>
	 * 
	 * @param license
	 *            the New license for this application.
	 */
	public void setToolkitLicense(License license) {
		this.license = license;
	}

	public void setFocusedComponent(Focusable focusable) {
		this.pendingFocus = focusable;
	}

	/**
	 * Gets and nulls focused component in this window
	 * 
	 * @return Focused component or null if none is focused.
	 */
	public Component.Focusable consumeFocus() {
		Component.Focusable f = this.pendingFocus;
		this.pendingFocus = null;
		return f;
	}

}