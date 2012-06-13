/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.client.communication.ClientRpc;
import com.vaadin.terminal.gwt.client.communication.ServerRpc;
import com.vaadin.terminal.gwt.client.communication.SharedState;
import com.vaadin.terminal.gwt.server.ClientConnector;
import com.vaadin.terminal.gwt.server.ClientMethodInvocation;
import com.vaadin.terminal.gwt.server.RpcManager;
import com.vaadin.terminal.gwt.server.RpcTarget;
import com.vaadin.terminal.gwt.server.ServerRpcManager;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.Root;

/**
 *
 */
public abstract class AbstractClientConnector implements ClientConnector {
    /**
     * A map from client to server RPC interface class to the RPC call manager
     * that handles incoming RPC calls for that interface.
     */
    private Map<Class<?>, RpcManager> rpcManagerMap = new HashMap<Class<?>, RpcManager>();

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

    /**
     * Pending RPC method invocations to be sent.
     */
    private ArrayList<ClientMethodInvocation> pendingInvocations = new ArrayList<ClientMethodInvocation>();

    private String connectorId;

    private ArrayList<Extension> extensions = new ArrayList<Extension>();

    private ClientConnector parent;

    /* Documentation copied from interface */
    public void requestRepaint() {
        Root root = getRoot();
        if (root != null) {
            root.getDirtyConnectorTracker().markDirty(this);
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
    protected <T> void registerRpc(T implementation, Class<T> rpcInterfaceType) {
        rpcManagerMap.put(rpcInterfaceType, new ServerRpcManager<T>(
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
        Class<T> type = (Class<T>) interfaces[0];
        registerRpc(implementation, type);
    }

    public SharedState getState() {
        if (null == sharedState) {
            sharedState = createState();
        }
        return sharedState;
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

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.gwt.server.ClientConnector#getStateType()
     */
    public Class<? extends SharedState> getStateType() {
        try {
            Method m = getClass().getMethod("getState", (Class[]) null);
            Class<?> type = m.getReturnType();
            return type.asSubclass(SharedState.class);
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
    public <T extends ClientRpc> T getRpcProxy(final Class<T> rpcInterface) {
        // create, initialize and return a dynamic proxy for RPC
        try {
            if (!rpcProxyMap.containsKey(rpcInterface)) {
                Class<?> proxyClass = Proxy.getProxyClass(
                        rpcInterface.getClassLoader(), rpcInterface);
                Constructor<?> constructor = proxyClass
                        .getConstructor(InvocationHandler.class);
                T rpcProxy = rpcInterface.cast(constructor
                        .newInstance(new RpcInvoicationHandler(rpcInterface)));
                // cache the proxy
                rpcProxyMap.put(rpcInterface, rpcProxy);
            }
            return (T) rpcProxyMap.get(rpcInterface);
        } catch (Exception e) {
            // TODO exception handling?
            throw new RuntimeException(e);
        }
    }

    private static final class AllChildrenIterable implements
            Iterable<ClientConnector>, Serializable {
        private final ClientConnector connector;

        private AllChildrenIterable(ClientConnector connector) {
            this.connector = connector;
        }

        public Iterator<ClientConnector> iterator() {
            CombinedIterator<ClientConnector> iterator = new CombinedIterator<ClientConnector>();
            iterator.addIterator(connector.getExtensionIterator());

            if (connector instanceof HasComponents) {
                HasComponents hasComponents = (HasComponents) connector;
                iterator.addIterator(hasComponents.iterator());
            }

            return iterator;
        }
    }

    private class RpcInvoicationHandler implements InvocationHandler,
            Serializable {

        private String rpcInterfaceName;

        public RpcInvoicationHandler(Class<?> rpcInterface) {
            rpcInterfaceName = rpcInterface.getName().replaceAll("\\$", ".");
        }

        public Object invoke(Object proxy, Method method, Object[] args)
                throws Throwable {
            addMethodInvocationToQueue(rpcInterfaceName, method, args);
            // TODO no need to do full repaint if only RPC calls
            requestRepaint();
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
    }

    /**
     * @see RpcTarget#getRpcManager(Class)
     * 
     * @param rpcInterface
     *            RPC interface for which a call was made
     * @return RPC Manager handling calls for the interface
     * 
     * @since 7.0
     */
    public RpcManager getRpcManager(Class<?> rpcInterface) {
        return rpcManagerMap.get(rpcInterface);
    }

    public List<ClientMethodInvocation> retrievePendingRpcCalls() {
        if (pendingInvocations.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<ClientMethodInvocation> result = pendingInvocations;
            pendingInvocations = new ArrayList<ClientMethodInvocation>();
            return Collections.unmodifiableList(result);
        }
    }

    public String getConnectorId() {
        if (connectorId == null) {
            if (getApplication() == null) {
                throw new RuntimeException(
                        "Component must be attached to an application when getConnectorId() is called for the first time");
            }
            connectorId = getApplication().createConnectorId(this);
        }
        return connectorId;
    }

    /**
     * Finds the Application to which this connector belongs. If the connector
     * has not been attached, <code>null</code> is returned.
     * 
     * @return The connector's application, or <code>null</code> if not attached
     */
    protected Application getApplication() {
        Root root = getRoot();
        if (root == null) {
            return null;
        } else {
            return root.getApplication();
        }
    }

    /**
     * Finds a Root ancestor of this connector. <code>null</code> is returned if
     * no Root ancestor is found (typically because the connector is not
     * attached to a proper hierarchy).
     * 
     * @return the Root ancestor of this connector, or <code>null</code> if none
     *         is found.
     */
    protected Root getRoot() {
        ClientConnector connector = this;
        while (connector != null) {
            if (connector instanceof Root) {
                return (Root) connector;
            }
            connector = connector.getParent();
        }
        return null;
    }

    private static Logger getLogger() {
        return Logger.getLogger(AbstractClientConnector.class.getName());
    }

    public void requestRepaintAll() {
        requestRepaint();

        for (ClientConnector connector : getAllChildrenIterable(this)) {
            connector.requestRepaintAll();
        }
    }

    private static final class CombinedIterator<T> implements Iterator<T>,
            Serializable {

        private final Collection<Iterator<? extends T>> iterators = new ArrayList<Iterator<? extends T>>();

        public void addIterator(Iterator<? extends T> iterator) {
            iterators.add(iterator);
        }

        public boolean hasNext() {
            for (Iterator<? extends T> i : iterators) {
                if (i.hasNext()) {
                    return true;
                }
            }
            return false;
        }

        public T next() {
            for (Iterator<? extends T> i : iterators) {
                if (i.hasNext()) {
                    return i.next();
                }
            }
            throw new NoSuchElementException();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public static Iterable<ClientConnector> getAllChildrenIterable(
            final ClientConnector connector) {
        return new AllChildrenIterable(connector);
    }

    public Iterator<Extension> getExtensionIterator() {
        return Collections.unmodifiableList(extensions).iterator();
    }

    protected void addExtension(Extension extension) {
        addExtensionAtIndex(extension, extensions.size());
    }

    protected void addExtensionAtIndex(Extension extension, int index) {
        ClientConnector previousParent = extension.getParent();
        if (previousParent == this) {
            int oldIndex = extensions.indexOf(extension);
            if (oldIndex < index) {
                index--;
            }
            extensions.remove(oldIndex);
            extensions.add(index, extension);
        } else {
            if (previousParent != null) {
                previousParent.removeExtension(extension);
            }
            extensions.add(index, extension);
            extension.setParent(this);
        }
        requestRepaint();
    }

    public void removeExtension(Extension extension) {
        extension.setParent(null);
        extensions.remove(extension);
        requestRepaint();
    }

    public void setParent(ClientConnector parent) {

        // If the parent is not changed, don't do anything
        if (parent == this.parent) {
            return;
        }

        if (parent != null && this.parent != null) {
            throw new IllegalStateException(getClass().getName()
                    + " already has a parent.");
        }

        // Send detach event if the component have been connected to a window
        if (getApplication() != null) {
            detach();
        }

        // Connect to new parent
        this.parent = parent;

        // Send attach event if connected to an application
        if (getApplication() != null) {
            attach();
        }
    }

    public ClientConnector getParent() {
        return parent;
    }

    public void attach() {
        requestRepaint();

        for (ClientConnector connector : getAllChildrenIterable(this)) {
            connector.attach();
        }
    }

    public void detach() {
        for (ClientConnector connector : getAllChildrenIterable(this)) {
            connector.detach();
        }
    }

    public boolean isConnectorEnabled() {
        if (getParent() == null) {
            // No parent -> the component cannot receive updates from the client
            return false;
        } else {
            return getParent().isConnectorEnabled();
        }
    }
}
