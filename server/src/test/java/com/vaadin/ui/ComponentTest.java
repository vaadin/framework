package com.vaadin.ui;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;

import com.vaadin.server.ClientConnector;
import com.vaadin.server.LegacyCommunicationManager;
import com.vaadin.server.ServerRpcManager;
import com.vaadin.shared.communication.ServerRpc;

import elemental.json.JsonObject;

/**
 * Base class for component unit tests, providing helper methods for e.g.
 * invoking RPC and updating diff state.
 */
public class ComponentTest {

    /**
     * Perform operations on the component similar to what would be done when
     * the component state is communicated to the client, e.g. update diff state
     * and mark as clean.
     *
     * @param component
     *            the component to update
     */
    public static void syncToClient(AbstractComponent component) {
        updateDiffState(component);
        component.getUI().getConnectorTracker().markClean(component);
    }

    /**
     * Checks if the connector has been marked dirty.
     *
     * @param connector
     *            the connector to check
     * @return <code>true</code> if the connector has been marked dirty,
     *         <code>false</code> otherwise
     */
    public static boolean isDirty(ClientConnector connector) {
        return connector.getUI().getConnectorTracker().isDirty(connector);
    }

    /**
     * Updates the stored diff state from the current component state.
     *
     * @param rta
     *            the component to update
     */
    public static void updateDiffState(AbstractComponent component) {
        LegacyCommunicationManager.encodeState(component, component.getState());

    }

    /**
     * Gets the server rpc handler registered for a component.
     *
     * @param connector
     *            the connector which listens to the RPC
     * @param serverRpcClass
     *            the server RPC class
     * @return the server RPC handler
     */
    public static <T extends ServerRpc> T getRpcProxy(ClientConnector connector,
            Class<T> serverRpcClass) {
        try {
            ServerRpcManager<?> rpcManager = connector
                    .getRpcManager(serverRpcClass.getName());
            Method method = ServerRpcManager.class
                    .getDeclaredMethod("getImplementation");
            method.setAccessible(true);
            return serverRpcClass.cast(method.invoke(rpcManager));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Asserts the set of properties that would be sent as state changes for the
     * given connector.
     *
     * @param connector
     *            the connector that has state changes
     * @param message
     *            the message to show if the properties are not as expected
     * @param expectedProperties
     *            names of the expected properties
     */
    public static void assertEncodedStateProperties(ClientConnector connector,
            String message, String... expectedProperties) {
        assert connector.isAttached();

        JsonObject encodeState = connector.encodeState();

        // Collect to HashSet so that order doesn't matter
        assertEquals(message, new HashSet<>(Arrays.asList(expectedProperties)),
                new HashSet<>(Arrays.asList(encodeState.keys())));
    }

    private ComponentTest() {
    }

}
