package main.java.com.vaadin.server;

import com.vaadin.server.ServerRpcMethodInvocation;

/**
 * A manager that allows clients to signal to the runtime that their resources are no longer valid.
 * A use case would be an OSGi bundle that is unloading.
 *
 * @since TBD
 */
public class InvalidatableResourceManager {
    static public void invalidateCachedResources(ClassLoader classLoader) {
        ServerRpcMethodInvocation.invalidateCachedResources(classLoader);
        //TODO invalidate EventRouter resources
        //TODO invalidate all session dependencies
        //session.getCommunicationManager().getDependencies()
    }
}
