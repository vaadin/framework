/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.widgetsetutils.metadata;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.vaadin.shared.communication.ClientRpc;
import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.shared.ui.Connect;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ServerConnector;

public class ConnectorBundle {
    private final String name;
    private final ConnectorBundle previousBundle;

    private final Set<JClassType> needsGwtConstructor = new HashSet<JClassType>();
    private final Map<JClassType, Set<String>> identifiers = new HashMap<JClassType, Set<String>>();
    private final Set<JClassType> visitedTypes = new HashSet<JClassType>();
    private final Set<JClassType> visitQueue = new HashSet<JClassType>();
    private final Map<JClassType, Set<JMethod>> needsReturnType = new HashMap<JClassType, Set<JMethod>>();

    private final Collection<TypeVisitor> visitors;

    private ConnectorBundle(String name, ConnectorBundle previousBundle,
            Collection<TypeVisitor> visitors) {
        this.name = name;
        this.previousBundle = previousBundle;
        this.visitors = visitors;
    }

    public ConnectorBundle(String name, ConnectorBundle previousBundle) {
        this(name, previousBundle, previousBundle.visitors);
    }

    public ConnectorBundle(String name, Collection<TypeVisitor> visitors) {
        this(name, null, visitors);
    }

    public void setNeedsGwtConstructor(JClassType type) {
        if (!needsGwtConstructor(type)) {
            ensureVisited(type);
            needsGwtConstructor.add(type);
        }
    }

    private boolean needsGwtConstructor(JClassType type) {
        if (needsGwtConstructor.contains(type)) {
            return true;
        } else {
            return previousBundle != null
                    && previousBundle.needsGwtConstructor(type);
        }
    }

    public void setIdentifier(JClassType type, String identifier) {
        if (!hasIdentifier(type, identifier)) {
            ensureVisited(type);
            Set<String> set = identifiers.get(type);
            if (set == null) {
                set = new HashSet<String>();
                identifiers.put(type, set);
            }
            set.add(identifier);
        }
    }

    private boolean hasIdentifier(JClassType type, String identifier) {
        if (identifiers.containsKey(type)
                && identifiers.get(type).contains(identifier)) {
            return true;
        } else {
            return previousBundle != null
                    && previousBundle.hasIdentifier(type, identifier);
        }
    }

    public ConnectorBundle getPreviousBundle() {
        return previousBundle;
    }

    public String getName() {
        return name;
    }

    public Map<JClassType, Set<String>> getIdentifiers() {
        return Collections.unmodifiableMap(identifiers);
    }

    public Set<JClassType> getGwtConstructors() {
        return Collections.unmodifiableSet(needsGwtConstructor);
    }

    public void processTypes(TreeLogger logger, Collection<JClassType> types)
            throws UnableToCompleteException {
        for (JClassType type : types) {
            processType(logger, type);
        }
    }

    public void processType(TreeLogger logger, JClassType type)
            throws UnableToCompleteException {
        ensureVisited(type);
        purgeQueue(logger);
    }

    private boolean isTypeVisited(JClassType type) {
        if (visitedTypes.contains(type)) {
            return true;
        } else {
            return previousBundle != null && previousBundle.isTypeVisited(type);
        }
    }

    private void ensureVisited(JClassType type) {
        if (!isTypeVisited(type)) {
            visitQueue.add(type);
        }
    }

    private void purgeQueue(TreeLogger logger) throws UnableToCompleteException {
        while (!visitQueue.isEmpty()) {
            Iterator<JClassType> iterator = visitQueue.iterator();
            JClassType type = iterator.next();
            iterator.remove();

            if (isTypeVisited(type)) {
                continue;
            }
            for (TypeVisitor typeVisitor : visitors) {
                invokeVisitor(logger, type, typeVisitor);
            }
            visitedTypes.add(type);
        }
    }

    private void invokeVisitor(TreeLogger logger, JClassType type,
            TypeVisitor typeVisitor) throws UnableToCompleteException {
        TreeLogger subLogger = logger.branch(Type.TRACE,
                "Visiting " + type.getName() + " with "
                        + typeVisitor.getClass().getSimpleName());
        if (isConnectedConnector(type)) {
            typeVisitor.visitConnector(subLogger, type, this);
        } else if (isClientRpc(type)) {
            typeVisitor.visitClientRpc(subLogger, type, this);
        } else if (isServerRpc(type)) {
            typeVisitor.visitServerRpc(subLogger, type, this);
        }
    }

    public void processSubTypes(TreeLogger logger, JClassType type)
            throws UnableToCompleteException {
        processTypes(logger, Arrays.asList(type.getSubtypes()));
    }

    public void setNeedsReturnType(JClassType type, JMethod method) {
        if (!isNeedsReturnType(type, method)) {
            ensureVisited(type);
            Set<JMethod> set = needsReturnType.get(type);
            if (set == null) {
                set = new HashSet<JMethod>();
                needsReturnType.put(type, set);
            }
            set.add(method);
        }
    }

    private boolean isNeedsReturnType(JClassType type, JMethod method) {
        if (needsReturnType.containsKey(type)
                && needsReturnType.get(type).contains(method)) {
            return true;
        } else {
            return previousBundle != null
                    && previousBundle.isNeedsReturnType(type, method);
        }
    }

    public Map<JClassType, Set<JMethod>> getMethodReturnTypes() {
        return Collections.unmodifiableMap(needsReturnType);
    }

    private static boolean isClientRpc(JClassType type) {
        return isType(type, ClientRpc.class);
    }

    private static boolean isServerRpc(JClassType type) {
        return isType(type, ServerRpc.class);
    }

    public static boolean isConnectedConnector(JClassType type) {
        return isConnected(type) && isType(type, ServerConnector.class);
    }

    private static boolean isConnected(JClassType type) {
        return type.isAnnotationPresent(Connect.class);
    }

    public static boolean isConnectedComponentConnector(JClassType type) {
        return isConnected(type) && isType(type, ComponentConnector.class);
    }

    private static boolean isType(JClassType type, Class<?> class1) {
        try {
            return type.getOracle().getType(class1.getName())
                    .isAssignableFrom(type);
        } catch (NotFoundException e) {
            throw new RuntimeException("Could not find " + class1.getName(), e);
        }
    }

}