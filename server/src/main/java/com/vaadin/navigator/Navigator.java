/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.navigator;

/*
 * Copyright 2000-2018 Vaadin Ltd.
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.server.Page.UriFragmentChangedListener;
import com.vaadin.shared.util.SharedUtil;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.SingleComponentContainer;
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
 * Note that {@link Navigator} is not a component itself but uses a
 * {@link ViewDisplay} to update contents based on the state.
 *
 * @author Vaadin Ltd
 * @since 7.0
 */
public class Navigator implements Serializable {

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
    public static class UriFragmentManager
            implements NavigationStateManager, UriFragmentChangedListener {
        private final Page page;
        private Navigator navigator;

        /**
         * Creates a new URIFragmentManager and attach it to listen to URI
         * fragment changes of a {@link Page}.
         *
         * @param page
         *            page whose URI fragment to get and modify
         */
        public UriFragmentManager(Page page) {
            this.page = page;
        }

        @Override
        public void setNavigator(Navigator navigator) {
            if (this.navigator == null && navigator != null) {
                page.addUriFragmentChangedListener(this);
            } else if (this.navigator != null && navigator == null) {
                page.removeUriFragmentChangedListener(this);
            }
            this.navigator = navigator;
        }

        @Override
        public String getState() {
            String fragment = getFragment();
            if (fragment == null || !fragment.startsWith("!")) {
                return "";
            } else {
                return fragment.substring(1);
            }
        }

        @Override
        public void setState(String state) {
            setFragment("!" + state);
        }

        @Override
        public void uriFragmentChanged(UriFragmentChangedEvent event) {
            navigator.navigateTo(getState());
        }

        /**
         * Returns the current URI fragment tracked by this UriFragentManager.
         *
         * @return The URI fragment.
         */
        protected String getFragment() {
            return page.getUriFragment();
        }

        /**
         * Sets the URI fragment to the given string.
         *
         * @param fragment
         *            The new URI fragment.
         */
        protected void setFragment(String fragment) {
            page.setUriFragment(fragment, false);
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
                throw new IllegalArgumentException(
                        "View is not a component: " + view);
            }
        }
    }

    /**
     * A ViewDisplay that replaces the contents of a
     * {@link SingleComponentContainer} with the active {@link View}.
     * <p>
     * This display only supports views that are {@link Component}s themselves.
     * Attempting to display a view that is not a component causes an exception
     * to be thrown.
     */
    public static class SingleComponentContainerViewDisplay
            implements ViewDisplay {

        private final SingleComponentContainer container;

        /**
         * Create new {@link ViewDisplay} that updates a
         * {@link SingleComponentContainer} to show the view.
         */
        public SingleComponentContainerViewDisplay(
                SingleComponentContainer container) {
            this.container = container;
        }

        @Override
        public void showView(View view) {
            if (view instanceof Component) {
                container.setContent((Component) view);
            } else {
                throw new IllegalArgumentException(
                        "View is not a component: " + view);
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

    private UI ui;
    private NavigationStateManager stateManager;
    private ViewDisplay display;
    private View currentView = null;
    private List<ViewChangeListener> listeners = new LinkedList<ViewChangeListener>();
    private List<ViewProvider> providers = new LinkedList<ViewProvider>();
    private String currentNavigationState = null;
    private ViewProvider errorProvider;

    /**
     * Creates a navigator that is tracking the active view using URI fragments
     * of the {@link Page} containing the given UI and replacing the contents of
     * a {@link ComponentContainer} with the active view.
     * <p>
     * All components of the container are removed each time before adding the
     * active {@link View}. Views must implement {@link Component} when using
     * this constructor.
     * <p>
     * Navigation is automatically initiated after {@code UI.init()} if a
     * navigator was created. If at a later point changes are made to the
     * navigator, {@code navigator.navigateTo(navigator.getState())} may need to
     * be explicitly called to ensure the current view matches the navigation
     * state.
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
     * of the {@link Page} containing the given UI and replacing the contents of
     * a {@link SingleComponentContainer} with the active view.
     * <p>
     * Views must implement {@link Component} when using this constructor.
     * <p>
     * Navigation is automatically initiated after {@code UI.init()} if a
     * navigator was created. If at a later point changes are made to the
     * navigator, {@code navigator.navigateTo(navigator.getState())} may need to
     * be explicitly called to ensure the current view matches the navigation
     * state.
     *
     * @param ui
     *            The UI to which this Navigator is attached.
     * @param container
     *            The SingleComponentContainer whose contents should be replaced
     *            with the active view on view change
     */
    public Navigator(UI ui, SingleComponentContainer container) {
        this(ui, new SingleComponentContainerViewDisplay(container));
    }

    /**
     * Creates a navigator that is tracking the active view using URI fragments
     * of the {@link Page} containing the given UI.
     * <p>
     * Navigation is automatically initiated after {@code UI.init()} if a
     * navigator was created. If at a later point changes are made to the
     * navigator, {@code navigator.navigateTo(navigator.getState())} may need to
     * be explicitly called to ensure the current view matches the navigation
     * state.
     *
     * @param ui
     *            The UI to which this Navigator is attached.
     * @param display
     *            The ViewDisplay used to display the views.
     */
    public Navigator(UI ui, ViewDisplay display) {
        this(ui, new UriFragmentManager(ui.getPage()), display);
    }

    /**
     * Creates a navigator.
     * <p>
     * When a custom navigation state manager is not needed, use one of the
     * other constructors which use a URI fragment based state manager.
     * <p>
     * Navigation is automatically initiated after {@code UI.init()} if a
     * navigator was created. If at a later point changes are made to the
     * navigator, {@code navigator.navigateTo(navigator.getState())} may need to
     * be explicitly called to ensure the current view matches the navigation
     * state.
     *
     * @param ui
     *            The UI to which this Navigator is attached.
     * @param stateManager
     *            The NavigationStateManager keeping track of the active view
     *            and enabling bookmarking and direct navigation or null to use
     *            the default implementation
     * @param display
     *            The ViewDisplay used to display the views handled by this
     *            navigator
     */
    public Navigator(UI ui, NavigationStateManager stateManager,
            ViewDisplay display) {
        init(ui, stateManager, display);
    }

    /**
     * Creates a navigator. This method is for use by dependency injection
     * frameworks etc. and must be followed by a call to
     * {@link #init(UI, NavigationStateManager, ViewDisplay)} before use.
     *
     * @since 7.6
     */
    protected Navigator() {
    }

    /**
     * Initializes a navigator created with the no arguments constructor.
     * <p>
     * When a custom navigation state manager is not needed, use null to create
     * a default one based on URI fragments.
     * <p>
     * Navigation is automatically initiated after {@code UI.init()} if a
     * navigator was created. If at a later point changes are made to the
     * navigator, {@code navigator.navigateTo(navigator.getState())} may need to
     * be explicitly called to ensure the current view matches the navigation
     * state.
     *
     * @since 7.6
     * @param ui
     *            The UI to which this Navigator is attached.
     * @param stateManager
     *            The NavigationStateManager keeping track of the active view
     *            and enabling bookmarking and direct navigation or null for
     *            default
     * @param display
     *            The ViewDisplay used to display the views handled by this
     *            navigator
     */
    protected void init(UI ui, NavigationStateManager stateManager,
            ViewDisplay display) {
        this.ui = ui;
        this.ui.setNavigator(this);
        if (stateManager == null) {
            stateManager = new UriFragmentManager(ui.getPage());
        }
        if (stateManager != null && this.stateManager != null
                && stateManager != this.stateManager) {
            this.stateManager.setNavigator(null);
        }
        this.stateManager = stateManager;
        this.stateManager.setNavigator(this);
        this.display = display;
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
     *
     * @throws IllegalArgumentException
     *             if <code>navigationState</code> does not map to a known view
     *             and no error view is registered
     */
    public void navigateTo(String navigationState) {
        ViewProvider longestViewNameProvider = getViewProvider(navigationState);
        String longestViewName = longestViewNameProvider == null ? null
                : longestViewNameProvider.getViewName(navigationState);
        View viewWithLongestName = null;

        if (longestViewName != null) {
            viewWithLongestName = longestViewNameProvider
                    .getView(longestViewName);
        }

        if (viewWithLongestName == null && errorProvider != null) {
            longestViewName = errorProvider.getViewName(navigationState);
            viewWithLongestName = errorProvider.getView(longestViewName);
        }

        if (viewWithLongestName == null) {
            throw new IllegalArgumentException(
                    "Trying to navigate to an unknown state '" + navigationState
                            + "' and an error view provider not present");
        }

        String parameters = "";
        if (navigationState.length() > longestViewName.length() + 1) {
            parameters = navigationState
                    .substring(longestViewName.length() + 1);
        } else if (navigationState.endsWith("/")) {
            navigationState = navigationState.substring(0,
                    navigationState.length() - 1);
        }
        if (getCurrentView() == null
                || !SharedUtil.equals(getCurrentView(), viewWithLongestName)
                || !SharedUtil.equals(currentNavigationState,
                        navigationState)) {
            navigateTo(viewWithLongestName, longestViewName, parameters);
        } else {
            updateNavigationState(new ViewChangeEvent(this, getCurrentView(),
                    viewWithLongestName, longestViewName, parameters));
        }
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
        boolean navigationAllowed = beforeViewChange(event);
        if (!navigationAllowed) {
            // #10901. Revert URL to previous state if back-button navigation
            // was canceled
            revertNavigation();
            return;
        }

        updateNavigationState(event);

        if (getDisplay() != null) {
            getDisplay().showView(view);
        }

        switchView(event);

        view.enter(event);

        fireAfterViewChange(event);
    }

    /**
     * Check whether view change is allowed by view change listeners (
     * {@link ViewChangeListener#beforeViewChange(ViewChangeEvent)}).
     *
     * This method can be overridden to extend the behavior, and should not be
     * called directly except by {@link #navigateTo(View, String, String)}.
     *
     * @since 7.6
     * @param event
     *            the event to fire as the before view change event
     * @return true if view change is allowed
     */
    protected boolean beforeViewChange(ViewChangeEvent event) {
        return fireBeforeViewChange(event);
    }

    /**
     * Revert the changes to the navigation state. When navigation fails, this
     * method can be called by {@link #navigateTo(View, String, String)} to
     * revert the URL fragment to point to the previous view to which navigation
     * succeeded.
     *
     * This method should only be called by
     * {@link #navigateTo(View, String, String)}. Normally it should not be
     * overridden, but can be by frameworks that need to hook into view change
     * cancellations of this type.
     *
     * @since 7.6
     */
    protected void revertNavigation() {
        if (currentNavigationState != null) {
            getStateManager().setState(currentNavigationState);
        }
    }

    /**
     * Update the internal state of the navigator (parameters, previous
     * successful URL fragment navigated to) when navigation succeeds.
     *
     * Normally this method should not be overridden nor called directly from
     * application code, but it can be called by a custom implementation of
     * {@link #navigateTo(View, String, String)}.
     *
     * @since 7.6
     * @param event
     *            a view change event with details of the change
     */
    protected void updateNavigationState(ViewChangeEvent event) {
        String viewName = event.getViewName();
        String parameters = event.getParameters();
        if (null != viewName && getStateManager() != null) {
            String navigationState = viewName;
            if (!parameters.isEmpty()) {
                navigationState += "/" + parameters;
            }
            if (!navigationState.equals(getStateManager().getState())) {
                getStateManager().setState(navigationState);
            }
            currentNavigationState = navigationState;
        }
    }

    /**
     * Update the internal state of the navigator to reflect the actual
     * switching of views.
     *
     * This method should only be called by
     * {@link #navigateTo(View, String, String)} between showing the view and
     * calling {@link View#enter(ViewChangeEvent)}. If this method is
     * overridden, the overriding version must call the super method.
     *
     * @since 7.6
     * @param event
     *            a view change event with details of the change
     */
    protected void switchView(ViewChangeEvent event) {
        currentView = event.getNewView();
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
        // a copy of the listener list is needed to avoid
        // ConcurrentModificationException as a listener can add/remove
        // listeners
        for (ViewChangeListener l : new ArrayList<ViewChangeListener>(
                listeners)) {
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
     * Returns the current navigation state reported by this Navigator's
     * {@link NavigationStateManager}.
     *
     * @return The navigation state.
     */
    public String getState() {
        return getStateManager().getState();
    }

    /**
     * Return the {@link ViewDisplay} used by the navigator.
     *
     * @return the ViewDisplay used for displaying views
     */
    public ViewDisplay getDisplay() {
        return display;
    }

    public UI getUI() {
        return ui;
    }

    /**
     * Return the currently active view.
     *
     * @since 7.6
     * @return current view
     */
    public View getCurrentView() {
        return currentView;
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
        // a copy of the listener list is needed to avoid
        // ConcurrentModificationException as a listener can add/remove
        // listeners
        for (ViewChangeListener l : new ArrayList<ViewChangeListener>(
                listeners)) {
            l.afterViewChange(event);
        }
    }

    /**
     * Registers a static, pre-initialized view instance for a view name.
     * <p>
     * Registering another view with a name that is already registered
     * overwrites the old registration of the same type.
     * <p>
     * Note that a view should not be shared between UIs (for instance, it
     * should not be a static field in a UI subclass).
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
     * Registers a view class for a view name.
     * <p>
     * Registering another view with a name that is already registered
     * overwrites the old registration of the same type.
     * <p>
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
     *            provider to register, not <code>null</code>
     * @throws IllegalArgumentException
     *             if the provided view provider is <code>null</code>
     */
    public void addProvider(ViewProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException(
                    "Cannot add a null view provider");
        }
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
     * Registers a view class that is instantiated when no other view matches
     * the navigation state. This implicitly sets an appropriate error view
     * provider and overrides any previous
     * {@link #setErrorProvider(ViewProvider)} call.
     * <p>
     * Note that an error view should not be shared between UIs (for instance,
     * it should not be a static field in a UI subclass).
     *
     * @param viewClass
     *            The View class whose instance should be used as the error
     *            view.
     */
    public void setErrorView(final Class<? extends View> viewClass) {
        setErrorProvider(new ViewProvider() {
            @Override
            public View getView(String viewName) {
                try {
                    return viewClass.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public String getViewName(String navigationState) {
                return navigationState;
            }
        });
    }

    /**
     * Registers a view that is displayed when no other view matches the
     * navigation state. This implicitly sets an appropriate error view provider
     * and overrides any previous {@link #setErrorProvider(ViewProvider)} call.
     *
     * @param view
     *            The View that should be used as the error view.
     */
    public void setErrorView(final View view) {
        setErrorProvider(new ViewProvider() {
            @Override
            public View getView(String viewName) {
                return view;
            }

            @Override
            public String getViewName(String navigationState) {
                return navigationState;
            }
        });
    }

    /**
     * Registers a view provider that is queried for a view when no other view
     * matches the navigation state. An error view provider should match any
     * navigation state, but could return different views for different states.
     * Its <code>getViewName(String navigationState)</code> should return
     * <code>navigationState</code>.
     *
     * @param provider
     */
    public void setErrorProvider(ViewProvider provider) {
        errorProvider = provider;
    }

    /**
     * Listen to changes of the active view.
     * <p>
     * Registered listeners are invoked in registration order before (
     * {@link ViewChangeListener#beforeViewChange(ViewChangeEvent)
     * beforeViewChange()}) and after (
     * {@link ViewChangeListener#afterViewChange(ViewChangeEvent)
     * afterViewChange()}) a view change occurs.
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

    /**
     * Get view provider that handles the given {@code state}.
     *
     * @param state
     *            state string
     * @return suitable provider
     */
    private ViewProvider getViewProvider(String state) {
        String longestViewName = null;
        ViewProvider longestViewNameProvider = null;
        for (ViewProvider provider : providers) {
            String viewName = provider.getViewName(state);
            if (null != viewName && (longestViewName == null
                    || viewName.length() > longestViewName.length())) {
                longestViewName = viewName;
                longestViewNameProvider = provider;
            }
        }
        return longestViewNameProvider;
    }

    /**
     * Creates view change event for given {@code view}, {@code viewName} and
     * {@code parameters}.
     *
     * @since 7.6.7
     * @return view change event
     */
    public void destroy() {
        stateManager.setNavigator(null);
        ui.setNavigator(null);
    }
}
