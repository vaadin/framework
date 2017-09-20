/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.client.communication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ApplicationConfiguration;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ApplicationConnection.ApplicationState;
import com.vaadin.client.ApplicationConnection.MultiStepDuration;
import com.vaadin.client.ApplicationConnection.ResponseHandlingStartedEvent;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.ConnectorMap;
import com.vaadin.client.FastStringSet;
import com.vaadin.client.HasComponentsConnector;
import com.vaadin.client.JsArrayObject;
import com.vaadin.client.LayoutManager;
import com.vaadin.client.LocaleService;
import com.vaadin.client.Paintable;
import com.vaadin.client.Profiler;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.UIDL;
import com.vaadin.client.Util;
import com.vaadin.client.VCaption;
import com.vaadin.client.VConsole;
import com.vaadin.client.ValueMap;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.metadata.ConnectorBundleLoader;
import com.vaadin.client.metadata.NoDataException;
import com.vaadin.client.metadata.Property;
import com.vaadin.client.metadata.Type;
import com.vaadin.client.metadata.TypeData;
import com.vaadin.client.ui.AbstractConnector;
import com.vaadin.client.ui.VNotification;
import com.vaadin.client.ui.dd.VDragAndDropManager;
import com.vaadin.client.ui.ui.UIConnector;
import com.vaadin.client.ui.window.WindowConnector;
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.shared.communication.MethodInvocation;
import com.vaadin.shared.communication.SharedState;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;

/**
 * A MessageHandler is responsible for handling all incoming messages (JSON)
 * from the server (state changes, RPCs and other updates) and ensuring that the
 * connectors are updated accordingly.
 *
 * @since 7.6
 * @author Vaadin Ltd
 */
public class MessageHandler {

    public static final String JSON_COMMUNICATION_PREFIX = "for(;;);[";
    public static final String JSON_COMMUNICATION_SUFFIX = "]";

    /**
     * Helper used to return two values when updating the connector hierarchy.
     */
    private static class ConnectorHierarchyUpdateResult {
        /**
         * Needed at a later point when the created events are fired
         */
        private JsArrayObject<ConnectorHierarchyChangeEvent> events = JavaScriptObject
                .createArray().cast();
        /**
         * Needed to know where captions might need to get updated
         */
        private FastStringSet parentChangedIds = FastStringSet.create();

        /**
         * Connectors for which the parent has been set to null
         */
        private FastStringSet detachedConnectorIds = FastStringSet.create();
    }

    /** The max timeout that response handling may be suspended */
    private static final int MAX_SUSPENDED_TIMEOUT = 5000;

    /**
     * The value of an undefined sync id.
     * <p>
     * This must be <code>-1</code>, because of the contract in
     * {@link #getLastSeenServerSyncId()}
     */
    private static final int UNDEFINED_SYNC_ID = -1;

    /**
     * If responseHandlingLocks contains any objects, response handling is
     * suspended until the collection is empty or a timeout has occurred.
     */
    private Set<Object> responseHandlingLocks = new HashSet<>();

    /**
     * Contains all UIDL messages received while response handling is suspended
     */
    private List<PendingUIDLMessage> pendingUIDLMessages = new ArrayList<>();

    // will hold the CSRF token once received
    private String csrfToken = ApplicationConstants.CSRF_TOKEN_DEFAULT_VALUE;

    // holds the push identifier once received
    private String pushId = null;

    /** Timer for automatic redirect to SessionExpiredURL */
    private Timer redirectTimer;

    /** redirectTimer scheduling interval in seconds */
    private int sessionExpirationInterval;

    /**
     * Holds the time spent rendering the last request
     */
    protected int lastProcessingTime;

    /**
     * Holds the total time spent rendering requests during the lifetime of the
     * session.
     */
    protected int totalProcessingTime;

    /**
     * Holds the time it took to load the page and render the first view. -2
     * means that this value has not yet been calculated because the first view
     * has not yet been rendered (or that your browser is very fast). -1 means
     * that the browser does not support the performance.timing feature used to
     * get this measurement.
     *
     * Note: also used for tracking whether the first UIDL has been handled
     */
    private int bootstrapTime = 0;

    /**
     * true if state updates are currently being done
     */
    private boolean updatingState = false;

    /**
     * Holds the timing information from the server-side. How much time was
     * spent servicing the last request and how much time has been spent
     * servicing the session so far. These values are always one request behind,
     * since they cannot be measured before the request is finished.
     */
    private ValueMap serverTimingInfo;

    /**
     * Holds the last seen response id given by the server.
     * <p>
     * The server generates a strictly increasing id for each response to each
     * request from the client. This ID is then replayed back to the server on
     * each request. This helps the server in knowing in what state the client
     * is, and compare it to its own state. In short, it helps with concurrent
     * changes between the client and server.
     * <p>
     * Initial value, i.e. no responses received from the server, is
     * {@link #UNDEFINED_SYNC_ID} ({@value #UNDEFINED_SYNC_ID}). This happens
     * between the bootstrap HTML being loaded and the first UI being rendered;
     */
    private int lastSeenServerSyncId = UNDEFINED_SYNC_ID;

    private ApplicationConnection connection;

    /**
     * Data structure holding information about pending UIDL messages.
     */
    private static class PendingUIDLMessage {
        private ValueMap json;

        public PendingUIDLMessage(ValueMap json) {
            this.json = json;
        }

        public ValueMap getJson() {
            return json;
        }
    }

    /**
     * Sets the application connection this instance is connected to. Called
     * internally by the framework.
     *
     * @param connection
     *            the application connection this instance is connected to
     */
    public void setConnection(ApplicationConnection connection) {
        this.connection = connection;
    }

    private static Logger getLogger() {
        return Logger.getLogger(MessageHandler.class.getName());
    }

    /**
     * Handles a received UIDL JSON text, parsing it, and passing it on to the
     * appropriate handlers, while logging timing information.
     *
     * @param jsonText
     *            The JSON to handle
     */
    public void handleMessage(final ValueMap json) {
        if (json == null) {
            throw new IllegalArgumentException(
                    "The json to handle cannot be null");
        }
        if (getServerId(json) == -1) {
            getLogger().severe("Response didn't contain a server id. "
                    + "Please verify that the server is up-to-date and that the response data has not been modified in transmission.");
        }

        if (connection.getApplicationState() == ApplicationState.RUNNING) {
            handleJSON(json);
        } else if (connection
                .getApplicationState() == ApplicationState.INITIALIZING) {
            // Application is starting up for the first time
            connection.setApplicationRunning(true);
            connection.executeWhenCSSLoaded(new Command() {
                @Override
                public void execute() {
                    handleJSON(json);
                }
            });
        } else {
            getLogger().warning(
                    "Ignored received message because application has already been stopped");
            return;
        }
    }

    protected void handleJSON(final ValueMap json) {
        final int serverId = getServerId(json);

        if (isResynchronize(json) && !isNextExpectedMessage(serverId)) {
            // Resynchronize request. We must remove any old pending
            // messages and ensure this is handled next. Otherwise we
            // would keep waiting for an older message forever (if this
            // is triggered by forceHandleMessage)
            getLogger().info("Received resync message with id " + serverId
                    + " while waiting for " + getExpectedServerId());
            lastSeenServerSyncId = serverId - 1;
            removeOldPendingMessages();
        }

        boolean locked = !responseHandlingLocks.isEmpty();

        if (locked || !isNextExpectedMessage(serverId)) {
            // Cannot or should not handle this message right now, either
            // because of locks or because it's an out-of-order message

            if (locked) {
                // Some component is doing something that can't be interrupted
                // (e.g. animation that should be smooth). Enqueue the UIDL
                // message for later processing.
                getLogger().info("Postponing UIDL handling due to lock...");
            } else {
                // Unexpected server id
                if (serverId <= lastSeenServerSyncId) {
                    // Why is the server re-sending an old package? Ignore it
                    getLogger().warning("Received message with server id "
                            + serverId + " but have already seen "
                            + lastSeenServerSyncId + ". Ignoring it");
                    endRequestIfResponse(json);
                    return;
                }

                // We are waiting for an earlier message...
                getLogger().info("Received message with server id " + serverId
                        + " but expected " + getExpectedServerId()
                        + ". Postponing handling until the missing message(s) have been received");
            }
            pendingUIDLMessages.add(new PendingUIDLMessage(json));
            if (!forceHandleMessage.isRunning()) {
                forceHandleMessage.schedule(MAX_SUSPENDED_TIMEOUT);
            }
            return;
        }

        final Date start = new Date();
        /*
         * Lock response handling to avoid a situation where something pushed
         * from the server gets processed while waiting for e.g. lazily loaded
         * connectors that are needed for processing the current message.
         */
        final Object lock = new Object();
        suspendReponseHandling(lock);

        getLogger().info("Handling message from server");
        connection.fireEvent(new ResponseHandlingStartedEvent(connection));

        // Client id must be updated before server id, as server id update can
        // cause a resync (which must use the updated id)
        if (json.containsKey(ApplicationConstants.CLIENT_TO_SERVER_ID)) {
            int serverNextExpected = json
                    .getInt(ApplicationConstants.CLIENT_TO_SERVER_ID);
            getMessageSender().setClientToServerMessageId(serverNextExpected,
                    isResynchronize(json));
        }

        if (serverId != -1) {
            /*
             * Use sync id unless explicitly set as undefined, as is done by
             * e.g. critical server-side notifications
             */
            lastSeenServerSyncId = serverId;
        }

        // Handle redirect
        if (json.containsKey("redirect")) {
            String url = json.getValueMap("redirect").getString("url");
            getLogger().info("redirecting to " + url);
            WidgetUtil.redirect(url);
            return;
        }

        final MultiStepDuration handleUIDLDuration = new MultiStepDuration();

        // Get security key
        if (json.containsKey(ApplicationConstants.UIDL_SECURITY_TOKEN_ID)) {
            csrfToken = json
                    .getString(ApplicationConstants.UIDL_SECURITY_TOKEN_ID);
        }

        // Get push id if present
        if (json.containsKey(ApplicationConstants.UIDL_PUSH_ID)) {
            pushId = json.getString(ApplicationConstants.UIDL_PUSH_ID);
        }

        getLogger().info(" * Handling resources from server");

        if (json.containsKey("resources")) {
            ValueMap resources = json.getValueMap("resources");
            JsArrayString keyArray = resources.getKeyArray();
            int l = keyArray.length();
            for (int i = 0; i < l; i++) {
                String key = keyArray.get(i);
                connection.setResource(key, resources.getAsString(key));
            }
        }
        handleUIDLDuration
                .logDuration(" * Handling resources from server completed", 10);

        getLogger().info(" * Handling type inheritance map from server");

        if (json.containsKey("typeInheritanceMap")) {
            connection.getConfiguration().addComponentInheritanceInfo(
                    json.getValueMap("typeInheritanceMap"));
        }
        handleUIDLDuration.logDuration(
                " * Handling type inheritance map from server completed", 10);

        getLogger().info("Handling type mappings from server");

        if (json.containsKey("typeMappings")) {
            connection.getConfiguration().addComponentMappings(
                    json.getValueMap("typeMappings"),
                    connection.getWidgetSet());

        }

        getLogger().info("Handling resource dependencies");
        connection.getDependencyLoader().loadDependencies(json);

        handleUIDLDuration.logDuration(
                " * Handling type mappings from server completed", 10);
        /*
         * Hook for e.g. TestBench to get details about server peformance
         */
        if (json.containsKey("timings")) {
            serverTimingInfo = json.getValueMap("timings");
        }

        Command c = new Command() {
            private boolean onlyNoLayoutUpdates = true;

            @Override
            public void execute() {
                assert serverId == -1 || serverId == lastSeenServerSyncId;

                handleUIDLDuration.logDuration(" * Loading widgets completed",
                        10);

                Profiler.enter("Handling meta information");
                ValueMap meta = null;
                if (json.containsKey("meta")) {
                    getLogger().info(" * Handling meta information");
                    meta = json.getValueMap("meta");
                    if (meta.containsKey("repaintAll")) {
                        prepareRepaintAll();
                    }
                    if (meta.containsKey("timedRedirect")) {
                        final ValueMap timedRedirect = meta
                                .getValueMap("timedRedirect");
                        if (redirectTimer != null) {
                            redirectTimer.cancel();
                        }
                        redirectTimer = new Timer() {
                            @Override
                            public void run() {
                                WidgetUtil.redirect(
                                        timedRedirect.getString("url"));
                            }
                        };
                        sessionExpirationInterval = timedRedirect
                                .getInt("interval");
                    }
                }
                Profiler.leave("Handling meta information");

                if (redirectTimer != null) {
                    redirectTimer.schedule(1000 * sessionExpirationInterval);
                }

                updatingState = true;

                double processUidlStart = Duration.currentTimeMillis();

                // Ensure that all connectors that we are about to update exist
                JsArrayString createdConnectorIds = createConnectorsIfNeeded(
                        json);

                // Update states, do not fire events
                JsArrayObject<StateChangeEvent> pendingStateChangeEvents = updateConnectorState(
                        json, createdConnectorIds);

                /*
                 * Doing this here so that locales are available also to the
                 * connectors which get a state change event before the UI.
                 */
                Profiler.enter("Handling locales");
                getLogger().info(" * Handling locales");
                // Store locale data
                LocaleService.addLocales(getUIConnector()
                        .getState().localeServiceState.localeData);
                Profiler.leave("Handling locales");

                // Update hierarchy, do not fire events
                ConnectorHierarchyUpdateResult connectorHierarchyUpdateResult = updateConnectorHierarchy(
                        json);

                // Fire hierarchy change events
                sendHierarchyChangeEvents(
                        connectorHierarchyUpdateResult.events);

                updateCaptions(pendingStateChangeEvents,
                        connectorHierarchyUpdateResult.parentChangedIds);

                delegateToWidget(pendingStateChangeEvents);

                // Fire state change events.
                sendStateChangeEvents(pendingStateChangeEvents);

                // Update of legacy (UIDL) style connectors
                updateVaadin6StyleConnectors(json);

                // Handle any RPC invocations done on the server side
                handleRpcInvocations(json);

                if (json.containsKey("dd")) {
                    // response contains data for drag and drop service
                    VDragAndDropManager.get()
                            .handleServerResponse(json.getValueMap("dd"));
                }

                unregisterRemovedConnectors(
                        connectorHierarchyUpdateResult.detachedConnectorIds);

                getLogger().info("handleUIDLMessage: "
                        + (Duration.currentTimeMillis() - processUidlStart)
                        + " ms");

                updatingState = false;

                Profiler.enter("Layout processing");
                try {
                    LayoutManager layoutManager = getLayoutManager();
                    if (!onlyNoLayoutUpdates) {
                        layoutManager.setEverythingNeedsMeasure();
                    }
                    if (layoutManager.isLayoutNeeded()) {
                        layoutManager.layoutNow();
                    }
                } catch (final Throwable e) {
                    getLogger().log(Level.SEVERE, "Error processing layouts",
                            e);
                }
                Profiler.leave("Layout processing");

                if (ApplicationConfiguration.isDebugMode()) {
                    Profiler.enter("Dumping state changes to the console");
                    getLogger().info(" * Dumping state changes to the console");
                    VConsole.dirUIDL(json, connection);
                    Profiler.leave("Dumping state changes to the console");
                }

                if (meta != null) {
                    Profiler.enter("Error handling");
                    if (meta.containsKey("appError")) {
                        ValueMap error = meta.getValueMap("appError");

                        VNotification.showError(connection,
                                error.getString("caption"),
                                error.getString("message"),
                                error.getString("details"),
                                error.getString("url"));

                        connection.setApplicationRunning(false);
                    }
                    Profiler.leave("Error handling");
                }

                // TODO build profiling for widget impl loading time

                lastProcessingTime = (int) ((new Date().getTime())
                        - start.getTime());
                totalProcessingTime += lastProcessingTime;
                if (bootstrapTime == 0) {

                    double fetchStart = getFetchStartTime();
                    if (fetchStart != 0) {
                        int time = (int) (Duration.currentTimeMillis()
                                - fetchStart);
                        getLogger().log(Level.INFO, "First response processed "
                                + time + " ms after fetchStart");
                    }

                    bootstrapTime = calculateBootstrapTime();
                    if (Profiler.isEnabled() && bootstrapTime != -1) {
                        Profiler.logBootstrapTimings();
                    }
                }

                getLogger().info(" Processing time was "
                        + String.valueOf(lastProcessingTime) + "ms");
                getLogger().info(
                        "Referenced paintables: " + getConnectorMap().size());

                endRequestIfResponse(json);
                resumeResponseHandling(lock);

                ConnectorBundleLoader.get().ensureDeferredBundleLoaded();

                if (Profiler.isEnabled()) {
                    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                        @Override
                        public void execute() {
                            Profiler.logTimings();
                            Profiler.reset();
                        }
                    });
                }
            }

            /**
             * Properly clean up any old stuff to ensure everything is properly
             * reinitialized.
             */
            private void prepareRepaintAll() {
                String uiConnectorId = getUIConnector().getConnectorId();
                if (uiConnectorId == null) {
                    // Nothing to clear yet
                    return;
                }

                // Create fake server response that says that the uiConnector
                // has no children
                JsonObject fakeHierarchy = Json.createObject();
                fakeHierarchy.put(uiConnectorId, Json.createArray());
                JsonObject fakeJson = Json.createObject();
                fakeJson.put("hierarchy", fakeHierarchy);
                ValueMap fakeValueMap = ((JavaScriptObject) fakeJson.toNative())
                        .cast();

                // Update hierarchy based on the fake response
                ConnectorHierarchyUpdateResult connectorHierarchyUpdateResult = updateConnectorHierarchy(
                        fakeValueMap);

                // Send hierarchy events based on the fake update
                sendHierarchyChangeEvents(
                        connectorHierarchyUpdateResult.events);

                // Unregister all the old connectors that have now been removed
                unregisterRemovedConnectors(
                        connectorHierarchyUpdateResult.detachedConnectorIds);
            }

            private void updateCaptions(
                    JsArrayObject<StateChangeEvent> pendingStateChangeEvents,
                    FastStringSet parentChangedIds) {
                Profiler.enter("updateCaptions");

                /*
                 * Find all components that might need a caption update based on
                 * pending state and hierarchy changes
                 */
                FastStringSet needsCaptionUpdate = FastStringSet.create();
                needsCaptionUpdate.addAll(parentChangedIds);

                // Find components with potentially changed caption state
                int size = pendingStateChangeEvents.size();
                for (int i = 0; i < size; i++) {
                    StateChangeEvent event = pendingStateChangeEvents.get(i);
                    if (VCaption.mightChange(event)) {
                        ServerConnector connector = event.getConnector();
                        needsCaptionUpdate.add(connector.getConnectorId());
                    }
                }

                ConnectorMap connectorMap = getConnectorMap();

                // Update captions for all suitable candidates
                JsArrayString dump = needsCaptionUpdate.dump();
                int needsUpdateLength = dump.length();
                for (int i = 0; i < needsUpdateLength; i++) {
                    String childId = dump.get(i);
                    ServerConnector child = connectorMap.getConnector(childId);

                    if (child instanceof ComponentConnector
                            && ((ComponentConnector) child)
                                    .delegateCaptionHandling()) {
                        ServerConnector parent = child.getParent();
                        if (parent instanceof HasComponentsConnector) {
                            Profiler.enter(
                                    "HasComponentsConnector.updateCaption");
                            ((HasComponentsConnector) parent)
                                    .updateCaption((ComponentConnector) child);
                            Profiler.leave(
                                    "HasComponentsConnector.updateCaption");
                        }
                    }
                }

                Profiler.leave("updateCaptions");
            }

            private void delegateToWidget(
                    JsArrayObject<StateChangeEvent> pendingStateChangeEvents) {
                Profiler.enter("@DelegateToWidget");

                getLogger().info(" * Running @DelegateToWidget");

                // Keep track of types that have no @DelegateToWidget in their
                // state to optimize performance
                FastStringSet noOpTypes = FastStringSet.create();

                int size = pendingStateChangeEvents.size();
                for (int eventIndex = 0; eventIndex < size; eventIndex++) {
                    StateChangeEvent sce = pendingStateChangeEvents
                            .get(eventIndex);
                    ServerConnector connector = sce.getConnector();
                    if (connector instanceof ComponentConnector) {
                        String className = connector.getClass().getName();
                        if (noOpTypes.contains(className)) {
                            continue;
                        }
                        ComponentConnector component = (ComponentConnector) connector;

                        Type stateType = AbstractConnector
                                .getStateType(component);
                        JsArrayString delegateToWidgetProperties = stateType
                                .getDelegateToWidgetProperties();
                        if (delegateToWidgetProperties == null) {
                            noOpTypes.add(className);
                            continue;
                        }

                        int length = delegateToWidgetProperties.length();
                        for (int i = 0; i < length; i++) {
                            String propertyName = delegateToWidgetProperties
                                    .get(i);
                            if (sce.hasPropertyChanged(propertyName)) {
                                Property property = stateType
                                        .getProperty(propertyName);
                                String method = property
                                        .getDelegateToWidgetMethodName();
                                Profiler.enter("doDelegateToWidget");
                                doDelegateToWidget(component, property, method);
                                Profiler.leave("doDelegateToWidget");
                            }
                        }

                    }
                }

                Profiler.leave("@DelegateToWidget");
            }

            private void doDelegateToWidget(ComponentConnector component,
                    Property property, String methodName) {
                Type type = TypeData.getType(component.getClass());
                try {
                    Type widgetType = type.getMethod("getWidget")
                            .getReturnType();
                    Widget widget = component.getWidget();

                    Object propertyValue = property
                            .getValue(component.getState());

                    widgetType.getMethod(methodName).invoke(widget,
                            propertyValue);
                } catch (NoDataException e) {
                    throw new RuntimeException(
                            "Missing data needed to invoke @DelegateToWidget for "
                                    + component.getClass().getSimpleName(),
                            e);
                }
            }

            /**
             * Sends the state change events created while updating the state
             * information.
             *
             * This must be called after hierarchy change listeners have been
             * called. At least caption updates for the parent are strange if
             * fired from state change listeners and thus calls the parent
             * BEFORE the parent is aware of the child (through a
             * ConnectorHierarchyChangedEvent)
             *
             * @param pendingStateChangeEvents
             *            The events to send
             */
            private void sendStateChangeEvents(
                    JsArrayObject<StateChangeEvent> pendingStateChangeEvents) {
                Profiler.enter("sendStateChangeEvents");
                getLogger().info(" * Sending state change events");

                int size = pendingStateChangeEvents.size();
                for (int i = 0; i < size; i++) {
                    StateChangeEvent sce = pendingStateChangeEvents.get(i);
                    try {
                        sce.getConnector().fireEvent(sce);
                    } catch (final Throwable e) {
                        getLogger().log(Level.SEVERE,
                                "Error sending state change events", e);
                    }
                }

                Profiler.leave("sendStateChangeEvents");
            }

            private void verifyConnectorHierarchy() {
                Profiler.enter(
                        "verifyConnectorHierarchy - this is only performed in debug mode");

                JsArrayObject<ServerConnector> currentConnectors = getConnectorMap()
                        .getConnectorsAsJsArray();
                int size = currentConnectors.size();
                for (int i = 0; i < size; i++) {
                    ServerConnector c = currentConnectors.get(i);
                    if (c.getParent() != null) {
                        if (!c.getParent().getChildren().contains(c)) {
                            getLogger().severe("ERROR: Connector "
                                    + c.getConnectorId()
                                    + " is connected to a parent but the parent ("
                                    + c.getParent().getConnectorId()
                                    + ") does not contain the connector");
                        }
                    } else if (c == getUIConnector()) {
                        // UIConnector for this connection, ignore
                    } else if (c instanceof WindowConnector && getUIConnector()
                            .hasSubWindow((WindowConnector) c)) {
                        // Sub window attached to this UIConnector, ignore
                    } else {
                        // The connector has been detached from the
                        // hierarchy but was not unregistered.
                        getLogger().severe("ERROR: Connector "
                                + c.getConnectorId()
                                + " is not attached to a parent but has not been unregistered");
                    }

                }

                Profiler.leave(
                        "verifyConnectorHierarchy - this is only performed in debug mode");
            }

            private void unregisterRemovedConnectors(
                    FastStringSet detachedConnectors) {
                Profiler.enter("unregisterRemovedConnectors");

                JsArrayString detachedArray = detachedConnectors.dump();
                for (int i = 0; i < detachedArray.length(); i++) {
                    ServerConnector connector = getConnectorMap()
                            .getConnector(detachedArray.get(i));

                    Profiler.enter(
                            "unregisterRemovedConnectors unregisterConnector");
                    getConnectorMap().unregisterConnector(connector);
                    Profiler.leave(
                            "unregisterRemovedConnectors unregisterConnector");
                }

                if (ApplicationConfiguration.isDebugMode()) {
                    // Do some extra checking if we're in debug mode (i.e. debug
                    // window is open)
                    verifyConnectorHierarchy();
                }

                getLogger().info("* Unregistered " + detachedArray.length()
                        + " connectors");
                Profiler.leave("unregisterRemovedConnectors");
            }

            private JsArrayString createConnectorsIfNeeded(ValueMap json) {
                getLogger().info(" * Creating connectors (if needed)");

                JsArrayString createdConnectors = JavaScriptObject.createArray()
                        .cast();
                if (!json.containsKey("types")) {
                    return createdConnectors;
                }

                Profiler.enter("Creating connectors");

                ValueMap types = json.getValueMap("types");
                JsArrayString keyArray = types.getKeyArray();
                for (int i = 0; i < keyArray.length(); i++) {
                    try {
                        String connectorId = keyArray.get(i);
                        ServerConnector connector = getConnectorMap()
                                .getConnector(connectorId);
                        if (connector != null) {
                            continue;
                        }

                        // Always do layouts if there's at least one new
                        // connector
                        onlyNoLayoutUpdates = false;

                        int connectorType = Integer
                                .parseInt(types.getString(connectorId));

                        Class<? extends ServerConnector> connectorClass = connection
                                .getConfiguration()
                                .getConnectorClassByEncodedTag(connectorType);

                        // Connector does not exist so we must create it
                        if (connectorClass != getUIConnector().getClass()) {
                            // create, initialize and register the paintable
                            Profiler.enter(
                                    "ApplicationConnection.getConnector");
                            connector = connection.getConnector(connectorId,
                                    connectorType);
                            Profiler.leave(
                                    "ApplicationConnection.getConnector");

                            createdConnectors.push(connectorId);
                        } else {
                            // First UIConnector update. Before this the
                            // UIConnector has been created but not
                            // initialized as the connector id has not been
                            // known
                            getConnectorMap().registerConnector(connectorId,
                                    getUIConnector());
                            getUIConnector().doInit(connectorId, connection);
                            createdConnectors.push(connectorId);
                        }
                    } catch (final Throwable e) {
                        getLogger().log(Level.SEVERE,
                                "Error handling type data", e);
                    }
                }

                Profiler.leave("Creating connectors");

                return createdConnectors;
            }

            private void updateVaadin6StyleConnectors(ValueMap json) {
                Profiler.enter("updateVaadin6StyleConnectors");

                JsArray<ValueMap> changes = json.getJSValueMapArray("changes");
                int length = changes.length();

                // Must always do layout if there's even a single legacy update
                if (length != 0) {
                    onlyNoLayoutUpdates = false;
                }

                getLogger()
                        .info(" * Passing UIDL to Vaadin 6 style connectors");
                // update paintables
                for (int i = 0; i < length; i++) {
                    try {
                        final UIDL change = changes.get(i).cast();
                        final UIDL uidl = change.getChildUIDL(0);
                        String connectorId = uidl.getId();

                        final ComponentConnector legacyConnector = (ComponentConnector) getConnectorMap()
                                .getConnector(connectorId);
                        if (legacyConnector instanceof Paintable) {
                            String key = null;
                            if (Profiler.isEnabled()) {
                                key = "updateFromUIDL for " + legacyConnector
                                        .getClass().getSimpleName();
                                Profiler.enter(key);
                            }

                            ((Paintable) legacyConnector).updateFromUIDL(uidl,
                                    connection);

                            if (Profiler.isEnabled()) {
                                Profiler.leave(key);
                            }
                        } else if (legacyConnector == null) {
                            getLogger().severe(
                                    "Received update for " + uidl.getTag()
                                            + ", but there is no such paintable ("
                                            + connectorId + ") rendered.");
                        } else {
                            getLogger()
                                    .severe("Server sent Vaadin 6 style updates for "
                                            + Util.getConnectorString(
                                                    legacyConnector)
                                            + " but this is not a Vaadin 6 Paintable");
                        }

                    } catch (final Throwable e) {
                        getLogger().log(Level.SEVERE, "Error handling UIDL", e);
                    }
                }

                Profiler.leave("updateVaadin6StyleConnectors");
            }

            private void sendHierarchyChangeEvents(
                    JsArrayObject<ConnectorHierarchyChangeEvent> events) {
                int eventCount = events.size();
                if (eventCount == 0) {
                    return;
                }
                Profiler.enter("sendHierarchyChangeEvents");

                getLogger().info(" * Sending hierarchy change events");
                for (int i = 0; i < eventCount; i++) {
                    ConnectorHierarchyChangeEvent event = events.get(i);
                    try {
                        logHierarchyChange(event);
                        event.getConnector().fireEvent(event);
                    } catch (final Throwable e) {
                        getLogger().log(Level.SEVERE,
                                "Error sending hierarchy change events", e);
                    }
                }

                Profiler.leave("sendHierarchyChangeEvents");
            }

            private void logHierarchyChange(
                    ConnectorHierarchyChangeEvent event) {
                if (true) {
                    // Always disabled for now. Can be enabled manually
                    return;
                }

                getLogger().info("Hierarchy changed for "
                        + Util.getConnectorString(event.getConnector()));
                String oldChildren = "* Old children: ";
                for (ComponentConnector child : event.getOldChildren()) {
                    oldChildren += Util.getConnectorString(child) + " ";
                }
                getLogger().info(oldChildren);

                String newChildren = "* New children: ";
                HasComponentsConnector parent = (HasComponentsConnector) event
                        .getConnector();
                for (ComponentConnector child : parent.getChildComponents()) {
                    newChildren += Util.getConnectorString(child) + " ";
                }
                getLogger().info(newChildren);
            }

            private JsArrayObject<StateChangeEvent> updateConnectorState(
                    ValueMap json, JsArrayString createdConnectorIds) {
                JsArrayObject<StateChangeEvent> events = JavaScriptObject
                        .createArray().cast();
                getLogger().info(" * Updating connector states");
                if (!json.containsKey("state")) {
                    return events;
                }

                Profiler.enter("updateConnectorState");

                FastStringSet remainingNewConnectors = FastStringSet.create();
                remainingNewConnectors.addAll(createdConnectorIds);

                // set states for all paintables mentioned in "state"
                ValueMap states = json.getValueMap("state");
                JsArrayString keyArray = states.getKeyArray();
                for (int i = 0; i < keyArray.length(); i++) {
                    try {
                        String connectorId = keyArray.get(i);
                        ServerConnector connector = getConnectorMap()
                                .getConnector(connectorId);
                        if (null != connector) {
                            Profiler.enter("updateConnectorState inner loop");
                            if (Profiler.isEnabled()) {
                                Profiler.enter("Decode connector state "
                                        + connector.getClass().getSimpleName());
                            }

                            JavaScriptObject jso = states
                                    .getJavaScriptObject(connectorId);
                            JsonObject stateJson = Util.jso2json(jso);

                            if (connector instanceof HasJavaScriptConnectorHelper) {
                                ((HasJavaScriptConnectorHelper) connector)
                                        .getJavascriptConnectorHelper()
                                        .setNativeState(jso);
                            }

                            SharedState state = connector.getState();
                            Type stateType = new Type(
                                    state.getClass().getName(), null);

                            if (onlyNoLayoutUpdates) {
                                Profiler.enter(
                                        "updateConnectorState @NoLayout handling");
                                for (String propertyName : stateJson.keys()) {
                                    Property property = stateType
                                            .getProperty(propertyName);
                                    if (!property.isNoLayout()) {
                                        onlyNoLayoutUpdates = false;
                                        break;
                                    }
                                }
                                Profiler.leave(
                                        "updateConnectorState @NoLayout handling");
                            }

                            Profiler.enter("updateConnectorState decodeValue");
                            JsonDecoder.decodeValue(stateType, stateJson, state,
                                    connection);
                            Profiler.leave("updateConnectorState decodeValue");

                            if (Profiler.isEnabled()) {
                                Profiler.leave("Decode connector state "
                                        + connector.getClass().getSimpleName());
                            }

                            Profiler.enter("updateConnectorState create event");

                            boolean isNewConnector = remainingNewConnectors
                                    .contains(connectorId);
                            if (isNewConnector) {
                                remainingNewConnectors.remove(connectorId);
                            }

                            StateChangeEvent event = new StateChangeEvent(
                                    connector, stateJson, isNewConnector);
                            events.add(event);
                            Profiler.leave("updateConnectorState create event");

                            Profiler.leave("updateConnectorState inner loop");
                        }
                    } catch (final Throwable e) {
                        getLogger().log(Level.SEVERE,
                                "Error updating connector states", e);
                    }
                }

                Profiler.enter("updateConnectorState newWithoutState");
                // Fire events for properties using the default value for newly
                // created connectors even if there were no state changes
                JsArrayString dump = remainingNewConnectors.dump();
                int length = dump.length();
                for (int i = 0; i < length; i++) {
                    String connectorId = dump.get(i);
                    ServerConnector connector = getConnectorMap()
                            .getConnector(connectorId);

                    StateChangeEvent event = new StateChangeEvent(connector,
                            Json.createObject(), true);

                    events.add(event);

                }
                Profiler.leave("updateConnectorState newWithoutState");

                Profiler.leave("updateConnectorState");

                return events;
            }

            /**
             * Updates the connector hierarchy and returns a list of events that
             * should be fired after update of the hierarchy and the state is
             * done.
             *
             * @param json
             *            The JSON containing the hierarchy information
             * @return A collection of events that should be fired when update
             *         of hierarchy and state is complete and a list of all
             *         connectors for which the parent has changed
             */
            private ConnectorHierarchyUpdateResult updateConnectorHierarchy(
                    ValueMap json) {
                ConnectorHierarchyUpdateResult result = new ConnectorHierarchyUpdateResult();

                getLogger().info(" * Updating connector hierarchy");

                Profiler.enter("updateConnectorHierarchy");

                FastStringSet maybeDetached = FastStringSet.create();
                FastStringSet hasHierarchy = FastStringSet.create();

                // Process regular hierarchy data
                if (json.containsKey("hierarchy")) {
                    ValueMap hierarchies = json.getValueMap("hierarchy");
                    JsArrayString hierarchyKeys = hierarchies.getKeyArray();
                    for (int i = 0; i < hierarchyKeys.length(); i++) {
                        String connectorId = hierarchyKeys.get(i);
                        JsArrayString childConnectorIds = hierarchies
                                .getJSStringArray(connectorId);
                        hasHierarchy.add(connectorId);

                        updateConnectorHierarchy(connectorId, childConnectorIds,
                                maybeDetached, result);
                    }
                }

                // Assume empty hierarchy for connectors with state updates but
                // no hierarchy data
                if (json.containsKey("state")) {
                    JsArrayString stateKeys = json.getValueMap("state")
                            .getKeyArray();

                    JsArrayString emptyArray = JavaScriptObject.createArray()
                            .cast();

                    for (int i = 0; i < stateKeys.length(); i++) {
                        String connectorId = stateKeys.get(i);
                        if (!hasHierarchy.contains(connectorId)) {
                            updateConnectorHierarchy(connectorId, emptyArray,
                                    maybeDetached, result);
                        }
                    }
                }

                Profiler.enter(
                        "updateConnectorHierarchy detach removed connectors");

                /*
                 * Connector is in maybeDetached at this point if it has been
                 * removed from its parent but not added to any other parent
                 */
                JsArrayString maybeDetachedArray = maybeDetached.dump();
                for (int i = 0; i < maybeDetachedArray.length(); i++) {
                    ServerConnector removed = getConnectorMap()
                            .getConnector(maybeDetachedArray.get(i));
                    recursivelyDetach(removed, result.events,
                            result.detachedConnectorIds);
                }

                Profiler.leave(
                        "updateConnectorHierarchy detach removed connectors");

                if (result.events.size() != 0) {
                    onlyNoLayoutUpdates = false;
                }

                Profiler.leave("updateConnectorHierarchy");

                return result;

            }

            /**
             * Updates the hierarchy for a connector
             *
             * @param connectorId
             *            the id of the connector to update
             * @param childConnectorIds
             *            array of child connector ids
             * @param maybeDetached
             *            set of connectors that are maybe detached
             * @param result
             *            the hierarchy update result
             */
            private void updateConnectorHierarchy(String connectorId,
                    JsArrayString childConnectorIds,
                    FastStringSet maybeDetached,
                    ConnectorHierarchyUpdateResult result) {
                try {
                    Profiler.enter("updateConnectorHierarchy hierarchy entry");

                    ConnectorMap connectorMap = getConnectorMap();

                    ServerConnector parentConnector = connectorMap
                            .getConnector(connectorId);
                    int childConnectorSize = childConnectorIds.length();

                    Profiler.enter(
                            "updateConnectorHierarchy find new connectors");

                    List<ServerConnector> newChildren = new ArrayList<>();
                    List<ComponentConnector> newComponents = new ArrayList<>();
                    for (int connectorIndex = 0; connectorIndex < childConnectorSize; connectorIndex++) {
                        String childConnectorId = childConnectorIds
                                .get(connectorIndex);
                        ServerConnector childConnector = connectorMap
                                .getConnector(childConnectorId);
                        if (childConnector == null) {
                            getLogger().severe("Hierarchy claims that "
                                    + childConnectorId + " is a child for "
                                    + connectorId + " ("
                                    + parentConnector.getClass().getName()
                                    + ") but no connector with id "
                                    + childConnectorId
                                    + " has been registered. "
                                    + "More information might be available in the server-side log if assertions are enabled");
                            continue;
                        }
                        newChildren.add(childConnector);
                        if (childConnector instanceof ComponentConnector) {
                            newComponents
                                    .add((ComponentConnector) childConnector);
                        } else if (!(childConnector instanceof AbstractExtensionConnector)) {
                            throw new IllegalStateException(Util
                                    .getConnectorString(childConnector)
                                    + " is not a ComponentConnector nor an AbstractExtensionConnector");
                        }
                        if (childConnector.getParent() != parentConnector) {
                            childConnector.setParent(parentConnector);
                            result.parentChangedIds.add(childConnectorId);
                            // Not detached even if previously removed from
                            // parent
                            maybeDetached.remove(childConnectorId);
                        }
                    }

                    Profiler.leave(
                            "updateConnectorHierarchy find new connectors");

                    // TODO This check should be done on the server side in
                    // the future so the hierarchy update is only sent when
                    // something actually has changed
                    List<ServerConnector> oldChildren = parentConnector
                            .getChildren();
                    boolean actuallyChanged = !Util
                            .collectionsEquals(oldChildren, newChildren);

                    if (!actuallyChanged) {
                        return;
                    }

                    Profiler.enter(
                            "updateConnectorHierarchy handle HasComponentsConnector");

                    if (parentConnector instanceof HasComponentsConnector) {
                        HasComponentsConnector ccc = (HasComponentsConnector) parentConnector;
                        List<ComponentConnector> oldComponents = ccc
                                .getChildComponents();
                        if (!Util.collectionsEquals(oldComponents,
                                newComponents)) {
                            // Fire change event if the hierarchy has
                            // changed
                            ConnectorHierarchyChangeEvent event = GWT.create(
                                    ConnectorHierarchyChangeEvent.class);
                            event.setOldChildren(oldComponents);
                            event.setConnector(parentConnector);
                            ccc.setChildComponents(newComponents);
                            result.events.add(event);
                        }
                    } else if (!newComponents.isEmpty()) {
                        getLogger().severe("Hierachy claims "
                                + Util.getConnectorString(parentConnector)
                                + " has component children even though it isn't a HasComponentsConnector");
                    }

                    Profiler.leave(
                            "updateConnectorHierarchy handle HasComponentsConnector");

                    Profiler.enter("updateConnectorHierarchy setChildren");
                    parentConnector.setChildren(newChildren);
                    Profiler.leave("updateConnectorHierarchy setChildren");

                    Profiler.enter(
                            "updateConnectorHierarchy find removed children");

                    /*
                     * Find children removed from this parent and mark for
                     * removal unless they are already attached to some other
                     * parent.
                     */
                    for (ServerConnector oldChild : oldChildren) {
                        if (oldChild.getParent() != parentConnector) {
                            // Ignore if moved to some other connector
                            continue;
                        }

                        if (!newChildren.contains(oldChild)) {
                            /*
                             * Consider child detached for now, will be cleared
                             * if it is later on added to some other parent.
                             */
                            maybeDetached.add(oldChild.getConnectorId());
                        }
                    }

                    Profiler.leave(
                            "updateConnectorHierarchy find removed children");
                } catch (final Throwable e) {
                    getLogger().log(Level.SEVERE,
                            "Error updating connector hierarchy", e);
                } finally {
                    Profiler.leave("updateConnectorHierarchy hierarchy entry");
                }
            }

            private void recursivelyDetach(ServerConnector connector,
                    JsArrayObject<ConnectorHierarchyChangeEvent> events,
                    FastStringSet detachedConnectors) {
                detachedConnectors.add(connector.getConnectorId());

                /*
                 * Reset state in an attempt to keep it consistent with the
                 * hierarchy. No children and no parent is the initial situation
                 * for the hierarchy, so changing the state to its initial value
                 * is the closest we can get without data from the server.
                 * #10151
                 */
                String prefix = getClass().getSimpleName() + " ";
                Profiler.enter(prefix + "recursivelyDetach reset state");
                try {
                    Profiler.enter(prefix
                            + "recursivelyDetach reset state - getStateType");
                    Type stateType = AbstractConnector.getStateType(connector);
                    Profiler.leave(prefix
                            + "recursivelyDetach reset state - getStateType");

                    // Empty state instance to get default property values from
                    Profiler.enter(prefix
                            + "recursivelyDetach reset state - createInstance");
                    Object defaultState = stateType.createInstance();
                    Profiler.leave(prefix
                            + "recursivelyDetach reset state - createInstance");

                    if (connector instanceof AbstractConnector) {
                        // optimization as the loop setting properties is very
                        // slow
                        replaceState((AbstractConnector) connector,
                                defaultState);
                    } else {
                        SharedState state = connector.getState();

                        Profiler.enter(prefix
                                + "recursivelyDetach reset state - properties");
                        JsArrayObject<Property> properties = stateType
                                .getPropertiesAsArray();
                        int size = properties.size();
                        for (int i = 0; i < size; i++) {
                            Property property = properties.get(i);
                            property.setValue(state,
                                    property.getValue(defaultState));
                        }
                        Profiler.leave(prefix
                                + "recursivelyDetach reset state - properties");
                    }
                } catch (NoDataException e) {
                    throw new RuntimeException("Can't reset state for "
                            + Util.getConnectorString(connector), e);
                } finally {
                    Profiler.leave(prefix + "recursivelyDetach reset state");
                }

                Profiler.enter(prefix + "recursivelyDetach perform detach");
                /*
                 * Recursively detach children to make sure they get
                 * setParent(null) and hierarchy change events as needed.
                 */
                for (ServerConnector child : connector.getChildren()) {
                    /*
                     * Server doesn't send updated child data for removed
                     * connectors -> ignore child that still seems to be a child
                     * of this connector although it has been moved to some part
                     * of the hierarchy that is not detached.
                     */
                    if (child.getParent() != connector) {
                        continue;
                    }
                    recursivelyDetach(child, events, detachedConnectors);
                }
                Profiler.leave(prefix + "recursivelyDetach perform detach");

                /*
                 * Clear child list and parent
                 */
                Profiler.enter(
                        prefix + "recursivelyDetach clear children and parent");
                connector
                        .setChildren(Collections.<ServerConnector> emptyList());
                connector.setParent(null);
                Profiler.leave(
                        prefix + "recursivelyDetach clear children and parent");

                /*
                 * Create an artificial hierarchy event for containers to give
                 * it a chance to clean up after its children if it has any
                 */
                Profiler.enter(
                        prefix + "recursivelyDetach create hierarchy event");
                if (connector instanceof HasComponentsConnector) {
                    HasComponentsConnector ccc = (HasComponentsConnector) connector;
                    List<ComponentConnector> oldChildren = ccc
                            .getChildComponents();
                    if (!oldChildren.isEmpty()) {
                        /*
                         * HasComponentsConnector has a separate child component
                         * list that should also be cleared
                         */
                        ccc.setChildComponents(
                                Collections.<ComponentConnector> emptyList());

                        // Create event and add it to the list of pending events
                        ConnectorHierarchyChangeEvent event = GWT
                                .create(ConnectorHierarchyChangeEvent.class);
                        event.setConnector(connector);
                        event.setOldChildren(oldChildren);
                        events.add(event);
                    }
                }
                Profiler.leave(
                        prefix + "recursivelyDetach create hierarchy event");
            }

            private native void replaceState(AbstractConnector connector,
                    Object defaultState)
            /*-{
                connector.@com.vaadin.client.ui.AbstractConnector::state = defaultState;
            }-*/;

            private void handleRpcInvocations(ValueMap json) {
                if (json.containsKey("rpc")) {
                    Profiler.enter("handleRpcInvocations");

                    getLogger()
                            .info(" * Performing server to client RPC calls");

                    JsonArray rpcCalls = Util
                            .jso2json(json.getJavaScriptObject("rpc"));

                    int rpcLength = rpcCalls.length();
                    for (int i = 0; i < rpcLength; i++) {
                        try {
                            JsonArray rpcCall = rpcCalls.getArray(i);
                            MethodInvocation invocation = getRpcManager()
                                    .parseAndApplyInvocation(rpcCall,
                                            connection);

                            if (onlyNoLayoutUpdates && !RpcManager
                                    .getMethod(invocation).isNoLayout()) {
                                onlyNoLayoutUpdates = false;
                            }

                        } catch (final Throwable e) {
                            getLogger().log(Level.SEVERE,
                                    "Error performing server to client RPC calls",
                                    e);
                        }
                    }

                    Profiler.leave("handleRpcInvocations");
                }
            }

        };
        ApplicationConfiguration.runWhenDependenciesLoaded(c);
    }

    private void endRequestIfResponse(ValueMap json) {
        if (isResponse(json)) {
            // End the request if the received message was a
            // response, not sent asynchronously
            getMessageSender().endRequest();
        }
    }

    private boolean isResynchronize(ValueMap json) {
        return json.containsKey(ApplicationConstants.RESYNCHRONIZE_ID);
    }

    private boolean isResponse(ValueMap json) {
        ValueMap meta = json.getValueMap("meta");
        if (meta == null || !meta.containsKey("async")) {
            return true;
        }
        return false;
    }

    /**
     * Checks if the given serverId is the one we are currently waiting for from
     * the server
     */
    private boolean isNextExpectedMessage(int serverId) {
        if (serverId == -1) {
            return true;
        }
        if (serverId == getExpectedServerId()) {
            return true;
        }
        if (lastSeenServerSyncId == UNDEFINED_SYNC_ID) {
            // First message is always ok
            return true;
        }
        return false;

    }

    private int getServerId(ValueMap json) {
        if (json.containsKey(ApplicationConstants.SERVER_SYNC_ID)) {
            return json.getInt(ApplicationConstants.SERVER_SYNC_ID);
        } else {
            return -1;
        }
    }

    private int getExpectedServerId() {
        return lastSeenServerSyncId + 1;
    }

    /**
     * Timer used to make sure that no misbehaving components can delay response
     * handling forever.
     */
    Timer forceHandleMessage = new Timer() {
        @Override
        public void run() {
            if (!responseHandlingLocks.isEmpty()) {
                // Lock which was never release -> bug in locker or things just
                // too slow
                getLogger().warning(
                        "WARNING: reponse handling was never resumed, forcibly removing locks...");
                responseHandlingLocks.clear();
            } else {
                // Waited for out-of-order message which never arrived
                // Do one final check and resynchronize if the message is not
                // there. The final check is only a precaution as this timer
                // should have been cancelled if the message has arrived
                getLogger().warning("Gave up waiting for message "
                        + getExpectedServerId() + " from the server");

            }
            if (!handlePendingMessages() && !pendingUIDLMessages.isEmpty()) {
                // There are messages but the next id was not found, likely it
                // has been lost
                // Drop pending messages and resynchronize
                pendingUIDLMessages.clear();
                getMessageSender().resynchronize();
            }
        }
    };

    /**
     * This method can be used to postpone rendering of a response for a short
     * period of time (e.g. to avoid the rendering process during animation).
     *
     * @param lock
     */
    public void suspendReponseHandling(Object lock) {
        responseHandlingLocks.add(lock);
    }

    /**
     * Resumes the rendering process once all locks have been removed.
     *
     * @param lock
     */
    public void resumeResponseHandling(Object lock) {
        responseHandlingLocks.remove(lock);
        if (responseHandlingLocks.isEmpty()) {
            // Cancel timer that breaks the lock
            forceHandleMessage.cancel();

            if (!pendingUIDLMessages.isEmpty()) {
                getLogger().info(
                        "No more response handling locks, handling pending requests.");
                handlePendingMessages();
            }
        }
    }

    private static native final int calculateBootstrapTime()
    /*-{
        if ($wnd.performance && $wnd.performance.timing) {
            return (new Date).getTime() - $wnd.performance.timing.responseStart;
        } else {
            // performance.timing not supported
            return -1;
        }
    }-*/;

    /**
     * Finds the next pending UIDL message and handles it (next pending is
     * decided based on the server id)
     *
     * @return true if a message was handled, false otherwise
     */
    private boolean handlePendingMessages() {
        if (pendingUIDLMessages.isEmpty()) {
            return false;
        }

        // Try to find the next expected message
        PendingUIDLMessage toHandle = null;
        for (PendingUIDLMessage message : pendingUIDLMessages) {
            if (isNextExpectedMessage(getServerId(message.json))) {
                toHandle = message;
                break;
            }
        }

        if (toHandle != null) {
            pendingUIDLMessages.remove(toHandle);
            handleJSON(toHandle.getJson());
            // Any remaining messages will be handled when this is called
            // again at the end of handleJSON
            return true;
        } else {
            return false;
        }

    }

    private void removeOldPendingMessages() {
        Iterator<PendingUIDLMessage> i = pendingUIDLMessages.iterator();
        while (i.hasNext()) {
            PendingUIDLMessage m = i.next();
            int serverId = getServerId(m.json);
            if (serverId != -1 && serverId < getExpectedServerId()) {
                getLogger().info("Removing old message with id " + serverId);
                i.remove();
            }
        }
    }

    /**
     * Gets the server id included in the last received response.
     * <p>
     * This id can be used by connectors to determine whether new data has been
     * received from the server to avoid doing the same calculations multiple
     * times.
     * <p>
     * No guarantees are made for the structure of the id other than that there
     * will be a new unique value every time a new response with data from the
     * server is received.
     * <p>
     * The initial id when no request has yet been processed is -1.
     *
     * @return an id identifying the response
     */
    public int getLastSeenServerSyncId() {
        return lastSeenServerSyncId;
    }

    /**
     * Gets the token (aka double submit cookie) that the server uses to protect
     * against Cross Site Request Forgery attacks.
     *
     * @return the CSRF token string
     */
    public String getCsrfToken() {
        return csrfToken;
    }

    /**
     * Gets the push connection identifier for this session. Used when
     * establishing a push connection with the client.
     *
     * @return the push connection identifier string
     *
     * @since 8.0.6
     */
    public String getPushId() {
        return pushId;
    }

    /**
     * Checks whether state changes are currently being processed. Certain
     * operations are not allowed when the internal state of the application
     * might be in an inconsistent state because some state changes have been
     * applied but others not. This includes running layotus.
     *
     * @return <code>true</code> if the internal state might be inconsistent
     *         because changes are being processed; <code>false</code> if the
     *         state should be consistent
     */
    public boolean isUpdatingState() {
        return updatingState;
    }

    /**
     * Checks if the first UIDL has been handled
     *
     * @return true if the initial UIDL has already been processed, false
     *         otherwise
     */
    public boolean isInitialUidlHandled() {
        return bootstrapTime != 0;
    }

    private LayoutManager getLayoutManager() {
        return LayoutManager.get(connection);
    }

    private ConnectorMap getConnectorMap() {
        return ConnectorMap.get(connection);
    }

    private UIConnector getUIConnector() {
        return connection.getUIConnector();
    }

    private RpcManager getRpcManager() {
        return connection.getRpcManager();
    }

    private MessageSender getMessageSender() {
        return connection.getMessageSender();
    }

    /**
     * Strips the JSON wrapping from the given json string with wrapping.
     *
     * If the given string is not wrapped as expected, returns null
     *
     * @since 7.6
     * @param jsonWithWrapping
     *            the JSON received from the server
     * @return an unwrapped JSON string or null if the given string was not
     *         wrapped
     */
    public static String stripJSONWrapping(String jsonWithWrapping) {
        if (jsonWithWrapping == null) {
            return null;
        }

        if (!jsonWithWrapping.startsWith(JSON_COMMUNICATION_PREFIX)
                || !jsonWithWrapping.endsWith(JSON_COMMUNICATION_SUFFIX)) {
            return null;
        }
        return jsonWithWrapping.substring(JSON_COMMUNICATION_PREFIX.length(),
                jsonWithWrapping.length() - JSON_COMMUNICATION_SUFFIX.length());
    }

    /**
     * Unwraps and parses the given JSON, originating from the server
     *
     * @param jsonText
     *            the json from the server
     * @return A parsed ValueMap or null if the input could not be parsed (or
     *         was null)
     */
    public static ValueMap parseJson(String jsonText) {
        if (jsonText == null) {
            return null;
        }
        final double start = Profiler.getRelativeTimeMillis();
        try {
            ValueMap json = parseJSONResponse(jsonText);
            getLogger().info("JSON parsing took "
                    + Util.round(Profiler.getRelativeTimeMillis() - start, 3)
                    + "ms");
            return json;
        } catch (final Exception e) {
            getLogger().severe("Unable to parse JSON: " + jsonText);
            return null;
        }
    }

    private static native ValueMap parseJSONResponse(String jsonText)
    /*-{
      try {
        return JSON.parse(jsonText);
      } catch (ignored) {
        return eval('(' + jsonText + ')');
      }
    }-*/;

    /**
     * Parse the given wrapped JSON, received from the server, to a ValueMap
     *
     * @param wrappedJsonText
     *            the json, wrapped as done by the server
     * @return a ValueMap, or null if the wrapping was incorrect or json could
     *         not be parsed
     */
    public static ValueMap parseWrappedJson(String wrappedJsonText) {
        return parseJson(stripJSONWrapping(wrappedJsonText));
    }

    private static final native double getFetchStartTime()
    /*-{
        if ($wnd.performance && $wnd.performance.timing && $wnd.performance.timing.fetchStart) {
            return $wnd.performance.timing.fetchStart;
        } else {
            return 0;
        }
    }-*/;

}
