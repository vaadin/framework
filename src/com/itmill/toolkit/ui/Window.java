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

package com.itmill.toolkit.ui;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.terminal.DownloadStream;
import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.PaintTarget;
import com.itmill.toolkit.terminal.ParameterHandler;
import com.itmill.toolkit.terminal.Resource;
import com.itmill.toolkit.terminal.Sizeable;
import com.itmill.toolkit.terminal.Terminal;
import com.itmill.toolkit.terminal.URIHandler;

import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Iterator;

/**
 * Application window component.
 * 
 * @author IT Mill Ltd.
 * @version
 * @VERSION@
 * @since 3.0
 */
public class Window extends Panel implements URIHandler, ParameterHandler {

    /** Window with no border */
    public static final int BORDER_NONE = 0;

    /** Window with only minimal border */
    public static final int BORDER_MINIMAL = 1;

    /** Window with default borders */
    public static final int BORDER_DEFAULT = 2;

    /** The terminal this window is attached to */
    private Terminal terminal = null;

    /** The applicaiton this window is attached to */
    private Application application = null;

    /** List of URI handlers for this window */
    private LinkedList uriHandlerList = null;

    /** List of parameter handlers for this window */
    private LinkedList parameterHandlerList = null;

    /**
     * Explicitly specified theme of this window. If null, application theme is
     * used
     */
    private String theme = null;

    /** Resources to be opened automatically on next repaint */
    private LinkedList openList = new LinkedList();

    /** The name of the window */
    private String name = null;

    /** Window border mode */
    private int border = BORDER_DEFAULT;

    /** Focused component */
    private Focusable focusedComponent;

    /* ********************************************************************* */

    /**
     * Create new empty unnamed window with default layout.
     * 
     * <p>
     * To show the window in application, it must be added to application with
     * <code>Application.addWindow()</code> method.
     * </p>
     * 
     * <p>
     * The windows are scrollable by default.
     * </p>
     * 
     * @param caption
     *            Title of the window
     */
    public Window() {
        this("", null);
    }

    /**
     * Create new empty window with default layout.
     * 
     * <p>
     * To show the window in application, it must be added to application with
     * <code>Application.addWindow()</code> method.
     * </p>
     * 
     * <p>
     * The windows are scrollable by default.
     * </p>
     * 
     * @param caption
     *            Title of the window
     */
    public Window(String caption) {
        this(caption, null);
    }

    /**
     * Create new window.
     * 
     * <p>
     * To show the window in application, it must be added to application with
     * <code>Application.addWindow()</code> method.
     * </p>
     * 
     * <p>
     * The windows are scrollable by default.
     * </p>
     * 
     * @param caption
     *            Title of the window
     * @param layout
     *            Layout of the window
     */
    public Window(String caption, Layout layout) {
        super(caption, layout);
        setScrollable(true);
    }

    /**
     * Get terminal type.
     * 
     * @return Value of property terminal.
     */
    public Terminal getTerminal() {
        return this.terminal;
    }

    /* ********************************************************************* */

    /**
     * Get window of the component. Returns the window where this component
     * belongs to. If the component does not yet belong to a window the returns
     * null.
     * 
     * @return parent window of the component.
     */
    public final Window getWindow() {
        return this;
    }

    /**
     * Get application instance of the component. Returns the application where
     * this component belongs to. If the component does not yet belong to a
     * application the returns null.
     * 
     * @return parent application of the component.
     */
    public final Application getApplication() {
        return this.application;
    }

    /**
     * Getter for property parent. Parent is the visual parent of a component.
     * Each component can belong to only one ComponentContainer at time.
     * 
     * @return Value of property parent.
     */
    public final Component getParent() {
        return null;
    }

    /**
     * Setter for property parent. Parent is the visual parent of a component.
     * This is mostly called by containers add method. Setting parent is not
     * allowed for the window, and thus this call should newer be called.
     * 
     * @param parent
     *            New value of property parent.
     */
    public void setParent(Component parent) {
        throw new RuntimeException("Setting parent for Window is not allowed");
    }

    /**
     * Get component UIDL tag.
     * 
     * @return Component UIDL tag as string.
     */
    public String getTag() {
        return "window";
    }

    /* ********************************************************************* */

    /** Add new URI handler to this window */
    public void addURIHandler(URIHandler handler) {
        if (uriHandlerList == null)
            uriHandlerList = new LinkedList();
        synchronized (uriHandlerList) {
            uriHandlerList.addLast(handler);
        }
    }

    /** Remove given URI handler from this window */
    public void removeURIHandler(URIHandler handler) {
        if (handler == null || uriHandlerList == null)
            return;
        synchronized (uriHandlerList) {
            uriHandlerList.remove(handler);
            if (uriHandlerList.isEmpty())
                uriHandlerList = null;
        }
    }

    /**
     * Handle uri recursively.
     */
    public DownloadStream handleURI(URL context, String relativeUri) {
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

    /** Add new parameter handler to this window. */
    public void addParameterHandler(ParameterHandler handler) {
        if (parameterHandlerList == null)
            parameterHandlerList = new LinkedList();
        synchronized (parameterHandlerList) {
            parameterHandlerList.addLast(handler);
        }
    }

    /** Remove given URI handler from this window. */
    public void removeParameterHandler(ParameterHandler handler) {
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
     * Get theme for this window.
     * 
     * @return Name of the theme used in window. If the theme for this
     *         individual window is not explicitly set, the application theme is
     *         used instead. If application is not assigned the
     *         terminal.getDefaultTheme is used. If terminal is not set, null is
     *         returned
     */
    public String getTheme() {
        if (theme != null)
            return theme;
        if ((application != null) && (application.getTheme() != null))
            return application.getTheme();
        if (terminal != null)
            return terminal.getDefaultTheme();
        return null;
    }

    /**
     * Set theme for this window.
     * 
     * @param theme
     *            New theme for this window. Null implies the default theme.
     */
    public void setTheme(String theme) {
        this.theme = theme;
        requestRepaint();
    }

    /**
     * Paint the content of this component.
     * 
     * @param event
     *            PaintEvent.
     * @throws PaintException
     *             The paint operation failed.
     */
    public synchronized void paintContent(PaintTarget target)
            throws PaintException {

        // Set the window name
        target.addAttribute("name", getName());

        // Mark main window
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

        // Set focused component
        if (this.focusedComponent != null)
            target.addVariable(this, "focused", ""
                    + this.focusedComponent.getFocusableId());
        else
            target.addVariable(this, "focused", "");

    }

    /* ********************************************************************* */

    /**
     * Open the given resource in this window.
     */
    public void open(Resource resource) {
        synchronized (openList) {
            openList.add(new OpenResource(resource, null, -1, -1,
                    BORDER_DEFAULT));
        }
        requestRepaint();
    }

    /* ********************************************************************* */

    /**
     * Open the given resource in named terminal window. Empty or
     * <code>null</code> window name results the resource to be opened in this
     * window.
     */
    public void open(Resource resource, String windowName) {
        synchronized (openList) {
            openList.add(new OpenResource(resource, windowName, -1, -1,
                    BORDER_DEFAULT));
        }
        requestRepaint();
    }

    /* ********************************************************************* */

    /**
     * Open the given resource in named terminal window with given size and
     * border properties. Empty or <code>null</code> window name results the
     * resource to be opened in this window.
     */
    public void open(Resource resource, String windowName, int width,
            int height, int border) {
        synchronized (openList) {
            openList.add(new OpenResource(resource, windowName, width, height,
                    border));
        }
        requestRepaint();
    }

    /* ********************************************************************* */

    /**
     * Returns the full url of the window, this returns window specific url even
     * for the main window.
     * 
     * @return String
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
     * Get the unique name of the window that indentifies it on the terminal.
     * 
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the border.
     * 
     * @return int
     */
    public int getBorder() {
        return border;
    }

    /**
     * Sets the border.
     * 
     * @param border
     *            The border to set
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
     *            The application to set
     */
    public void setApplication(Application application) {

        // If the application is not changed, dont do nothing
        if (application == this.application)
            return;

        // Send detach event if the window is connected to application
        if (this.application != null) {
            detach();
        }

        // Connect to new parent
        this.application = application;

        // Send attach event if connected to a window
        if (application != null)
            attach();
    }

    /**
     * Sets the name.
     * <p>
     * The name of the window must be unique inside the application. Also the
     * name may only contain the following characters: a-z, A-Z and 0-9.
     * </p>
     * 
     * <p>
     * If the name is null, the the window is given name automatically when it
     * is added to an application.
     * </p>
     * 
     * @param name
     *            The name to set
     */
    public void setName(String name) {

        // The name can not be changed in application
        if (getApplication() != null)
            throw new IllegalStateException(
                    "Window name can not be changed while "
                            + "the window is in application");

        // Check the name format
        if (name != null)
            for (int i = 0; i < name.length(); i++) {
                char c = name.charAt(i);
                if (!(('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || ('0' <= c && c <= '9')))
                    throw new IllegalArgumentException(
                            "Window name can contain "
                                    + "only a-z, A-Z and 0-9 characters: '"
                                    + name + "' given.");
            }

        this.name = name;
    }

    /**
     * Set terminal type. The terminal type is set by the the terminal adapter
     * and may change from time to time.
     * 
     * @param type
     *            terminal type to set
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

    /** Private data structure for storing opening window properties */
    private class OpenResource {

        private Resource resource;

        private String name;

        private int width;

        private int height;

        private int border;

        /** Create new open resource */
        private OpenResource(Resource resource, String name, int width,
                int height, int border) {
            this.resource = resource;
            this.name = name;
            this.width = width;
            this.height = height;
            this.border = border;
        }

        /** Paint the open-tag inside the window. */
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
     * @see com.itmill.toolkit.terminal.VariableOwner#changeVariables(java.lang.Object,
     *      java.util.Map)
     */
    public void changeVariables(Object source, Map variables) {
        super.changeVariables(source, variables);

        // Get focused component
        String focusedId = (String) variables.get("focused");
        if (focusedId != null) {
            try {
                long id = Long.parseLong(focusedId);
                this.focusedComponent = Window.getFocusableById(id);
            } catch (NumberFormatException ignored) {
                // We ignore invalid focusable ids
            }
        }

    }

    /**
     * Get currently focused component in this window.
     * 
     * @return Focused component or null if none is focused.
     */
    public Component.Focusable getFocusedComponent() {
        return this.focusedComponent;
    }

    /**
     * Set currently focused component in this window.
     * 
     * @param focusable
     *            Focused component or null if none is focused.
     */
    public void setFocusedComponent(Component.Focusable focusable) {
        this.focusedComponent = focusable;
    }

    /* Focusable id generator ****************************************** */

    private static long lastUsedFocusableId = 0;

    private static Map focusableComponents = new HashMap();

    /** Get an id for focusable component. */
    public static long getNewFocusableId(Component.Focusable focusable) {
        long newId = ++lastUsedFocusableId;
        WeakReference ref = new WeakReference(focusable);
        focusableComponents.put(new Long(newId), ref);
        return newId;
    }

    /** Map focusable id back to focusable component. */
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

    /** Release focusable component id when not used anymore. */
    public static void removeFocusableId(long focusableId) {
        Long id = new Long(focusableId);
        WeakReference ref = (WeakReference) focusableComponents.get(id);
        ref.clear();
        focusableComponents.remove(id);
    }
}
