/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.server;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import com.vaadin.event.EventRouter;
import com.vaadin.event.MethodEventSource;
import com.vaadin.shared.communication.ClientRpc;
import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.shared.communication.SharedState;
import com.vaadin.shared.ui.ComponentStateUtil;
import com.vaadin.ui.Component;
import com.vaadin.ui.Component.Event;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.LegacyComponent;
import com.vaadin.ui.UI;


/**
 * An abstract base class for ClientConnector implementations. This class
 * provides all the basic functionality required for connectors.
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public abstract class AbstractClientConnector implements ClientConnector,
        MethodEventSource {
    /**
     * A map from client to server RPC interface class name to the RPC call
     * manager that handles incoming RPC calls for that interface.
     */
    private Map<String, ServerRpcManager<?>> rpcManagerMap = new HashMap<String, ServerRpcManager<?>>();

    /**
     * A map from server to client RPC interface class to the RPC proxy that
     * sends ourgoing RPC calls for that interface.
     */
    private Map<Class<?>, ClientRpc> rpcProxyMap = new HashMap<Class<?>, ClientRpc>();

    /**
     * Shared state object to be communicated from the server to the client when
     * modified.
     */
    private SharedState sharedState;

    private Class<? extends SharedState> stateType;

    /**
     * Pending RPC method invocations to be sent.
     */
    private ArrayList<ClientMethodInvocation> pendingInvocations = new ArrayList<ClientMethodInvocation>();

    private String connectorId;

    private ArrayList<Extension> extensions = new ArrayList<Extension>();

    /**
     * The EventRouter used for the event model.
     */
    private EventRouter eventRouter = null;

    private ErrorHandler errorHandler = null;

    @Override
    public void addAttachListener(AttachListener listener) {
        addListener(AttachEvent.ATTACH_EVENT_IDENTIFIER, AttachEvent.class,
                listener, AttachListener.attachMethod);
    }

    @Override
    public void removeAttachListener(AttachListener listener) {
        removeListener(AttachEvent.ATTACH_EVENT_IDENTIFIER, AttachEvent.class,
                listener);
    }

    @Override
    public void addDetachListener(DetachListener listener) {
        addListener(DetachEvent.DETACH_EVENT_IDENTIFIER, DetachEvent.class,
                listener, DetachListener.detachMethod);
    }

    @Override
    public void removeDetachListener(DetachListener listener) {
        removeListener(DetachEvent.DETACH_EVENT_IDENTIFIER, DetachEvent.class,
                listener);
    }

    /**
     * @deprecated As of 7.0, use {@link #markAsDirty()} instead. Note that you
     *             typically do not need to call {@link #markAsDirty()} as
     *             {@link #getState()} will mark the connector dirty and the
     *             framework will then check what, if anything, needs to be sent
     *             to the client. {@link LegacyComponent}s which rely on paint
     *             might still need to call this or {@link #markAsDirty()} .
     */
    @Deprecated
    @Override
    public void requestRepaint() {
        markAsDirty();
    }

    /* Documentation copied from interface */
    @Override
    public void markAsDirty() {
        assert getSession() == null || getSession().hasLock() : buildLockAssertMessage("markAsDirty()");
        UI uI = getUI();
        if (uI != null) {
            uI.getConnectorTracker().markDirty(this);
        }
    }

    private String buildLockAssertMessage(String method) {
        if (VaadinService.isOtherSessionLocked(getSession())) {
            return "The session of this connecor is not locked, but there is another session that is locked. "
                    + "This might be caused by accidentally using a connector that belongs to another session.";
        } else {
            return "Session must be locked when " + method + " is called";
        }
    }

    /**
     * Registers an RPC interface implementation for this component.
     * 
     * A component can listen to multiple RPC interfaces, and subclasses can
     * register additional implementations.
     * 
     * @since 7.0
     * 
     * @param implementation
     *            RPC interface implementation
     * @param rpcInterfaceType
     *            RPC interface class for which the implementation should be
     *            registered
     */
    protected <T extends ServerRpc> void registerRpc(T implementation,
            Class<T> rpcInterfaceType) {
        rpcManagerMap.put(rpcInterfaceType.getName(), new ServerRpcManager<T>(
                implementation, rpcInterfaceType));
    }

    /**
     * Registers an RPC interface implementation for this component.
     * 
     * A component can listen to multiple RPC interfaces, and subclasses can
     * register additional implementations.
     * 
     * @since 7.0
     * 
     * @param implementation
     *            RPC interface implementation. Also used to deduce the type.
     */
    protected <T extends ServerRpc> void registerRpc(T implementation) {
        Class<?> cls = implementation.getClass();
        Class<?>[] interfaces = cls.getInterfaces();
        while (interfaces.length == 0) {
            // Search upwards until an interface is found. It must be found as T
            // extends ServerRpc
            cls = cls.getSuperclass();
            interfaces = cls.getInterfaces();
        }
        if (interfaces.length != 1
                || !(ServerRpc.class.isAssignableFrom(interfaces[0]))) {
            throw new RuntimeException(
                    "Use registerRpc(T implementation, Class<T> rpcInterfaceType) if the Rpc implementation implements more than one interface");
        }
        @SuppressWarnings("unchecked")
        Class<T> type = (Class<T>) interfaces[0];
        registerRpc(implementation, type);
    }

    /**
     * Returns the shared state for this connector. The shared state object is
     * shared between the server connector and the client connector. Changes are
     * only communicated from the server to the client and not in the other
     * direction.
     * <p>
     * As a side effect, marks the connector dirty so any changes done to the
     * state will be sent to the client. Use {@code getState(false)} to avoid
     * marking the connector as dirty.
     * </p>
     * 
     * @return The shared state for this connector. Never null.
     */
    protected SharedState getState() {
        return getState(true);
    }

    /**
     * Returns the shared state for this connector.
     * 
     * @param markAsDirty
     *            true if the connector should automatically be marked dirty,
     *            false otherwise
     * 
     * @return The shared state for this connector. Never null.
     * @see #getState()
     */
    protected SharedState getState(boolean markAsDirty) {
        assert getSession() == null || getSession().hasLock() : buildLockAssertMessage("getState()");

        if (null == sharedState) {
            sharedState = createState();
        }
        if (markAsDirty) {
            UI ui = getUI();
            if (ui != null && !ui.getConnectorTracker().isDirty(this)
                    && !ui.getConnectorTracker().isWritingResponse()) {
                ui.getConnectorTracker().markDirty(this);
            }
        }
        return sharedState;
    }

    @Override
    public JSONObject encodeState() throws JSONException {
        return LegacyCommunicationManager.encodeState(this, getState(false));
    }

    /**
     * Creates the shared state bean to be used in server to client
     * communication.
     * <p>
     * By default a state object of the defined return type of
     * {@link #getState()} is created. Subclasses can override this method and
     * return a new instance of the correct state class but this should rarely
     * be necessary.
     * </p>
     * <p>
     * No configuration of the values of the state should be performed in
     * {@link #createState()}.
     * 
     * @since 7.0
     * 
     * @return new shared state object
     */
    protected SharedState createState() {
        try {
            return getStateType().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error creating state of type " + getStateType().getName()
                            + " for " + getClass().getName(), e);
        }
    }

    @Override
    public Class<? extends SharedState> getStateType() {
        // Lazy load because finding type can be expensive because of the
        // exceptions flying around
        if (stateType == null) {
            stateType = findStateType();
        }

        return stateType;
    }

    private Class<? extends SharedState> findStateType() {
        try {
            Class<?> class1 = getClass();
            while (class1 != null) {
                try {
                    Method m = class1.getDeclaredMethod("getState",
                            (Class[]) null);
                    Class<?> type = m.getReturnType();
                    if (!m.isSynthetic()) {
                        return type.asSubclass(SharedState.class);
                    }
                } catch (NoSuchMethodException nsme) {
                }
                // Try in superclass instead
                class1 = class1.getSuperclass();
            }
            throw new NoSuchMethodException(getClass().getCanonicalName()
                    + ".getState()");
        } catch (Exception e) {
            throw new RuntimeException("Error finding state type for "
                    + getClass().getName(), e);
        }
    }

    /**
     * Returns an RPC proxy for a given server to client RPC interface for this
     * component.
     * 
     * TODO more javadoc, subclasses, ...
     * 
     * @param rpcInterface
     *            RPC interface type
     * 
     * @since 7.0
     */
    protected <T extends ClientRpc> T getRpcProxy(final Class<T> rpcInterface) {
        // create, initialize and return a dynamic proxy for RPC
        try {
            if (!rpcProxyMap.containsKey(rpcInterface)) {
                Class<?> proxyClass = Proxy.getProxyClass(
                        rpcInterface.getClassLoader(), rpcInterface);
                Constructor<?> constructor = proxyClass
                        .getConstructor(InvocationHandler.class);
                T rpcProxy = rpcInterface.cast(constructor
                        .newInstance(new RpcInvocationHandler(rpcInterface)));
                // cache the proxy
                rpcProxyMap.put(rpcInterface, rpcProxy);
            }
            return (T) rpcProxyMap.get(rpcInterface);
        } catch (Exception e) {
            // TODO exception handling?
            throw new RuntimeException(e);
        }
    }

    private class RpcInvocationHandler implements InvocationHandler,
            Serializable {

        private String rpcInterfaceName;

        public RpcInvocationHandler(Class<?> rpcInterface) {
            rpcInterfaceName = rpcInterface.getName().replaceAll("\\$", ".");
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args)
                throws Throwable {
            if (method.getDeclaringClass() == Object.class) {
                // Don't add Object methods such as toString and hashCode as
                // invocations
                return method.invoke(this, args);
            }
            addMethodInvocationToQueue(rpcInterfaceName, method, args);
            return null;
        }

    }

    /**
     * For internal use: adds a method invocation to the pending RPC call queue.
     * 
     * @param interfaceName
     *            RPC interface name
     * @param method
     *            RPC method
     * @param parameters
     *            RPC all parameters
     * 
     * @since 7.0
     */
    protected void addMethodInvocationToQueue(String interfaceName,
            Method method, Object[] parameters) {
        // add to queue
        pendingInvocations.add(new ClientMethodInvocation(this, interfaceName,
                method, parameters));
        // TODO no need to do full repaint if only RPC calls
        requestRepaint();
    }

    @Override
    public ServerRpcManager<?> getRpcManager(String rpcInterfaceName) {
        return rpcManagerMap.get(rpcInterfaceName);
    }

    @Override
    public List<ClientMethodInvocation> retrievePendingRpcCalls() {
        if (pendingInvocations.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<ClientMethodInvocation> result = pendingInvocations;
            pendingInvocations = new ArrayList<ClientMethodInvocation>();
            return Collections.unmodifiableList(result);
        }
    }

    @Override
    public String getConnectorId() {
        if (connectorId == null) {
            if (getSession() == null) {
                throw new RuntimeException(
                        "Component must be attached to a session when getConnectorId() is called for the first time");
            }
            connectorId = getSession().createConnectorId(this);
        }
        return connectorId;
    }

    /**
     * Finds the {@link VaadinSession} to which this connector belongs. If the
     * connector has not been attached, <code>null</code> is returned.
     * 
     * @return The connector's session, or <code>null</code> if not attached
     */
    protected VaadinSession getSession() {
        UI uI = getUI();
        if (uI == null) {
            return null;
        } else {
            return uI.getSession();
        }
    }

    /**
     * Finds a UI ancestor of this connector. <code>null</code> is returned if
     * no UI ancestor is found (typically because the connector is not attached
     * to a proper hierarchy).
     * 
     * @return the UI ancestor of this connector, or <code>null</code> if none
     *         is found.
     */
    @Override
    public UI getUI() {
        ClientConnector connector = this;
        while (connector != null) {
            if (connector instanceof UI) {
                return (UI) connector;
            }
            connector = connector.getParent();
        }
        return null;
    }

    private static Logger getLogger() {
        return Logger.getLogger(AbstractClientConnector.class.getName());
    }

    /**
     * @deprecated As of 7.0, use {@link #markAsDirtyRecursive()} instead
     */
    @Override
    @Deprecated
    public void requestRepaintAll() {
        markAsDirtyRecursive();
    }

    @Override
    public void markAsDirtyRecursive() {
        markAsDirty();

        for (ClientConnector connector : getAllChildrenIterable(this)) {
            connector.markAsDirtyRecursive();
        }
    }

    /**
     * Get an Iterable for iterating over all child connectors, including both
     * extensions and child components.
     * 
     * @param connector
     *            the connector to get children for
     * @return an Iterable giving all child connectors.
     */
    public static Iterable<? extends ClientConnector> getAllChildrenIterable(
            final ClientConnector connector) {

        Collection<Extension> extensions = connector.getExtensions();
        boolean hasComponents = connector instanceof HasComponents;
        boolean hasExtensions = extensions.size() > 0;
        if (!hasComponents && !hasExtensions) {
            // If has neither component nor extensions, return immutable empty
            // list as iterable.
            return Collections.emptyList();
        }
        if (hasComponents && !hasExtensions) {
            // only components
            return (HasComponents) connector;
        }
        if (!hasComponents && hasExtensions) {
            // only extensions
            return extensions;
        }

        // combine the iterators of extensions and components to a new iterable.
        final Iterator<Component> componentsIterator = ((HasComponents) connector)
                .iterator();
        final Iterator<Extension> extensionsIterator = extensions.iterator();
        Iterable<? extends ClientConnector> combinedIterable = new Iterable<ClientConnector>() {

            @Override
            public Iterator<ClientConnector> iterator() {
                return new Iterator<ClientConnector>() {

                    @Override
                    public boolean hasNext() {
                        return componentsIterator.hasNext()
                                || extensionsIterator.hasNext();
                    }

                    @Override
                    public ClientConnector next() {
                        if (componentsIterator.hasNext()) {
                            return componentsIterator.next();
                        }
                        if (extensionsIterator.hasNext()) {
                            return extensionsIterator.next();
                        }
                        throw new NoSuchElementException();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }

                };
            }
        };
        return combinedIterable;
    }

    @Override
    public Collection<Extension> getExtensions() {
        return Collections.unmodifiableCollection(extensions);
    }

    /**
     * Add an extension to this connector. This method is protected to allow
     * extensions to select which targets they can extend.
     * 
     * @param extension
     *            the extension to add
     */
    protected void addExtension(Extension extension) {
        ClientConnector previousParent = extension.getParent();
        if (equals(previousParent)) {
            // Nothing to do, already attached
            return;
        } else if (previousParent != null) {
            throw new IllegalStateException(
                    "Moving an extension from one parent to another is not supported");
        }

        extensions.add(extension);
        extension.setParent(this);
        markAsDirty();
    }

    @Override
    public void removeExtension(Extension extension) {
        extension.setParent(null);
        extensions.remove(extension);
        markAsDirty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.server.ClientConnector#isAttached()
     */
    @Override
    public boolean isAttached() {
        return getSession() != null;
    }

    @Override
    public void attach() {
        markAsDirty();

        getUI().getConnectorTracker().registerConnector(this);

        fireEvent(new AttachEvent(this));

        for (ClientConnector connector : getAllChildrenIterable(this)) {
            connector.attach();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * The {@link #getSession()} and {@link #getUI()} methods might return
     * <code>null</code> after this method is called.
     * </p>
     */
    @Override
    public void detach() {
        for (ClientConnector connector : getAllChildrenIterable(this)) {
            connector.detach();
        }

        fireEvent(new DetachEvent(this));

        getUI().getConnectorTracker().unregisterConnector(this);
    }

    @Override
    public boolean isConnectorEnabled() {
        if (getParent() == null) {
            // No parent -> the component cannot receive updates from the client
            return false;
        } else {
            return getParent().isConnectorEnabled();
        }
    }

    @Override
    public void beforeClientResponse(boolean initial) {
        // Do nothing by default
    }

    @Override
    public boolean handleConnectorRequest(VaadinRequest request,
            VaadinResponse response, String path) throws IOException {
        DownloadStream stream = null;
        String[] parts = path.split("/", 2);
        String key = parts[0];

        VaadinSession session = getSession();
        session.lock();
        try {
            ConnectorResource resource = (ConnectorResource) getResource(key);
            if (resource == null) {
                return false;
            }
            stream = resource.getStream();
        } finally {
            session.unlock();
        }
        stream.writeResponse(request, response);
        return true;
    }

    /**
     * Gets a resource defined using {@link #setResource(String, Resource)} with
     * the corresponding key.
     * 
     * @param key
     *            the string identifier of the resource
     * @return a resource, or <code>null</code> if there's no resource
     *         associated with the given key
     * 
     * @see #setResource(String, Resource)
     */
    protected Resource getResource(String key) {
        return ResourceReference
                .getResource(getState(false).resources.get(key));
    }

    /**
     * Registers a resource with this connector using the given key. This will
     * make the URL for retrieving the resource available to the client-side
     * connector using
     * {@link com.vaadin.terminal.gwt.client.ui.AbstractConnector#getResourceUrl(String)}
     * with the same key.
     * 
     * @param key
     *            the string key to associate the resource with
     * @param resource
     *            the resource to set, or <code>null</code> to clear a previous
     *            association.
     */
    protected void setResource(String key, Resource resource) {
        ResourceReference resourceReference = ResourceReference.create(
                resource, this, key);

        if (resourceReference == null) {
            getState().resources.remove(key);
        } else {
            getState().resources.put(key, resourceReference);
        }
    }

    /* Listener code starts. Should be refactored. */

    /**
     * <p>
     * Registers a new listener with the specified activation method to listen
     * events generated by this component. If the activation method does not
     * have any arguments the event object will not be passed to it when it's
     * called.
     * </p>
     * 
     * <p>
     * This method additionally informs the event-api to route events with the
     * given eventIdentifier to the components handleEvent function call.
     * </p>
     * 
     * <p>
     * For more information on the inheritable event mechanism see the
     * {@link com.vaadin.event com.vaadin.event package documentation}.
     * </p>
     * 
     * @param eventIdentifier
     *            the identifier of the event to listen for
     * @param eventType
     *            the type of the listened event. Events of this type or its
     *            subclasses activate the listener.
     * @param target
     *            the object instance who owns the activation method.
     * @param method
     *            the activation method.
     * 
     * @since 6.2
     */
    protected void addListener(String eventIdentifier, Class<?> eventType,
            Object target, Method method) {
        if (eventRouter == null) {
            eventRouter = new EventRouter();
        }
        boolean needRepaint = !eventRouter.hasListeners(eventType);
        eventRouter.addListener(eventType, target, method);

        if (needRepaint) {
            ComponentStateUtil.addRegisteredEventListener(getState(),
                    eventIdentifier);
        }
    }

    /**
     * Checks if the given {@link Event} type is listened for this component.
     * 
     * @param eventType
     *            the event type to be checked
     * @return true if a listener is registered for the given event type
     */
    protected boolean hasListeners(Class<?> eventType) {
        return eventRouter != null && eventRouter.hasListeners(eventType);
    }

    /**
     * Removes all registered listeners matching the given parameters. Since
     * this method receives the event type and the listener object as
     * parameters, it will unregister all <code>object</code>'s methods that are
     * registered to listen to events of type <code>eventType</code> generated
     * by this component.
     * 
     * <p>
     * This method additionally informs the event-api to stop routing events
     * with the given eventIdentifier to the components handleEvent function
     * call.
     * </p>
     * 
     * <p>
     * For more information on the inheritable event mechanism see the
     * {@link com.vaadin.event com.vaadin.event package documentation}.
     * </p>
     * 
     * @param eventIdentifier
     *            the identifier of the event to stop listening for
     * @param eventType
     *            the exact event type the <code>object</code> listens to.
     * @param target
     *            the target object that has registered to listen to events of
     *            type <code>eventType</code> with one or more methods.
     * 
     * @since 6.2
     */
    protected void removeListener(String eventIdentifier, Class<?> eventType,
            Object target) {
        if (eventRouter != null) {
            eventRouter.removeListener(eventType, target);
            if (!eventRouter.hasListeners(eventType)) {
                ComponentStateUtil.removeRegisteredEventListener(getState(),
                        eventIdentifier);
            }
        }
    }

    /**
     * <p>
     * Registers a new listener with the specified activation method to listen
     * events generated by this component. If the activation method does not
     * have any arguments the event object will not be passed to it when it's
     * called.
     * </p>
     * 
     * <p>
     * For more information on the inheritable event mechanism see the
     * {@link com.vaadin.event com.vaadin.event package documentation}.
     * </p>
     * 
     * @param eventType
     *            the type of the listened event. Events of this type or its
     *            subclasses activate the listener.
     * @param target
     *            the object instance who owns the activation method.
     * @param method
     *            the activation method.
     * 
     */
    @Override
    public void addListener(Class<?> eventType, Object target, Method method) {
        if (eventRouter == null) {
            eventRouter = new EventRouter();
        }
        eventRouter.addListener(eventType, target, method);
    }

    /**
     * <p>
     * Convenience method for registering a new listener with the specified
     * activation method to listen events generated by this component. If the
     * activation method does not have any arguments the event object will not
     * be passed to it when it's called.
     * </p>
     * 
     * <p>
     * This version of <code>addListener</code> gets the name of the activation
     * method as a parameter. The actual method is reflected from
     * <code>object</code>, and unless exactly one match is found,
     * <code>java.lang.IllegalArgumentException</code> is thrown.
     * </p>
     * 
     * <p>
     * For more information on the inheritable event mechanism see the
     * {@link com.vaadin.event com.vaadin.event package documentation}.
     * </p>
     * 
     * <p>
     * Note: Using this method is discouraged because it cannot be checked
     * during compilation. Use {@link #addListener(Class, Object, Method)} or
     * {@link #addListener(com.vaadin.ui.Component.Listener)} instead.
     * </p>
     * 
     * @param eventType
     *            the type of the listened event. Events of this type or its
     *            subclasses activate the listener.
     * @param target
     *            the object instance who owns the activation method.
     * @param methodName
     *            the name of the activation method.
     * @deprecated As of 7.0. This method should be avoided. Use
     *             {@link #addListener(Class, Object, Method)} or
     *             {@link #addListener(String, Class, Object, Method)} instead.
     */
    @Override
    @Deprecated
    public void addListener(Class<?> eventType, Object target, String methodName) {
        if (eventRouter == null) {
            eventRouter = new EventRouter();
        }
        eventRouter.addListener(eventType, target, methodName);
    }

    /**
     * Removes all registered listeners matching the given parameters. Since
     * this method receives the event type and the listener object as
     * parameters, it will unregister all <code>object</code>'s methods that are
     * registered to listen to events of type <code>eventType</code> generated
     * by this component.
     * 
     * <p>
     * For more information on the inheritable event mechanism see the
     * {@link com.vaadin.event com.vaadin.event package documentation}.
     * </p>
     * 
     * @param eventType
     *            the exact event type the <code>object</code> listens to.
     * @param target
     *            the target object that has registered to listen to events of
     *            type <code>eventType</code> with one or more methods.
     */
    @Override
    public void removeListener(Class<?> eventType, Object target) {
        if (eventRouter != null) {
            eventRouter.removeListener(eventType, target);
        }
    }

    /**
     * Removes one registered listener method. The given method owned by the
     * given object will no longer be called when the specified events are
     * generated by this component.
     * 
     * <p>
     * For more information on the inheritable event mechanism see the
     * {@link com.vaadin.event com.vaadin.event package documentation}.
     * </p>
     * 
     * @param eventType
     *            the exact event type the <code>object</code> listens to.
     * @param target
     *            target object that has registered to listen to events of type
     *            <code>eventType</code> with one or more methods.
     * @param method
     *            the method owned by <code>target</code> that's registered to
     *            listen to events of type <code>eventType</code>.
     */
    @Override
    public void removeListener(Class<?> eventType, Object target, Method method) {
        if (eventRouter != null) {
            eventRouter.removeListener(eventType, target, method);
        }
    }

    /**
     * <p>
     * Removes one registered listener method. The given method owned by the
     * given object will no longer be called when the specified events are
     * generated by this component.
     * </p>
     * 
     * <p>
     * This version of <code>removeListener</code> gets the name of the
     * activation method as a parameter. The actual method is reflected from
     * <code>target</code>, and unless exactly one match is found,
     * <code>java.lang.IllegalArgumentException</code> is thrown.
     * </p>
     * 
     * <p>
     * For more information on the inheritable event mechanism see the
     * {@link com.vaadin.event com.vaadin.event package documentation}.
     * </p>
     * 
     * @param eventType
     *            the exact event type the <code>object</code> listens to.
     * @param target
     *            the target object that has registered to listen to events of
     *            type <code>eventType</code> with one or more methods.
     * @param methodName
     *            the name of the method owned by <code>target</code> that's
     *            registered to listen to events of type <code>eventType</code>.
     * @deprecated As of 7.0. This method should be avoided. Use
     *             {@link #removeListener(Class, Object, Method)} instead.
     */
    @Deprecated
    @Override
    public void removeListener(Class<?> eventType, Object target,
            String methodName) {
        if (eventRouter != null) {
            eventRouter.removeListener(eventType, target, methodName);
        }
    }

    /**
     * Returns all listeners that are registered for the given event type or one
     * of its subclasses.
     * 
     * @param eventType
     *            The type of event to return listeners for.
     * @return A collection with all registered listeners. Empty if no listeners
     *         are found.
     */
    public Collection<?> getListeners(Class<?> eventType) {
        if (eventRouter == null) {
            return Collections.EMPTY_LIST;
        }

        return eventRouter.getListeners(eventType);
    }

    /**
     * Sends the event to all listeners.
     * 
     * @param event
     *            the Event to be sent to all listeners.
     */
    protected void fireEvent(EventObject event) {
        if (eventRouter != null) {
            eventRouter.fireEvent(event);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.server.ClientConnector#getErrorHandler()
     */
    @Override
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.server.ClientConnector#setErrorHandler(com.vaadin.server.
     * ErrorHandler)
     */
    @Override
    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        /*
         * This equals method must return true when we're comparing an object to
         * its proxy. This happens a lot with CDI (and possibly Spring) when
         * we're injecting Components. See #14639
         */
        if (obj instanceof AbstractClientConnector) {
            AbstractClientConnector connector = (AbstractClientConnector) obj;
            return connector.isThis(this);
        }
        return false;
    }

    /**
     * For internal use only, may be changed or removed in future versions.
     * <p>
     * This method must be protected, because otherwise it will not be redefined
     * by the proxy to actually be called on the underlying instance.
     * <p>
     * See #14639
     * 
     * @deprecated only defined for framework hacks, do not use.
     */
    @Deprecated
    protected boolean isThis(Object that) {
        return this == that;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
