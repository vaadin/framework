/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

public final class UIDL extends JavaScriptObject {

    protected UIDL() {
    }

    public String getId() {
        return getStringAttribute("id");
    }

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

    public String getStringAttribute(String name) {
        return attr().getString(name);
    }

    public Set<String> getAttributeNames() {
        Set<String> keySet = attr().getKeySet();
        keySet.remove("v");
        return keySet;
    }

    public Set<String> getVariableNames() {
        if (!hasVariables()) {
            return new HashSet<String>();
        } else {
            Set<String> keySet = var().getKeySet();
            return keySet;
        }
    }

    public int getIntAttribute(String name) {
        return attr().getInt(name);
    }

    public long getLongAttribute(String name) {
        return (long) attr().getRawNumber(name);
    }

    public float getFloatAttribute(String name) {
        return (float) attr().getRawNumber(name);
    }

    public double getDoubleAttribute(String name) {
        return attr().getRawNumber(name);
    }

    public boolean getBooleanAttribute(String name) {
        return attr().getBoolean(name);
    }

    public ValueMap getMapAttribute(String name) {
        return attr().getValueMap(name);
    }

    public String[] getStringArrayAttribute(String name) {
        return attr().getStringArray(name);
    }

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

    public boolean hasAttribute(final String name) {
        return attr().containsKey(name);
    }

    public native UIDL getChildUIDL(int i)
    /*-{
        return this[i + 2];
    }-*/;

    public native String getChildString(int i)
    /*-{
        return this[i + 2];
    }-*/;

    private native XML getChildXML(int index)
    /*-{
        return this[index + 2];
    }-*/;

    public Iterator<Object> getChildIterator() {

        return new Iterator<Object>() {

            int index = -1;

            public void remove() {
                throw new UnsupportedOperationException();
            }

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
     * 
     * @return
     * 
     * @deprecated
     */
    @Deprecated
    public String getChildrenAsXML() {
        return toString();
    }

    public boolean hasVariable(String name) {
        return var().containsKey(name);
    }

    public String getStringVariable(String name) {
        return var().getString(name);
    }

    public int getIntVariable(String name) {
        return var().getInt(name);
    }

    public long getLongVariable(String name) {
        return (long) var().getRawNumber(name);
    }

    public float getFloatVariable(String name) {
        return (float) var().getRawNumber(name);
    }

    public double getDoubleVariable(String name) {
        return var().getRawNumber(name);
    }

    public boolean getBooleanVariable(String name) {
        return var().getBoolean(name);
    }

    public String[] getStringArrayVariable(String name) {
        return var().getStringArray(name);
    }

    public Set<String> getStringArrayVariableAsSet(final String name) {
        final HashSet<String> s = new HashSet<String>();
        JsArrayString a = var().getJSStringArray(name);
        for (int i = 0; i < a.length(); i++) {
            s.add(a.get(i));
        }
        return s;
    }

    public int[] getIntArrayVariable(String name) {
        return var().getIntArray(name);
    }

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
                buf.push("tag");
                buf.push(">");
            }
            return buf.join();
        }-*/;
    }

    public native int getChildCount()
    /*-{
        return this.length - 2;
    }-*/;

    public native UIDL getErrors()
    /*-{
        return this[1]['error']; 
    }-*/;

    native boolean isMapAttribute(String name)
    /*-{
        return typeof this[1][name] == "object";
    }-*/;

}
