/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.navigator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Root;
import com.vaadin.ui.Root.FragmentChangedEvent;
import com.vaadin.ui.Root.FragmentChangedListener;

/**
 * Navigator utility that allows switching of views in a part of an application.
 * 
 * The view switching can be based e.g. on URI fragments containing the view
 * name and parameters to the view. There are two types of parameters for views:
 * an optional parameter string that is included in the fragment (may be
 * bookmarkable).
 * 
 * Views can be explicitly registered or dynamically generated and listening to
 * view changes is possible.
 * 
 * Note that {@link Navigator} is not a component itself but comes with
 * {@link SimpleViewDisplay} which is a component that displays the selected
 * view as its contents.
 * 
 * @author Vaadin Ltd
 * @since 7.0
 */
public class Navigator implements Serializable {

    // TODO divert navigation e.g. if no permissions? Or just show another view
    // but keep URL? how best to intercept
    // TODO investigate relationship to TouchKit navigation support

    /**
     * Empty view component.
     */
    public static class EmptyView extends CssLayout implements View {
        public EmptyView() {
            setWidth("0px");
            setHeight("0px");
        }

        public void navigateTo(String fragmentParameters) {
            // nothing to do
        }
    }

    /**
     * Fragment manager using URI fragments of a Root to track views and enable
     * listening to view changes.
     * 
     * This class is mostly for internal use by Navigator, and is only public
     * and static to enable testing.
     */
    public static class UriFragmentManager implements FragmentManager,
            FragmentChangedListener {
        private final Root root;
        private final Navigator navigator;

        /**
         * Create a new URIFragmentManager and attach it to listen to URI
         * fragment changes of a {@link Root}.
         * 
         * @param root
         *            root whose URI fragment to get and modify
         * @param navigator
         *            {@link Navigator} to notify of fragment changes (using
         *            {@link Navigator#navigateTo(String, Object...)}
         */
        public UriFragmentManager(Root root, Navigator navigator) {
            this.root = root;
            this.navigator = navigator;

            root.addListener(this);
        }

        public String getFragment() {
            return root.getFragment();
        }

        public void setFragment(String fragment) {
            // TODO ", false" ???
            root.setFragment(fragment);
        }

        public void fragmentChanged(FragmentChangedEvent event) {
            UriFragmentManager.this.navigator.navigateTo(getFragment());
        }
    }

    /**
     * View display that is a component itself and replaces its contents with
     * the view.
     * 
     * This display only supports views that are {@link Component}s themselves.
     * Attempting to display a view that is not a component causes an exception
     * to be thrown.
     * 
     * By default, the view display has full size.
     */
    public static class SimpleViewDisplay extends CustomComponent implements
            ViewDisplay {

        /**
         * Create new {@link ViewDisplay} that is itself a component displaying
         * the view.
         */
        public SimpleViewDisplay() {
            setSizeFull();
        }

        public void showView(View view) {
            if (view instanceof Component) {
                setCompositionRoot((Component) view);
            } else {
                throw new IllegalArgumentException("View is not a component: "
                        + view);
            }
        }
    }

    /**
     * View provider which uses a map from view name to pre-created and
     * registered view instances.
     */
    public static class RegisteredViewProvider implements ViewProvider {

        private HashMap<String, View> viewNameToView = new HashMap<String, View>();

        public String getViewName(String viewAndParameters) {
            if (null == viewAndParameters) {
                return null;
            }
            for (String viewName : viewNameToView.keySet()) {
                if (viewAndParameters.equals(viewName)
                        || viewAndParameters.startsWith(viewName + "/")) {
                    return viewName;
                }
            }
            return null;
        }

        public View getView(String viewName) {
            return viewNameToView.get(viewName);
        }

        /**
         * Register a view for a view name.
         * 
         * Registering another view with a name that is already registered
         * overwrites the old registration.
         * 
         * @param viewName
         *            String that identifies a view (not null nor empty string)
         * @param view
         *            {@link View} instance (not null)
         */
        public void addView(String viewName, View view) {

            // Check parameters
            if (viewName == null || view == null || viewName.length() == 0) {
                throw new IllegalArgumentException(
                        "view and viewName must be non-null and not empty");
            }

            viewNameToView.put(viewName, view);
        }

        /**
         * Remove view from navigator.
         * 
         * @param viewName
         *            name of the view to remove
         */
        public void removeView(String viewName) {
            viewNameToView.remove(viewName);
        }
    }

    /**
     * View provider which uses a map from view name to the class to instantiate
     * for the view.
     * 
     * Views that have been created are cached and reused when the same view
     * name is requested again.
     * 
     * Note that the view class must be accessible by the class loader used by
     * the provider. This may require its visibility to be public.
     */
    public static class ClassBasedViewProvider implements ViewProvider {

        private HashMap<String, Class<? extends View>> viewNameToClass = new HashMap<String, Class<? extends View>>();
        private HashMap<Class<? extends View>, String> classToViewName = new HashMap<Class<? extends View>, String>();
        /**
         * Already opened (cached) views that can be reopened or reused with new
         * parameters.
         */
        private HashMap<Class<? extends View>, View> classToView = new HashMap<Class<? extends View>, View>();

        public String getViewName(String viewAndParameters) {
            if (null == viewAndParameters) {
                return null;
            }
            for (String viewName : viewNameToClass.keySet()) {
                if (viewAndParameters.equals(viewName)
                        || viewAndParameters.startsWith(viewName + "/")) {
                    return viewName;
                }
            }
            return null;
        }

        public View getView(String viewName) {
            Class<? extends View> newViewClass = viewNameToClass.get(viewName);
            if (null == newViewClass) {
                return null;
            }
            if (!classToView.containsKey(newViewClass)) {
                try {
                    View view = newViewClass.newInstance();
                    classToView.put(newViewClass, view);
                } catch (InstantiationException e) {
                    // TODO error handling
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    // TODO error handling
                    throw new RuntimeException(e);
                }
            }
            // return already cached view
            final View v = classToView.get(newViewClass);
            return v;
        }

        /**
         * Register a view class for a view name.
         * 
         * @param viewName
         *            String that identifies a view (not null nor empty string)
         * @param viewClass
         *            Component class that implements Navigator.View interface
         *            (not null)
         */
        public void addView(String viewName, Class<? extends View> viewClass) {

            // Check parameters
            if (viewName == null || viewClass == null || viewName.length() == 0) {
                throw new IllegalArgumentException(
                        "viewClass and viewName must be non-null and not empty");
            }

            if (!View.class.isAssignableFrom(viewClass)) {
                throw new IllegalArgumentException(
                        "viewClass must implement Navigator.View");
            }

            if (viewNameToClass.containsKey(viewName)) {
                if (viewNameToClass.get(viewName) == viewClass) {
                    return;
                }

                throw new IllegalArgumentException(viewNameToClass
                        .get(viewName).getName()
                        + " is already mapped to '"
                        + viewName + "'");
            }

            if (classToViewName.containsKey(viewClass)) {
                throw new IllegalArgumentException(
                        "Each view class can only be added to Navigator with one view name");
            }

            viewNameToClass.put(viewName, viewClass);
            classToViewName.put(viewClass, viewName);
        }

        /**
         * Remove view from navigator.
         * 
         * @param viewName
         *            name of the view to remove
         */
        public void removeView(String viewName) {
            Class<? extends View> c = viewNameToClass.get(viewName);
            if (c != null) {
                viewNameToClass.remove(viewName);
                classToViewName.remove(c);
                classToView.remove(c);
            }
        }

        /**
         * Get the view name for given view implementation class.
         * 
         * @param viewClass
         *            Class that implements the view.
         * @return view name for which the view class is registered, null if
         *         none
         */
        public String getViewName(Class<? extends View> viewClass) {
            return classToViewName.get(viewClass);
        }

        /**
         * Get the view class for given view name.
         * 
         * @param viewName
         *            view name to get view for
         * @return View that corresponds to the name
         */
        public Class<? extends View> getViewClass(String viewName) {
            return viewNameToClass.get(viewName);
        }
    }

    private final FragmentManager fragmentManager;
    private final ViewDisplay display;
    private View currentView = null;
    private List<ViewChangeListener> listeners = new LinkedList<ViewChangeListener>();
    private List<ViewProvider> providers = new LinkedList<ViewProvider>();

    /**
     * Create a navigator that is tracking the active view using URI fragments.
     * 
     * @param root
     *            whose URI fragments are used
     * @param display
     *            where to display the views
     */
    public Navigator(Root root, ViewDisplay display) {
        this.display = display;
        fragmentManager = new UriFragmentManager(root, this);
    }

    /**
     * Create a navigator that is tracking the active view using URI fragments.
     * By default, a {@link SimpleViewDisplay} is used and can be obtained using
     * {@link #getDisplay()}.
     * 
     * @param root
     *            whose URI fragments are used
     */
    public Navigator(Root root) {
        display = new SimpleViewDisplay();
        fragmentManager = new UriFragmentManager(root, this);
    }

    /**
     * Create a navigator.
     * 
     * When a custom fragment manager is not needed, use the constructor
     * {@link #Navigator(Root, ViewDisplay)} which uses a URI fragment based
     * fragment manager.
     * 
     * @param fragmentManager
     *            fragment manager keeping track of the active view and enabling
     *            bookmarking and direct navigation
     * @param display
     *            where to display the views
     */
    public Navigator(FragmentManager fragmentManager, ViewDisplay display) {
        this.display = display;
        this.fragmentManager = fragmentManager;
    }

    /**
     * Navigate to a view and initialize the view with given parameters.
     * 
     * The view string consists of a view name optionally followed by a slash
     * and (fragment) parameters. ViewProviders are used to find and create the
     * correct type of view.
     * 
     * If the view being left indicates it wants a confirmation for the
     * navigation operation, the user is asked for the confirmation.
     * 
     * @param viewAndParameters
     *            view name and parameters
     */
    public void navigateTo(String viewAndParameters) {
        for (ViewProvider provider : providers) {
            String viewName = provider.getViewName(viewAndParameters);
            if (null != viewName) {
                String parameters = null;
                if (viewAndParameters.length() > viewName.length() + 1) {
                    parameters = viewAndParameters
                            .substring(viewName.length() + 1);
                }
                View view = provider.getView(viewName);
                if (null != view) {
                    navigateTo(view, viewName, parameters);
                    // stop after a view is found
                    return;
                }
            }
        }
        // TODO if no view is found, what to do?
    }

    /**
     * Internal method activating a view, setting its parameters and calling
     * listeners.
     * 
     * This method also verifies that the user is allowed to perform the
     * navigation operation.
     * 
     * @param view
     *            view to activate
     * @param viewName
     *            (optional) name of the view or null not to set the fragment
     * @param fragmentParameters
     *            parameters passed in the fragment for the view
     */
    protected void navigateTo(View view, String viewName,
            String fragmentParameters) {
        ViewChangeEvent event = new ViewChangeEvent(this, currentView, view,
                viewName, fragmentParameters);
        if (!isViewChangeAllowed(event)) {
            return;
        }

        if (null != viewName && getFragmentManager() != null) {
            String currentFragment = viewName;
            if (fragmentParameters != null) {
                currentFragment += "/" + fragmentParameters;
            }
            if (!currentFragment.equals(getFragmentManager().getFragment())) {
                getFragmentManager().setFragment(currentFragment);
            }
        }

        view.navigateTo(fragmentParameters);
        currentView = view;

        if (display != null) {
            display.showView(view);
        }

        fireViewChange(event);
    }

    /**
     * Check whether view change is allowed.
     * 
     * All related listeners are called. The view change is blocked if any of
     * them wants to block the navigation operation.
     * 
     * The view change listeners may also e.g. open a warning or question dialog
     * and save the parameters to re-initiate the navigation operation upon user
     * action.
     * 
     * @param event
     *            view change event (not null, view change not yet performed)
     * @return true if the view change should be allowed, false to silently
     *         block the navigation operation
     */
    protected boolean isViewChangeAllowed(ViewChangeEvent event) {
        for (ViewChangeListener l : listeners) {
            if (!l.isViewChangeAllowed(event)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return the fragment manager that is used to get, listen to and manipulate
     * the URI fragment or other source of navigation information.
     * 
     * @return fragment manager in use
     */
    protected FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    /**
     * Returns the ViewDisplay used by the navigator. Unless another display is
     * specified, a {@link SimpleViewDisplay} (which is a {@link Component}) is
     * used by default.
     * 
     * @return current ViewDisplay
     */
    public ViewDisplay getDisplay() {
        return display;
    }

    /**
     * Fire an event when the current view has changed.
     * 
     * @param event
     *            view change event (not null)
     */
    protected void fireViewChange(ViewChangeEvent event) {
        for (ViewChangeListener l : listeners) {
            l.navigatorViewChanged(event);
        }
    }

    /**
     * Register a view provider (factory).
     * 
     * Providers are called in order of registration until one that can handle
     * the requested view name is found.
     * 
     * @param provider
     *            provider to register
     */
    public void registerProvider(ViewProvider provider) {
        providers.add(provider);
    }

    /**
     * Unregister a view provider (factory).
     * 
     * @param provider
     *            provider to unregister
     */
    public void unregisterProvider(ViewProvider provider) {
        providers.remove(provider);
    }

    /**
     * Listen to changes of the active view.
     * 
     * The listener will get notified after the view has changed.
     * 
     * @param listener
     *            Listener to invoke after view changes.
     */
    public void addListener(ViewChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove a view change listener.
     * 
     * @param listener
     *            Listener to remove.
     */
    public void removeListener(ViewChangeListener listener) {
        listeners.remove(listener);
    }

}
