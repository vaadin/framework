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

package com.vaadin.client;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.vaadin.server.PaintTarget;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;

/**
 * When a component is updated, it's client side widget's
 * {@link ComponentConnector#updateFromUIDL(UIDL, ApplicationConnection)
 * updateFromUIDL()} will be called with the updated ("changes") UIDL received
 * from the server.
 * <p>
 * UIDL is hierarchical, and there are a few methods to retrieve the children,
 * {@link #getChildCount()}, {@link #getChildIterator()}
 * {@link #getChildString(int)}, {@link #getChildUIDL(int)}.
 * </p>
 * <p>
 * It can be helpful to keep in mind that UIDL was originally modeled in XML, so
 * it's structure is very XML -like. For instance, the first to children in the
 * underlying UIDL representation will contain the "tag" name and attributes,
 * but will be skipped by the methods mentioned above.
 * </p>
 */
public final class UIDL extends JavaScriptObject {

    protected UIDL() {
    }

    /**
     * Shorthand for getting the attribute named "id", which for Paintables is
     * the essential paintableId which binds the server side component to the
     * client side widget.
     * 
     * @return the value of the id attribute, if available
     */
    public String getId() {
        return getStringAttribute("id");
    }

    /**
     * Gets the name of this UIDL section, as created with
     * {@link PaintTarget#startTag(String) PaintTarget.startTag()} in the
     * server-side {@link Component#paint(PaintTarget) Component.paint()} or
     * (usually) {@link AbstractComponent#paintContent(PaintTarget)
     * AbstractComponent.paintContent()}. Note that if the UIDL corresponds to a
     * Paintable, a component identifier will be returned instead - this is used
     * internally and is not needed within
     * {@link ComponentConnector#updateFromUIDL(UIDL, ApplicationConnection)
     * updateFromUIDL()}.
     * 
     * @return the name for this section
     */
    public native String getTag()
    /*-{
        return this[0];
    }-*/;

    private native ValueMap attr()
    /*-{
        return this[1];
    }-*/;

    private native ValueMap var()
    /*-{
        return this[1]["v"];
    }-*/;

    private native boolean hasVariables()
    /*-{
        return Boolean(this[1]["v"]);
    }-*/;

    /**
     * Gets the named attribute as a String.
     * 
     * @param name
     *            the name of the attribute to get
     * @return the attribute value
     */
    public String getStringAttribute(String name) {
        return attr().getString(name);
    }

    /**
     * Gets the names of the attributes available.
     * 
     * @return the names of available attributes
     */
    public Set<String> getAttributeNames() {
        Set<String> keySet = attr().getKeySet();
        keySet.remove("v");
        return keySet;
    }

    /**
     * Gets the names of variables available.
     * 
     * @return the names of available variables
     */
    public Set<String> getVariableNames() {
        if (!hasVariables()) {
            return new HashSet<String>();
        } else {
            Set<String> keySet = var().getKeySet();
            return keySet;
        }
    }

    /**
     * Gets the named attribute as an int.
     * 
     * @param name
     *            the name of the attribute to get
     * @return the attribute value
     */
    public int getIntAttribute(String name) {
        return attr().getInt(name);
    }

    /**
     * Gets the named attribute as a long.
     * 
     * @param name
     *            the name of the attribute to get
     * @return the attribute value
     */
    public long getLongAttribute(String name) {
        return (long) attr().getRawNumber(name);
    }

    /**
     * Gets the named attribute as a float.
     * 
     * @param name
     *            the name of the attribute to get
     * @return the attribute value
     */
    public float getFloatAttribute(String name) {
        return (float) attr().getRawNumber(name);
    }

    /**
     * Gets the named attribute as a double.
     * 
     * @param name
     *            the name of the attribute to get
     * @return the attribute value
     */
    public double getDoubleAttribute(String name) {
        return attr().getRawNumber(name);
    }

    /**
     * Gets the named attribute as a boolean.
     * 
     * @param name
     *            the name of the attribute to get
     * @return the attribute value
     */
    public boolean getBooleanAttribute(String name) {
        return attr().getBoolean(name);
    }

    /**
     * Gets the named attribute as a Map of named values (key/value pairs).
     * 
     * @param name
     *            the name of the attribute to get
     * @return the attribute Map
     */
    public ValueMap getMapAttribute(String name) {
        return attr().getValueMap(name);
    }

    /**
     * Gets the named attribute as an array of Strings.
     * 
     * @param name
     *            the name of the attribute to get
     * @return the attribute value
     */
    public String[] getStringArrayAttribute(String name) {
        return attr().getStringArray(name);
    }

    /**
     * Gets the named attribute as an int array.
     * 
     * @param name
     *            the name of the attribute to get
     * @return the attribute value
     */
    public int[] getIntArrayAttribute(final String name) {
        return attr().getIntArray(name);
    }

    /**
     * Get attributes value as string whatever the type is
     * 
     * @param name
     * @return string presentation of attribute
     */
    native String getAttribute(String name)
    /*-{
        return '' + this[1][name];
    }-*/;

    native String getVariable(String name)
    /*-{
        return '' + this[1]['v'][name];
    }-*/;

    /**
     * Indicates whether or not the named attribute is available.
     * 
     * @param name
     *            the name of the attribute to check
     * @return true if the attribute is available, false otherwise
     */
    public boolean hasAttribute(final String name) {
        return attr().containsKey(name);
    }

    /**
     * Gets the UIDL for the child at the given index.
     * 
     * @param i
     *            the index of the child to get
     * @return the UIDL of the child if it exists
     */
    public native UIDL getChildUIDL(int i)
    /*-{
        return this[i + 2];
    }-*/;

    /**
     * Gets the child at the given index as a String.
     * 
     * @param i
     *            the index of the child to get
     * @return the String representation of the child if it exists
     */
    public native String getChildString(int i)
    /*-{
        return this[i + 2];
    }-*/;

    private native XML getChildXML(int index)
    /*-{
        return this[index + 2];
    }-*/;

    /**
     * Gets an iterator that can be used to iterate trough the children of this
     * UIDL.
     * <p>
     * The Object returned by <code>next()</code> will be appropriately typed -
     * if it's UIDL, {@link #getTag()} can be used to check which section is in
     * question.
     * </p>
     * <p>
     * The basic use case is to iterate over the children of an UIDL update, and
     * update the appropriate part of the widget for each child encountered, e.g
     * if <code>getTag()</code> returns "color", one would update the widgets
     * color to reflect the value of the "color" section.
     * </p>
     * 
     * @return an iterator for iterating over UIDL children
     */
    public Iterator<Object> getChildIterator() {

        return new Iterator<Object>() {

            int index = -1;

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Object next() {

                if (hasNext()) {
                    int typeOfChild = typeOfChild(++index);
                    switch (typeOfChild) {
                    case CHILD_TYPE_UIDL:
                        UIDL childUIDL = getChildUIDL(index);
                        return childUIDL;
                    case CHILD_TYPE_STRING:
                        return getChildString(index);
                    case CHILD_TYPE_XML:
                        return getChildXML(index);
                    default:
                        throw new IllegalStateException(
                                "Illegal child  in tag " + getTag()
                                        + " at index " + index);
                    }
                }
                return null;
            }

            @Override
            public boolean hasNext() {
                int count = getChildCount();
                return count > index + 1;
            }

        };
    }

    private static final int CHILD_TYPE_STRING = 0;
    private static final int CHILD_TYPE_UIDL = 1;
    private static final int CHILD_TYPE_XML = 2;

    private native int typeOfChild(int index)
    /*-{
        var t = typeof this[index + 2];
        if(t == "object") {
            if(typeof(t.length) == "number") {
                return 1;
            } else {
                return 2;
            }
        } else if (t == "string") {
            return 0;
        }
        return -1;
    }-*/;

    /**
     * @deprecated
     */
    @Deprecated
    public String getChildrenAsXML() {
        return toString();
    }

    /**
     * Checks if the named variable is available.
     * 
     * @param name
     *            the name of the variable desired
     * @return true if the variable exists, false otherwise
     */
    public boolean hasVariable(String name) {
        return hasVariables() && var().containsKey(name);
    }

    /**
     * Gets the value of the named variable.
     * 
     * @param name
     *            the name of the variable
     * @return the value of the variable
     */
    public String getStringVariable(String name) {
        return var().getString(name);
    }

    /**
     * Gets the value of the named variable.
     * 
     * @param name
     *            the name of the variable
     * @return the value of the variable
     */
    public int getIntVariable(String name) {
        return var().getInt(name);
    }

    /**
     * Gets the value of the named variable.
     * 
     * @param name
     *            the name of the variable
     * @return the value of the variable
     */
    public long getLongVariable(String name) {
        return (long) var().getRawNumber(name);
    }

    /**
     * Gets the value of the named variable.
     * 
     * @param name
     *            the name of the variable
     * @return the value of the variable
     */
    public float getFloatVariable(String name) {
        return (float) var().getRawNumber(name);
    }

    /**
     * Gets the value of the named variable.
     * 
     * @param name
     *            the name of the variable
     * @return the value of the variable
     */
    public double getDoubleVariable(String name) {
        return var().getRawNumber(name);
    }

    /**
     * Gets the value of the named variable.
     * 
     * @param name
     *            the name of the variable
     * @return the value of the variable
     */
    public boolean getBooleanVariable(String name) {
        return var().getBoolean(name);
    }

    /**
     * Gets the value of the named variable.
     * 
     * @param name
     *            the name of the variable
     * @return the value of the variable
     */
    public String[] getStringArrayVariable(String name) {
        return var().getStringArray(name);
    }

    /**
     * Gets the value of the named String[] variable as a Set of Strings.
     * 
     * @param name
     *            the name of the variable
     * @return the value of the variable
     */
    public Set<String> getStringArrayVariableAsSet(final String name) {
        final HashSet<String> s = new HashSet<String>();
        JsArrayString a = var().getJSStringArray(name);
        for (int i = 0; i < a.length(); i++) {
            s.add(a.get(i));
        }
        return s;
    }

    /**
     * Gets the value of the named variable.
     * 
     * @param name
     *            the name of the variable
     * @return the value of the variable
     */
    public int[] getIntArrayVariable(String name) {
        return var().getIntArray(name);
    }

    /**
     * @deprecated should not be used anymore
     */
    @Deprecated
    public final static class XML extends JavaScriptObject {
        protected XML() {
        }

        public native String getXMLAsString()
        /*-{
            var buf = new Array();
            var self = this;
            for(j in self) {
                buf.push("<");
                buf.push(j);
                buf.push(">");
                buf.push(self[j]);
                buf.push("</");
                buf.push(j);
                buf.push(">");
            }
            return buf.join("");
        }-*/;
    }

    /**
     * Returns the number of children.
     * 
     * @return the number of children
     */
    public native int getChildCount()
    /*-{
        return this.length - 2;
    }-*/;

    native boolean isMapAttribute(String name)
    /*-{
        return typeof this[1][name] == "object";
    }-*/;

    /**
     * Gets the Paintable with the id found in the named attributes's value.
     * 
     * @param name
     *            the name of the attribute
     * @return the Paintable referenced by the attribute, if it exists
     */
    public ServerConnector getPaintableAttribute(String name,
            ApplicationConnection connection) {
        return ConnectorMap.get(connection).getConnector(
                getStringAttribute(name));
    }

    /**
     * Gets the Paintable with the id found in the named variable's value.
     * 
     * @param name
     *            the name of the variable
     * @return the Paintable referenced by the variable, if it exists
     */
    public ServerConnector getPaintableVariable(String name,
            ApplicationConnection connection) {
        return ConnectorMap.get(connection).getConnector(
                getStringVariable(name));
    }

    /**
     * Returns the child UIDL by its name. If several child nodes exist with the
     * given name, the first child UIDL will be returned.
     * 
     * @param tagName
     * @return the child UIDL or null if child wit given name was not found
     */
    public UIDL getChildByTagName(String tagName) {
        Iterator<Object> childIterator = getChildIterator();
        while (childIterator.hasNext()) {
            Object next = childIterator.next();
            if (next instanceof UIDL) {
                UIDL childUIDL = (UIDL) next;
                if (childUIDL.getTag().equals(tagName)) {
                    return childUIDL;
                }
            }
        }
        return null;
    }

}
