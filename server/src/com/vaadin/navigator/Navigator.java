package com.vaadin.navigator;

/*
 * Copyright 2011 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.Page.FragmentChangedEvent;
import com.vaadin.server.Page.FragmentChangedListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;

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
    // TODO investigate relationship with TouchKit navigation support

    /**
     * Empty view component.
     */
    public static class EmptyView extends CssLayout implements View {
        /**
         * Create minimally sized empty view.
         */
        public EmptyView() {
            setWidth("0px");
            setHeight("0px");
        }

        @Override
        public void navigateTo(String fragmentParameters) {
            // nothing to do
        }
    }

    /**
     * Fragment manager using URI fragments of a Page to track views and enable
     * listening to view changes.
     * 
     * This class is mostly for internal use by Navigator, and is only public
     * and static to enable testing.
     */
    public static class UriFragmentManager implements FragmentManager,
            FragmentChangedListener {
        private final Page page;
        private final Navigator navigator;

        /**
         * Create a new URIFragmentManager and attach it to listen to URI
         * fragment changes of a {@link Page}.
         * 
         * @param page
         *            page whose URI fragment to get and modify
         * @param navigator
         *            {@link Navigator} to notify of fragment changes (using
         *            {@link Navigator#navigateTo(String)}
         */
        public UriFragmentManager(Page page, Navigator navigator) {
            this.page = page;
            this.navigator = navigator;

            page.addListener(this);
        }

        @Override
        public String getFragment() {
            return page.getFragment();
        }

        @Override
        public void setFragment(String fragment) {
            page.setFragment(fragment, false);
        }

        @Override
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

        @Override
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
     * View display that replaces the contents of a {@link ComponentContainer}
     * with the active {@link View}.
     * 
     * All components of the container are removed before adding the new view to
     * it.
     * 
     * This display only supports views that are {@link Component}s themselves.
     * Attempting to display a view that is not a component causes an exception
     * to be thrown.
     */
    public static class ComponentContainerViewDisplay implements ViewDisplay {

        private final ComponentContainer container;

        /**
         * Create new {@link ViewDisplay} that updates a
         * {@link ComponentContainer} to show the view.
         */
        public ComponentContainerViewDisplay(ComponentContainer container) {
            this.container = container;
        }

        @Override
        public void showView(View view) {
            if (view instanceof Component) {
                container.removeAllComponents();
                container.addComponent((Component) view);
            } else {
                throw new IllegalArgumentException("View is not a component: "
                        + view);
            }
        }
    }

    /**
     * View provider which supports mapping a single view name to a single
     * pre-initialized view instance.
     * 
     * For most cases, ClassBasedViewProvider should be used instead of this.
     */
    public static class StaticViewProvider implements ViewProvider {
        private final String viewName;
        private final View view;

        /**
         * Create a new view provider which returns a pre-created view instance.
         * 
         * @param viewName
         *            name of the view (not null)
         * @param view
         *            view instance to return (not null), reused on every
         *            request
         */
        public StaticViewProvider(String viewName, View view) {
            this.viewName = viewName;
            this.view = view;
        }

        @Override
        public String getViewName(String viewAndParameters) {
            if (null == viewAndParameters) {
                return null;
            }
            if (viewAndParameters.startsWith(viewName)) {
                return viewName;
            }
            return null;
        }

        @Override
        public View getView(String viewName) {
            if (this.viewName.equals(viewName)) {
                return view;
            }
            return null;
        }

        /**
         * Get the view name for this provider.
         * 
         * @return view name for this provider
         */
        public String getViewName() {
            return viewName;
        }
    }

    /**
     * View provider which maps a single view name to a class to instantiate for
     * the view.
     * 
     * Note that the view class must be accessible by the class loader used by
     * the provider. This may require its visibility to be public.
     * 
     * This class is primarily for internal use by {@link Navigator}.
     */
    public static class ClassBasedViewProvider implements ViewProvider {

        private final String viewName;
        private final Class<? extends View> viewClass;

        /**
         * Create a new view provider which creates new view instances based on
         * a view class.
         * 
         * @param viewName
         *            name of the views to create (not null)
         * @param viewClass
         *            class to instantiate when a view is requested (not null)
         */
        public ClassBasedViewProvider(String viewName,
                Class<? extends View> viewClass) {
            if (null == viewName || null == viewClass) {
                throw new IllegalArgumentException(
                        "View name and class should not be null");
            }
            this.viewName = viewName;
            this.viewClass = viewClass;
        }

        @Override
        public String getViewName(String viewAndParameters) {
            if (null == viewAndParameters) {
                return null;
            }
            if (viewAndParameters.equals(viewName)
                    || viewAndParameters.startsWith(viewName + "/")) {
                return viewName;
            }
            return null;
        }

        @Override
        public View getView(String viewName) {
            if (this.viewName.equals(viewName)) {
                try {
                    View view = viewClass.newInstance();
                    return view;
                } catch (InstantiationException e) {
                    // TODO error handling
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    // TODO error handling
                    throw new RuntimeException(e);
                }
            }
            return null;
        }

        /**
         * Get the view name for this provider.
         * 
         * @return view name for this provider
         */
        public String getViewName() {
            return viewName;
        }

        /**
         * Get the view class for this provider.
         * 
         * @return {@link View} class
         */
        public Class<? extends View> getViewClass() {
            return viewClass;
        }
    }

    private final FragmentManager fragmentManager;
    private final ViewDisplay display;
    private View currentView = null;
    private List<ViewChangeListener> listeners = new LinkedList<ViewChangeListener>();
    private List<ViewProvider> providers = new LinkedList<ViewProvider>();

    /**
     * Create a navigator that is tracking the active view using URI fragments
     * of the current {@link Page} and replacing the contents of a
     * {@link ComponentContainer} with the active view.
     * 
     * In case the container is not on the current page, use another
     * {@link Navigator#Navigator(Page, ViewDisplay)} with an explicitly created
     * {@link ComponentContainerViewDisplay}.
     * 
     * All components of the container are removed each time before adding the
     * active {@link View}. Views must implement {@link Component} when using
     * this constructor.
     * 
     * <p>
     * After all {@link View}s and {@link ViewProvider}s have been registered,
     * the application should trigger navigation to the current fragment using
     * e.g.
     * 
     * <pre>
     * navigator.navigateTo(Page.getCurrent().getFragment());
     * </pre>
     * 
     * @param container
     *            ComponentContainer whose contents should be replaced with the
     *            active view on view change
     */
    public Navigator(ComponentContainer container) {
        display = new ComponentContainerViewDisplay(container);
        fragmentManager = new UriFragmentManager(Page.getCurrent(), this);
    }

    /**
     * Create a navigator that is tracking the active view using URI fragments.
     * 
     * <p>
     * After all {@link View}s and {@link ViewProvider}s have been registered,
     * the application should trigger navigation to the current fragment using
     * e.g.
     * 
     * <pre>
     * navigator.navigateTo(Page.getCurrent().getFragment());
     * </pre>
     * 
     * @param page
     *            whose URI fragments are used
     * @param display
     *            where to display the views
     */
    public Navigator(Page page, ViewDisplay display) {
        this.display = display;
        fragmentManager = new UriFragmentManager(page, this);
    }

    /**
     * Create a navigator.
     * 
     * When a custom fragment manager is not needed, use the constructor
     * {@link #Navigator(Page, ViewDisplay)} which uses a URI fragment based
     * fragment manager.
     * 
     * Note that navigation to the initial view must be performed explicitly by
     * the application after creating a Navigator using this constructor.
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
     * If multiple providers return a matching view, the view with the longest
     * name is selected. This way, e.g. hierarchies of subviews can be
     * registered like "admin/", "admin/users", "admin/settings" and the longest
     * match is used.
     * 
     * If the view being deactivated indicates it wants a confirmation for the
     * navigation operation, the user is asked for the confirmation.
     * 
     * Registered {@link ViewChangeListener}s are called upon successful view
     * change.
     * 
     * @param viewAndParameters
     *            view name and parameters
     */
    public void navigateTo(String viewAndParameters) {
        String longestViewName = null;
        View viewWithLongestName = null;
        for (ViewProvider provider : providers) {
            String viewName = provider.getViewName(viewAndParameters);
            if (null != viewName
                    && (longestViewName == null || viewName.length() > longestViewName
                            .length())) {
                View view = provider.getView(viewName);
                if (null != view) {
                    longestViewName = viewName;
                    viewWithLongestName = view;
                }
            }
        }
        if (viewWithLongestName != null) {
            String parameters = null;
            if (viewAndParameters.length() > longestViewName.length() + 1) {
                parameters = viewAndParameters.substring(longestViewName
                        .length() + 1);
            }
            navigateTo(viewWithLongestName, longestViewName, parameters);
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
     * Register a static, pre-initialized view instance for a view name.
     * 
     * Registering another view with a name that is already registered
     * overwrites the old registration of the same type.
     * 
     * @param viewName
     *            String that identifies a view (not null nor empty string)
     * @param view
     *            {@link View} instance (not null)
     */
    public void addView(String viewName, View view) {

        // Check parameters
        if (viewName == null || view == null) {
            throw new IllegalArgumentException(
                    "view and viewName must be non-null");
        }

        removeView(viewName);
        addProvider(new StaticViewProvider(viewName, view));
    }

    /**
     * Register for a view name a view class.
     * 
     * Registering another view with a name that is already registered
     * overwrites the old registration of the same type.
     * 
     * A new view instance is created every time a view is requested.
     * 
     * @param viewName
     *            String that identifies a view (not null nor empty string)
     * @param viewClass
     *            {@link View} class to instantiate when a view is requested
     *            (not null)
     */
    public void addView(String viewName, Class<? extends View> viewClass) {

        // Check parameters
        if (viewName == null || viewClass == null) {
            throw new IllegalArgumentException(
                    "view and viewClass must be non-null");
        }

        removeView(viewName);
        addProvider(new ClassBasedViewProvider(viewName, viewClass));
    }

    /**
     * Remove view from navigator.
     * 
     * This method only applies to views registered using
     * {@link #addView(String, View)} or {@link #addView(String, Class)}.
     * 
     * @param viewName
     *            name of the view to remove
     */
    public void removeView(String viewName) {
        Iterator<ViewProvider> it = providers.iterator();
        while (it.hasNext()) {
            ViewProvider provider = it.next();
            if (provider instanceof StaticViewProvider) {
                StaticViewProvider staticProvider = (StaticViewProvider) provider;
                if (staticProvider.getViewName().equals(viewName)) {
                    it.remove();
                }
            } else if (provider instanceof ClassBasedViewProvider) {
                ClassBasedViewProvider classBasedProvider = (ClassBasedViewProvider) provider;
                if (classBasedProvider.getViewName().equals(viewName)) {
                    it.remove();
                }
            }
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
    public void addProvider(ViewProvider provider) {
        providers.add(provider);
    }

    /**
     * Unregister a view provider (factory).
     * 
     * @param provider
     *            provider to unregister
     */
    public void removeProvider(ViewProvider provider) {
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
