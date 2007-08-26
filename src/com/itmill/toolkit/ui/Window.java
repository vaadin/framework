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

package com.itmill.toolkit.ui;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.Application.WindowAttachEvent;
import com.itmill.toolkit.Application.WindowAttachListener;
import com.itmill.toolkit.terminal.DownloadStream;
import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;
import com.itmill.toolkit.terminal.ParameterHandler;
import com.itmill.toolkit.terminal.Resource;
import com.itmill.toolkit.terminal.Sizeable;
import com.itmill.toolkit.terminal.Terminal;
import com.itmill.toolkit.terminal.URIHandler;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;

/**
 * Application window component.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public class Window extends Panel implements URIHandler, ParameterHandler {

	/**
	 * Window with no border.
	 */
	public static final int BORDER_NONE = 0;

	/**
	 * Window with only minimal border.
	 */
	public static final int BORDER_MINIMAL = 1;

	/**
	 * Window with default borders.
	 */
	public static final int BORDER_DEFAULT = 2;

	/**
	 * The terminal this window is attached to.
	 */
	private Terminal terminal = null;

	/**
	 * The application this window is attached to.
	 */
	private Application application = null;

	/**
	 * List of URI handlers for this window.
	 */
	private LinkedList uriHandlerList = null;

	/**
	 * List of parameter handlers for this window.
	 */
	private LinkedList parameterHandlerList = null;
	
	/** Set of subwindows */
	private HashSet subwindows = new HashSet();

	/**
	 * Explicitly specified theme of this window. If null, application theme is
	 * used.
	 */
	private String theme = null;

	/**
	 * Resources to be opened automatically on next repaint.
	 */
	private LinkedList openList = new LinkedList();

	/**
	 * The name of the window.
	 */
	private String name = null;

	/**
	 * Window border mode.
	 */
	private int border = BORDER_DEFAULT;

	/**
	 * Focused component.
	 */
	private Focusable focusedComponent;

	/**
	 * Distance of Window top border in pixels from top border of the containing
	 * (main window) or -1 if unspecified.
	 */
	private int positionY = -1;

	/**
	 * Distance of Window left border in pixels from left border of the
	 * containing (main window) or -1 if unspecified .
	 */
	private int positionX = -1;

	/* ********************************************************************* */

	/**
	 * Creates a new empty unnamed window with default layout.
	 * 
	 * <p>
	 * To show the window in application, it must be added to application with
	 * <code>Application.addWindow</code> method.
	 * </p>
	 * 
	 * <p>
	 * The windows are scrollable by default.
	 * </p>
	 * 
	 * @param caption
	 *            the Title of the window.
	 */
	public Window() {
		this("", null);
	}

	/**
	 * Creates a new empty window with default layout.
	 * 
	 * <p>
	 * To show the window in application, it must be added to application with
	 * <code>Application.addWindow</code> method.
	 * </p>
	 * 
	 * <p>
	 * The windows are scrollable by default.
	 * </p>
	 * 
	 * @param caption
	 *            the Title of the window.
	 */
	public Window(String caption) {
		this(caption, null);
	}

	/**
	 * Creates a new window.
	 * 
	 * <p>
	 * To show the window in application, it must be added to application with
	 * <code>Application.addWindow</code> method.
	 * </p>
	 * 
	 * <p>
	 * The windows are scrollable by default.
	 * </p>
	 * 
	 * @param caption
	 *            the Title of the window.
	 * @param layout
	 *            the Layout of the window.
	 */
	public Window(String caption, Layout layout) {
		super(caption, layout);
		setScrollable(true);
	}

	/**
	 * Gets the terminal type.
	 * 
	 * @return the Value of property terminal.
	 */
	public Terminal getTerminal() {
		return this.terminal;
	}

	/* ********************************************************************* */

	/**
	 * Gets the window of the component. Returns the window where this component
	 * belongs to. If the component does not yet belong to a window the returns
	 * null.
	 * 
	 * @return the parent window of the component.
	 */
	public final Window getWindow() {
		return this;
	}

	/**
	 * Gets the application instance of the component. Returns the application
	 * where this component belongs to. If the component does not yet belong to
	 * a application the returns null.
	 * 
	 * @return the parent application of the component.
	 */
	public final Application getApplication() {
		if (getParent() == null)
		return this.application;
		return ((Window)getParent()).getApplication();
	}

	/**
	 * Getter for property parent.
	 * 
	 * <p>
	 * Parent is the visual parent of a component. Each component can belong to
	 * only one ComponentContainer at time.
	 * </p>
	 * 
	 * <p>
	 * For windows attached directly to the application, parent is
	 * <code>null</code>. For windows inside other windows, parent is the
	 * window containing this window.
	 * </p>
	 * 
	 * @return the Value of property parent.
	 */
	public final Component getParent() {
		return null;
	}

	/**
	 * Setter for property parent.
	 * 
	 * <p>
	 * Parent is the visual parent of a component. This is mostly called by
	 * containers add method and should not be called directly
	 * </p>
	 * 
	 * @param parent
	 *            the New value of property parent.
	 */
	public void setParent(Component parent) {
		super.setParent(parent);
	}

	/**
	 * Gets the component UIDL tag.
	 * 
	 * @return the Component UIDL tag as string.
	 */
	public String getTag() {
		return "window";
	}

	/* ********************************************************************* */

	/**
	 * Adds the new URI handler to this window.
	 * 
	 * @param handler
	 *            the URI handler to add.
	 */
	public void addURIHandler(URIHandler handler) {
		// TODO Subwindow support

		if (uriHandlerList == null)
			uriHandlerList = new LinkedList();
		synchronized (uriHandlerList) {
			if (!uriHandlerList.contains(handler))
				uriHandlerList.addLast(handler);
		}
	}

	/**
	 * Removes the given URI handler from this window.
	 * 
	 * @param handler
	 *            the URI handler to remove.
	 */
	public void removeURIHandler(URIHandler handler) {
		// TODO Subwindow support

		if (handler == null || uriHandlerList == null)
			return;
		synchronized (uriHandlerList) {
			uriHandlerList.remove(handler);
			if (uriHandlerList.isEmpty())
				uriHandlerList = null;
		}
	}

	/**
	 * Handles uri recursively.
	 * 
	 * @param context
	 * @param relativeUri
	 */
	public DownloadStream handleURI(URL context, String relativeUri) {
		// TODO Subwindow support

		DownloadStream result = null;
		if (uriHandlerList != null) {
			Object[] handlers;
			synchronized (uriHandlerList) {
				handlers = uriHandlerList.toArray();
			}
			for (int i = 0; i < handlers.length; i++) {
				DownloadStream ds = ((URIHandler) handlers[i]).handleURI(
						context, relativeUri);
				if (ds != null) {
					if (result != null)
						throw new RuntimeException("handleURI for " + context
								+ " uri: '" + relativeUri
								+ "' returns ambigious result.");
					result = ds;
				}
			}
		}
		return result;
	}

	/* ********************************************************************* */

	/**
	 * Adds the new parameter handler to this window.
	 * 
	 * @param handler
	 *            the parameter handler to add.
	 */
	public void addParameterHandler(ParameterHandler handler) {
		// TODO Subwindow support
		if (parameterHandlerList == null)
			parameterHandlerList = new LinkedList();
		synchronized (parameterHandlerList) {
			if (!parameterHandlerList.contains(handler))
				parameterHandlerList.addLast(handler);
		}
	}

	/**
	 * Removes the given URI handler from this window.
	 * 
	 * @param handler
	 *            the parameter handler to remove.
	 */
	public void removeParameterHandler(ParameterHandler handler) {
		// TODO Subwindow support
		if (handler == null || parameterHandlerList == null)
			return;
		synchronized (parameterHandlerList) {
			parameterHandlerList.remove(handler);
			if (parameterHandlerList.isEmpty())
				parameterHandlerList = null;
		}
	}

	/* Documented by the interface */
	public void handleParameters(Map parameters) {
		if (parameterHandlerList != null) {
			Object[] handlers;
			synchronized (parameterHandlerList) {
				handlers = parameterHandlerList.toArray();
			}
			for (int i = 0; i < handlers.length; i++)
				((ParameterHandler) handlers[i]).handleParameters(parameters);
		}
	}

	/* ********************************************************************* */

	/**
	 * Gets the theme for this window.
	 * 
	 * <p>Subwindows do not support themes and thus return theme used by the parent</p>
	 * 
	 * @return the Name of the theme used in window. If the theme for this
	 *         individual window is not explicitly set, the application theme is
	 *         used instead. If application is not assigned the
	 *         terminal.getDefaultTheme is used. If terminal is not set, null is
	 *         returned
	 */
	public String getTheme() {
		if (getParent() != null) return ((Window) getParent()).getTheme();
		if (theme != null)
			return theme;
		if ((application != null) && (application.getTheme() != null))
			return application.getTheme();
		if (terminal != null)
			return terminal.getDefaultTheme();
		return null;
	}

	/**
	 * Sets the theme for this window.
	 * 
	 * 	 Setting theme for subwindows is not supported.
	 * @param theme
	 *            the New theme for this window. Null implies the default theme.
	 */
	public void setTheme(String theme) {
		if (getParent() != null) throw new UnsupportedOperationException("Setting theme for sub-windws is not supported.");
		this.theme = theme;
		requestRepaint();
	}

	/**
	 * Paints the content of this component.
	 * 
	 * @param event
	 *            the Paint Event.
	 * @throws PaintException
	 *             if the paint operation failed.
	 */
	public synchronized void paintContent(PaintTarget target)
			throws PaintException {

		// Sets the window name
		String name = getName();
		target.addAttribute("name", name == null ? "" : name);

		// Sets the window theme
		String theme = getTheme();
		target.addAttribute("theme", theme == null ? "" : theme);

		// Marks the main window
		if (getApplication() != null
				&& this == getApplication().getMainWindow())
			target.addAttribute("main", true);

		// Open requested resource
		synchronized (openList) {
			if (!openList.isEmpty()) {
				for (Iterator i = openList.iterator(); i.hasNext();)
					((OpenResource) i.next()).paintContent(target);
				openList.clear();
			}
		}

		// Contents of the window panel is painted
		super.paintContent(target);

		// Window position
		target.addVariable(this, "positionx", getPositionX());
		target.addVariable(this, "positiony", getPositionY());

		// Window closing
		target.addVariable(this, "close", false);

		// Sets the focused component
		if (this.focusedComponent != null)
			target.addVariable(this, "focused", ""
					+ this.focusedComponent.getFocusableId());
		else
			target.addVariable(this, "focused", "");
		
		// Paint subwindows
		for (Iterator i=subwindows.iterator(); i.hasNext();) {
			Window w = (Window) i.next();
			w.paint(target);
		}

	}

	/* ********************************************************************* */

	/**
	 * Opens the given resource in this window.
	 * 
	 * @param resource
	 */
	public void open(Resource resource) {
		synchronized (openList) {
			if (!openList.contains(resource))
				openList.add(new OpenResource(resource, null, -1, -1,
						BORDER_DEFAULT));
		}
		requestRepaint();
	}

	/* ********************************************************************* */

	/**
	 * Opens the given resource in named terminal window. Empty or
	 * <code>null</code> window name results the resource to be opened in this
	 * window.
	 * 
	 * @param resource
	 *            the resource.
	 * @param windowName
	 *            the name of the window.
	 */
	public void open(Resource resource, String windowName) {
		synchronized (openList) {
			if (!openList.contains(resource))
				openList.add(new OpenResource(resource, windowName, -1, -1,
						BORDER_DEFAULT));
		}
		requestRepaint();
	}

	/* ********************************************************************* */

	/**
	 * Opens the given resource in named terminal window with given size and
	 * border properties. Empty or <code>null</code> window name results the
	 * resource to be opened in this window.
	 * 
	 * @param resource
	 * @param windowName
	 * @param width
	 * @param height
	 * @param border
	 */
	public void open(Resource resource, String windowName, int width,
			int height, int border) {
		synchronized (openList) {
			if (!openList.contains(resource))
				openList.add(new OpenResource(resource, windowName, width,
						height, border));
		}
		requestRepaint();
	}

	/* ********************************************************************* */

	/**
	 * Returns the full url of the window, this returns window specific url even
	 * for the main window.
	 * 
	 * @return the URL of the window.
	 */
	public URL getURL() {

		if (application == null)
			return null;

		try {
			return new URL(application.getURL(), getName() + "/");
		} catch (MalformedURLException e) {
			throw new RuntimeException("Internal problem, please report");
		}
	}

	/**
	 * Gets the unique name of the window that indentifies it on the terminal.
	 * 
	 * <p>
	 * Name identifies the URL used to access application-level windows, but is
	 * not used for windows inside other windows. all application-level windows
	 * can be accessed by their names in url
	 * <code>http://host:port/foo/bar/</code> where
	 * <code>http://host:port/foo/</code> is the application url as returned
	 * by getURL() and <code>bar</code> is the name of the window. Also note
	 * that not all windows should be added to application - one can also add
	 * windows inside other windows - these windows show as smaller windows
	 * inside those windows.
	 * </p>
	 * 
	 * @return the Name of the Window.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the border.
	 * 
	 * @return the border.
	 */
	public int getBorder() {
		return border;
	}

	/**
	 * Sets the border.
	 * 
	 * @param border
	 *            the border to set.
	 */
	public void setBorder(int border) {
		this.border = border;
	}

	/**
	 * Sets the application this window is connected to.
	 * 
	 * <p>
	 * This method should not be invoked directly. Instead the
	 * {@link com.itmill.toolkit.Application#addWindow(Window)} method should be
	 * used to add the window to an application and
	 * {@link com.itmill.toolkit.Application#removeWindow(Window)} method for
	 * removing the window from the applicion. These methods call this method
	 * implicitly.
	 * </p>
	 * 
	 * <p>
	 * The method invokes {@link Component#attach()} and
	 * {@link Component#detach()} methods when necessary.
	 * <p>
	 * 
	 * @param application
	 *            the application to set.
	 */
	public void setApplication(Application application) {

		// If the application is not changed, dont do nothing
		if (application == this.application)
			return;

		// Sends detach event if the window is connected to application
		if (this.application != null) {
			detach();
		}

		// Connects to new parent
		this.application = application;

		// Sends the attach event if connected to a window
		if (application != null)
			attach();
	}

	/**
	 * Sets the name.
	 * <p>
	 * The name of the window must be unique inside the application.
	 * </p>
	 * 
	 * <p>
	 * If the name is null, the the window is given name automatically when it
	 * is added to an application.
	 * </p>
	 * 
	 * @param name
	 *            the name to set.
	 */
	public void setName(String name) {

		// The name can not be changed in application
		if (getApplication() != null)
			throw new IllegalStateException(
					"Window name can not be changed while "
							+ "the window is in application");

		this.name = name;
	}

	/**
	 * Sets the terminal type. The terminal type is set by the the terminal
	 * adapter and may change from time to time.
	 * 
	 * @param type
	 *            the terminal type to set.
	 */
	public void setTerminal(Terminal type) {
		this.terminal = type;
	}

	/**
	 * Window only supports pixels as unit.
	 * 
	 * @see com.itmill.toolkit.terminal.Sizeable#getHeightUnits()
	 */
	public void setHeightUnits(int units) {
		if (units != Sizeable.UNITS_PIXELS)
			throw new IllegalArgumentException("Only pixels are supported");
	}

	/**
	 * Window only supports pixels as unit.
	 * 
	 * @see com.itmill.toolkit.terminal.Sizeable#getWidthUnits()
	 */
	public void setWidthUnits(int units) {
		if (units != Sizeable.UNITS_PIXELS)
			throw new IllegalArgumentException("Only pixels are supported");
	}

	/**
	 * Private data structure for storing opening window properties.
	 */
	private class OpenResource {

		private Resource resource;

		private String name;

		private int width;

		private int height;

		private int border;

		/**
		 * Creates a new open resource.
		 * 
		 * @param resource
		 * @param name
		 * @param width
		 * @param height
		 * @param border
		 */
		private OpenResource(Resource resource, String name, int width,
				int height, int border) {
			this.resource = resource;
			this.name = name;
			this.width = width;
			this.height = height;
			this.border = border;
		}

		/**
		 * Paints the open-tag inside the window.
		 * 
		 * @param target
		 *            the Paint Event.
		 * @throws PaintException
		 *             if the Paint Operation fails.
		 */
		private void paintContent(PaintTarget target) throws PaintException {
			target.startTag("open");
			target.addAttribute("src", resource);
			if (name != null && name.length() > 0)
				target.addAttribute("name", name);
			if (width >= 0)
				target.addAttribute("width", width);
			if (height >= 0)
				target.addAttribute("height", height);
			switch (border) {
			case Window.BORDER_MINIMAL:
				target.addAttribute("border", "minimal");
				break;
			case Window.BORDER_NONE:
				target.addAttribute("border", "none");
				break;
			}

			target.endTag("open");
		}
	}

	/**
	 * Called when one or more variables handled by the implementing class are
	 * changed.
	 * 
	 * @see com.itmill.toolkit.terminal.VariableOwner#changeVariables(java.lang.Object,
	 *      java.util.Map)
	 */
	public void changeVariables(Object source, Map variables) {
		super.changeVariables(source, variables);

		// Gets the focused component
		String focusedId = (String) variables.get("focused");
		if (focusedId != null) {
			try {
				long id = Long.parseLong(focusedId);
				this.focusedComponent = Window.getFocusableById(id);
			} catch (NumberFormatException ignored) {
				// We ignore invalid focusable ids
			}
		}

		// Positioning
		Integer positionx = (Integer) variables.get("positionx");
		if (positionx != null) {
			int x = positionx.intValue();
			setPositionX(x < 0 ? -1 : x);
		}
		Integer positiony = (Integer) variables.get("positiony");
		if (positiony != null) {
			int y = positiony.intValue();
			setPositionY(y < 0 ? -1 : y);
		}

		// Closing
		Boolean close = (Boolean) variables.get("close");
		if (close != null && close.booleanValue()) {
			this.setVisible(false);
			fireClose();
		}
	}

	/**
	 * Gets the currently focused component in this window.
	 * 
	 * @return the Focused component or null if none is focused.
	 */
	public Component.Focusable getFocusedComponent() {
		return this.focusedComponent;
	}

	/**
	 * Sets the currently focused component in this window.
	 * 
	 * @param focusable
	 *            the Focused component or null if none is focused.
	 */
	public void setFocusedComponent(Component.Focusable focusable) {
		this.application.setFocusedComponent(focusable);
		this.focusedComponent = focusable;
	}

	/* Focusable id generator ****************************************** */

	private static long lastUsedFocusableId = 0;

	private static Map focusableComponents = new HashMap();

	/**
	 * Gets an id for focusable component.
	 * 
	 * @param focusable
	 *            the focused component.
	 */
	public static long getNewFocusableId(Component.Focusable focusable) {
		long newId = ++lastUsedFocusableId;
		WeakReference ref = new WeakReference(focusable);
		focusableComponents.put(new Long(newId), ref);
		return newId;
	}

	/**
	 * Maps the focusable id back to focusable component.
	 * 
	 * @param focusableId
	 *            the Focused Id.
	 * @return the focusable Id.
	 */
	public static Component.Focusable getFocusableById(long focusableId) {
		WeakReference ref = (WeakReference) focusableComponents.get(new Long(
				focusableId));
		if (ref != null) {
			Object o = ref.get();
			if (o != null) {
				return (Component.Focusable) o;
			}
		}
		return null;
	}

	/**
	 * Releases the focusable component id when not used anymore.
	 * 
	 * @param focusableId
	 *            the focusable Id to remove.
	 */
	public static void removeFocusableId(long focusableId) {
		Long id = new Long(focusableId);
		WeakReference ref = (WeakReference) focusableComponents.get(id);
		ref.clear();
		focusableComponents.remove(id);
	}

	/**
	 * Gets the distance of Window left border in pixels from left border of the
	 * containing (main window).
	 * 
	 * @return the Distance of Window left border in pixels from left border of
	 *         the containing (main window). or -1 if unspecified.
	 * @since 4.0.0
	 */
	public int getPositionX() {
		return positionX;
	}

	/**
	 * Sets the distance of Window left border in pixels from left border of the
	 * containing (main window).
	 * 
	 * @param positionX
	 *            the Distance of Window left border in pixels from left border
	 *            of the containing (main window). or -1 if unspecified.
	 * @since 4.0.0
	 */
	public void setPositionX(int positionX) {
		this.positionX = positionX;
	}

	/**
	 * Gets the distance of Window top border in pixels from top border of the
	 * containing (main window).
	 * 
	 * @return Distance of Window top border in pixels from top border of the
	 *         containing (main window). or -1 if unspecified .
	 * 
	 * @since 4.0.0
	 */
	public int getPositionY() {
		return positionY;
	}

	/**
	 * Sets the distance of Window top border in pixels from top border of the
	 * containing (main window).
	 * 
	 * @param positionY
	 *            the Distance of Window top border in pixels from top border of
	 *            the containing (main window). or -1 if unspecified
	 * 
	 * @since 4.0.0
	 */
	public void setPositionY(int positionY) {
		this.positionY = positionY;
	}

	private static final Method WINDOW_CLOSE_METHOD;
	static {
		try {
			WINDOW_CLOSE_METHOD = CloseListener.class.getDeclaredMethod(
					"windowClose", new Class[] { CloseEvent.class });
		} catch (java.lang.NoSuchMethodException e) {
			// This should never happen
			throw new java.lang.RuntimeException();
		}
	}

	public class CloseEvent extends Component.Event {

		/**
		 * Serial generated by eclipse.
		 */
		private static final long serialVersionUID = -7235770057344367327L;

		/**
		 * 
		 * @param source
		 */
		public CloseEvent(Component source) {
			super(source);
		}

		/**
		 * Gets the Window.
		 * 
		 * @return the window.
		 */
		public Window getWindow() {
			return (Window) getSource();
		}
	}

	public interface CloseListener {
		public void windowClose(CloseEvent e);
	}

	/**
	 * Adds the listener.
	 * 
	 * @param listener
	 *            the listener to add.
	 */
	public void addListener(CloseListener listener) {
		addListener(CloseEvent.class, listener, WINDOW_CLOSE_METHOD);
	}

	/**
	 * Removes the listener.
	 * 
	 * @param listener
	 *            the listener to remove.
	 */
	public void removeListener(CloseListener listener) {
		addListener(CloseEvent.class, listener, WINDOW_CLOSE_METHOD);
	}

	protected void fireClose() {
		fireEvent(new Window.CloseEvent(this));
	}

	/**
	 * Adds a new window inside another window.
	 * 
	 * <p>
	 * Adding windows inside another window creates "subwindows". These windows
	 * should not be added to application directly and are not accessible
	 * directly with any url. Addding windows implicitly sets their parents.
	 * </p>
	 * 
	 * <p>
	 * Only one level of subwindows are supported. Thus you can add windows
	 * inside such windows whose parent is <code>null</code>.
	 * </p>
	 * 
	 * @param window
	 * @throws IllegalArgumentException
	 *             if a window is added inside non-application level window.
	 * @throws NullPointerException
	 *             if the given <code>Window</code> is <code>null</code>.
	 */
	public void addWindow(Window window) throws IllegalArgumentException,
			NullPointerException {

		if (getParent() != null)
			throw new IllegalArgumentException(
					"You can only add windows inside application-level windows");
		
		if (window == null) throw new NullPointerException("Argument must not be null");

		subwindows.add(window);
		window.setParent(this);
		requestRepaint();
	}
	
	/** Remove the given subwindow from this window.
	 * 
	 * @param window Window to be removed.
	 */
	public void removeWindow(Window window) {
		subwindows.remove(window);
		window.setParent(null);
		requestRepaint();
		
	}

	/** Get the set of all child windows.
	 * 
	 * @return Set of child windows.
	 */
	public Set getChildWindows() {
		return Collections.unmodifiableSet(subwindows);
	}

}
