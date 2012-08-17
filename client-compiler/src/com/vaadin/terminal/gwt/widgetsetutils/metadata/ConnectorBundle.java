/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.widgetsetutils.metadata;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.NotFoundException;

public class ConnectorBundle {
    private final String name;
    private final ConnectorBundle previousBundle;

    private final Set<JClassType> needsGwtConstructor = new HashSet<JClassType>();
    private final Map<JClassType, Set<String>> identifiers = new HashMap<JClassType, Set<String>>();
    private final Set<JClassType> visitedTypes = new HashSet<JClassType>();
    private final Set<JClassType> visitQueue = new HashSet<JClassType>();
    private final Map<JClassType, Set<JMethod>> needsReturnType = new HashMap<JClassType, Set<JMethod>>();

    private boolean visiting = false;

    public ConnectorBundle(String name, ConnectorBundle previousBundle) {
        this.name = name;
        this.previousBundle = previousBundle;
    }

    public void setNeedsGwtConstructor(JClassType type) {
        if (!needsGwtConstructor(type)) {
            if (!isTypeVisited(type)) {
                visitQueue.add(type);
            }
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
            if (!isTypeVisited(type)) {
                visitQueue.add(type);
            }
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

    public void visitTypes(TreeLogger logger, Collection<JClassType> types,
            Collection<TypeVisitor> visitors) throws UnableToCompleteException {
        for (JClassType type : types) {
            if (!isTypeVisited(type)) {
                visitQueue.add(type);
            }
        }
        visitQueue(logger, visitors);
    }

    private boolean isTypeVisited(JClassType type) {
        if (visitedTypes.contains(type)) {
            return true;
        } else {
            return previousBundle != null && previousBundle.isTypeVisited(type);
        }
    }

    private void visitQueue(TreeLogger logger, Collection<TypeVisitor> visitors)
            throws UnableToCompleteException {
        while (!visitQueue.isEmpty()) {
            JClassType type = visitQueue.iterator().next();
            for (TypeVisitor typeVisitor : visitors) {
                try {
                    typeVisitor.visit(type, this);
                } catch (NotFoundException e) {
                    logger.log(Type.ERROR, e.getMessage(), e);
                    throw new UnableToCompleteException();
                }
            }
            visitQueue.remove(type);
            visitedTypes.add(type);
        }
    }

    public void visitSubTypes(TreeLogger logger, JClassType type,
            Collection<TypeVisitor> visitors) throws UnableToCompleteException {
        visitTypes(logger, Arrays.asList(type.getSubtypes()), visitors);
    }

    public void setNeedsReturnType(JClassType type, JMethod method) {
        if (!isNeedsReturnType(type, method)) {
            if (!isTypeVisited(type)) {
                visitQueue.add(type);
            }
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
}