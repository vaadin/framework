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

import com.google.gwt.core.ext.typeinfo.JClassType;

public class ConnectorBundle {
    private final String name;
    private final ConnectorBundle previousBundle;

    private final Set<JClassType> needsGwtConstructor = new HashSet<JClassType>();
    private final Map<JClassType, Set<String>> identifiers = new HashMap<JClassType, Set<String>>();
    private final Set<JClassType> visitedTypes = new HashSet<JClassType>();
    private final Set<JClassType> visitQueue = new HashSet<JClassType>();

    public ConnectorBundle(String name, ConnectorBundle previousBundle) {
        this.name = name;
        this.previousBundle = previousBundle;
    }

    public void setNeedsGwtConstructor(JClassType type) {
        if (!needsGwtConstructor(type)) {
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

    public void visitTypes(Collection<JClassType> types,
            Collection<TypeVisitor> visitors) {
        for (JClassType type : types) {
            if (!isTypeVisited(type)) {
                visitQueue.add(type);
            }
        }
        visitQueue(visitors);
    }

    private boolean isTypeVisited(JClassType type) {
        if (visitedTypes.contains(type)) {
            return true;
        } else {
            return previousBundle != null
                    && previousBundle.isTypeVisited(type);
        }
    }

    private void visitQueue(Collection<TypeVisitor> visitors) {
        while (!visitQueue.isEmpty()) {
            JClassType type = visitQueue.iterator().next();
            for (TypeVisitor typeVisitor : visitors) {
                typeVisitor.visit(type, this);
            }
            visitQueue.remove(type);
            visitedTypes.add(type);
        }
    }

    public void visitSubTypes(JClassType type, Collection<TypeVisitor> visitors) {
        visitTypes(Arrays.asList(type.getSubtypes()), visitors);
    }
}