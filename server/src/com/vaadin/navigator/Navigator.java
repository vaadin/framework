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
import com.vaadin.ui.UI;

/**
 * A navigator utility that allows switching of views in a part of an
 * application.
 * <p>
 * The view switching can be based e.g. on URI fragments containing the view
 * name and parameters to the view. There are two types of parameters for views:
 * an optional parameter string that is included in the fragment (may be
 * bookmarkable).
 * <p>
 * Views can be explicitly registered or dynamically generated and listening to
 * view changes is possible.
 * <p>
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
        public void enter(ViewChangeEvent event) {
            // nothing to do
        }
    }

    /**
     * A {@link NavigationStateManager} using hashbang fragments in the Page
     * location URI to track views and enable listening to view changes.
     * <p>
     * A hashbang URI is one where the optional fragment or "hash" part - the
     * part following a # sign - is used to encode navigation state in a web
     * application. The advantage of this is that the fragment can be
     * dynamically manipulated by javascript without causing page reloads.
     * <p>
     * This class is mostly for internal use by Navigator, and is only public
     * and static to enable testing.
     */
    public static class UriFragmentManager implements NavigationStateManager,
            FragmentChangedListener {
        private final Page page;
        private final Navigator navigator;

        /**
         * Creates a new URIFragmentManager and attach it to listen to URI
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

            page.addFragmentChangedListener(this);
        }

        @Override
        public String getState() {
            String fragment = page.getFragment();
            if (fragment.startsWith("!")) {
                return page.getFragment().substring(1);
            } else {
                return "";
            }
        }

        @Override
        public void setState(String state) {
            page.setFragment("!" + state, false);
        }

        @Override
        public void fragmentChanged(FragmentChangedEvent event) {
            navigator.navigateTo(getState());
        }
    }

    /**
     * A ViewDisplay that is a component itself and replaces its contents with
     * the view.
     * <p>
     * This display only supports views that are {@link Component}s themselves.
     * Attempting to display a view that is not a component causes an exception
     * to be thrown.
     * <p>
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
     * A ViewDisplay that replaces the contents of a {@link ComponentContainer}
     * with the active {@link View}.
     * <p>
     * All components of the container are removed before adding the new view to
     * it.
     * <p>
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
     * A ViewProvider which supports mapping a single view name to a single
     * pre-initialized view instance.
     * 
     * For most cases, ClassBasedViewProvider should be used instead of this.
     */
    public static class StaticViewProvider implements ViewProvider {
        private final String viewName;
        private final View view;

        /**
         * Creates a new view provider which returns a pre-created view
         * instance.
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
        public String getViewName(String navigationState) {
            if (null == navigationState) {
                return null;
            }
            if (navigationState.equals(viewName)
                    || navigationState.startsWith(viewName + "/")) {
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
     * A ViewProvider which maps a single view name to a class to instantiate
     * for the view.
     * <p>
     * Note that the view class must be accessible by the class loader used by
     * the provider. This may require its visibility to be public.
     * <p>
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
        public String getViewName(String navigationState) {
            if (null == navigationState) {
                return null;
            }
            if (navigationState.equals(viewName)
                    || navigationState.startsWith(viewName + "/")) {
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

    private final UI ui;
    private final NavigationStateManager stateManager;
    private final ViewDisplay display;
    private View currentView = null;
    private List<ViewChangeListener> listeners = new LinkedList<ViewChangeListener>();
    private List<ViewProvider> providers = new LinkedList<ViewProvider>();

    /**
     * Creates a navigator that is tracking the active view using URI fragments
     * of the current {@link Page} and replacing the contents of a
     * {@link ComponentContainer} with the active view.
     * <p>
     * In case the container is not on the current page, use another
     * {@link Navigator#Navigator(Page, ViewDisplay)} with an explicitly created
     * {@link ComponentContainerViewDisplay}.
     * <p>
     * All components of the container are removed each time before adding the
     * active {@link View}. Views must implement {@link Component} when using
     * this constructor.
     * <p>
     * After all {@link View}s and {@link ViewProvider}s have been registered,
     * the application should trigger navigation to the current fragment using
     * {@link #navigate()}.
     * 
     * @param ui
     *            The UI to which this Navigator is attached.
     * @param container
     *            The ComponentContainer whose contents should be replaced with
     *            the active view on view change
     */
    public Navigator(UI ui, ComponentContainer container) {
        this(ui, new ComponentContainerViewDisplay(container));
    }

    /**
     * Creates a navigator that is tracking the active view using URI fragments
     * of the Page containing the given UI.
     * <p>
     * After all {@link View}s and {@link ViewProvider}s have been registered,
     * the application should trigger navigation to the current fragment using
     * {@link #navigate()}.
     * 
     * @param ui
     *            The UI to which this Navigator is attached.
     * @param display
     *            The ViewDisplay used to display the views.
     */
    public Navigator(UI ui, ViewDisplay display) {
        this.ui = ui;
        this.ui.setNavigator(this);
        this.display = display;
        stateManager = new UriFragmentManager(ui.getPage(), this);
    }

    /**
     * Creates a navigator.
     * <p>
     * When a custom navigation state manager is not needed, use the constructor
     * {@link #Navigator(Page, ViewDisplay)} which uses a URI fragment based
     * state manager.
     * <p>
     * After all {@link View}s and {@link ViewProvider}s have been registered,
     * the application should trigger navigation to the current fragment using
     * {@link #navigate()}.
     * 
     * @param ui
     *            The UI to which this Navigator is attached.
     * @param stateManager
     *            The NavigationStateManager keeping track of the active view
     *            and enabling bookmarking and direct navigation
     * @param display
     *            The ViewDisplay used to display the views handled by this
     *            navigator
     */
    public Navigator(UI ui, NavigationStateManager stateManager,
            ViewDisplay display) {
        this.ui = ui;
        this.ui.setNavigator(this);
        this.display = display;
        this.stateManager = stateManager;
    }

    /**
     * Navigates to the current navigation state. This method should be called
     * when all required {@link View}s, {@link ViewProvider}s, and
     * {@link ViewChangeListener}s have been added to the navigator.
     */
    public void navigate() {
        navigateTo(getStateManager().getState());
    }

    /**
     * Navigates to a view and initialize the view with given parameters.
     * <p>
     * The view string consists of a view name optionally followed by a slash
     * and a parameters part that is passed as-is to the view. ViewProviders are
     * used to find and create the correct type of view.
     * <p>
     * If multiple providers return a matching view, the view with the longest
     * name is selected. This way, e.g. hierarchies of subviews can be
     * registered like "admin/", "admin/users", "admin/settings" and the longest
     * match is used.
     * <p>
     * If the view being deactivated indicates it wants a confirmation for the
     * navigation operation, the user is asked for the confirmation.
     * <p>
     * Registered {@link ViewChangeListener}s are called upon successful view
     * change.
     * 
     * @param navigationState
     *            view name and parameters
     */
    public void navigateTo(String navigationState) {
        String longestViewName = null;
        View viewWithLongestName = null;
        for (ViewProvider provider : providers) {
            String viewName = provider.getViewName(navigationState);
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
            String parameters = "";
            if (navigationState.length() > longestViewName.length() + 1) {
                parameters = navigationState
                        .substring(longestViewName.length() + 1);
            }
            navigateTo(viewWithLongestName, longestViewName, parameters);
        }
        // TODO if no view is found, what to do?
    }

    /**
     * Internal method activating a view, setting its parameters and calling
     * listeners.
     * <p>
     * This method also verifies that the user is allowed to perform the
     * navigation operation.
     * 
     * @param view
     *            view to activate
     * @param viewName
     *            (optional) name of the view or null not to change the
     *            navigation state
     * @param parameters
     *            parameters passed in the navigation state to the view
     */
    protected void navigateTo(View view, String viewName, String parameters) {
        ViewChangeEvent event = new ViewChangeEvent(this, currentView, view,
                viewName, parameters);
        if (!fireBeforeViewChange(event)) {
            return;
        }

        if (null != viewName && getStateManager() != null) {
            String navigationState = viewName;
            if (!parameters.isEmpty()) {
                navigationState += "/" + parameters;
            }
            if (!navigationState.equals(getStateManager().getState())) {
                getStateManager().setState(navigationState);
            }
        }

        view.enter(event);
        currentView = view;

        if (display != null) {
            display.showView(view);
        }

        fireAfterViewChange(event);
    }

    /**
     * Fires an event before an imminent view change.
     * <p>
     * Listeners are called in registration order. If any listener returns
     * <code>false</code>, the rest of the listeners are not called and the view
     * change is blocked.
     * <p>
     * The view change listeners may also e.g. open a warning or question dialog
     * and save the parameters to re-initiate the navigation operation upon user
     * action.
     * 
     * @param event
     *            view change event (not null, view change not yet performed)
     * @return true if the view change should be allowed, false to silently
     *         block the navigation operation
     */
    protected boolean fireBeforeViewChange(ViewChangeEvent event) {
        for (ViewChangeListener l : listeners) {
            if (!l.beforeViewChange(event)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the {@link NavigationStateManager} that is used to get, listen to
     * and manipulate the navigation state used by this Navigator.
     * 
     * @return NavigationStateManager in use
     */
    protected NavigationStateManager getStateManager() {
        return stateManager;
    }

    /**
     * Return the ViewDisplay used by the navigator. Unless another display is
     * specified, a {@link SimpleViewDisplay} (which is a {@link Component}) is
     * used by default.
     * 
     * @return current ViewDisplay
     */
    public ViewDisplay getDisplay() {
        return display;
    }

    public UI getUI() {
        return ui;
    }

    /**
     * Fires an event after the current view has changed.
     * <p>
     * Listeners are called in registration order.
     * 
     * @param event
     *            view change event (not null)
     */
    protected void fireAfterViewChange(ViewChangeEvent event) {
        for (ViewChangeListener l : listeners) {
            l.afterViewChange(event);
        }
    }

    /**
     * Registers a static, pre-initialized view instance for a view name.
     * <p>
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
     * Register a view class for a view name.
     * <p>
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
     * Removes a view from navigator.
     * <p>
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
     * Registers a view provider (factory).
     * <p>
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
     * Adds a listener for listening to changes of the active view.
     * <p>
     * The listener will get notified after the view has changed.
     * 
     * @param listener
     *            Listener to invoke during a view change.
     */
    public void addViewChangeListener(ViewChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a view change listener.
     * 
     * @param listener
     *            Listener to remove.
     */
    public void removeViewChangeListener(ViewChangeListener listener) {
        listeners.remove(listener);
    }
}
